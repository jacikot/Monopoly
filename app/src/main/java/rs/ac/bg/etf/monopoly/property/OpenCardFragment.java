package rs.ac.bg.etf.monopoly.property;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import rs.ac.bg.etf.monopoly.CardModel;
import rs.ac.bg.etf.monopoly.GameModel;
import rs.ac.bg.etf.monopoly.MainActivity;
import rs.ac.bg.etf.monopoly.MyApplication;
import rs.ac.bg.etf.monopoly.R;
import rs.ac.bg.etf.monopoly.databinding.FragmentOpenCardBinding;
import rs.ac.bg.etf.monopoly.db.Card;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Property;
import rs.ac.bg.etf.monopoly.db.Repository;


public class OpenCardFragment extends Fragment {

    private FragmentOpenCardBinding amb;
    private GameModel model;
    private PropertyModel propertyModel;
    private CardModel cardModel;
    private MainActivity activity;
    private NavController controller;
    private Repository repo;


    public OpenCardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= (MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        repo=new Repository(activity,db.getDaoProperty(), db.getDaoPlayer(), db.getDaoCard());
        model= GameModel.getModel(repo,activity);
        propertyModel=PropertyModel.getModel(repo,activity);
        cardModel=CardModel.getModel(repo,activity);
    }

    Handler h=new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb= FragmentOpenCardBinding.inflate(inflater,container,false);
        TypedArray images=getResources().obtainTypedArray(R.array.images_details);
        OpenCardFragmentArgs args=OpenCardFragmentArgs.fromBundle(getArguments());
        amb.posed.setImageDrawable(images.getDrawable(args.getIndex()));
        images.recycle();

        if(model.isPaid()){
            amb.transakcija.setEnabled(false);
        }
        else amb.transakcija.setEnabled(true);

        if(model.isBought()||!model.isAbleToBuy()){
            amb.otvori.setEnabled(false);
            amb.transakcija.setEnabled(false);
        }



        model.getCardOpen().observe(getViewLifecycleOwner(),e->{
            if(e==null){
                amb.otvori.setVisibility(View.VISIBLE);
                if(model.isAbleToBuy()) model.setPaid(false);
            }
            else{
                amb.poruka.setText(e.getMessage());
                amb.layout.removeView(amb.otvori);
                amb.transakcija.setVisibility(View.VISIBLE);
                if(!model.isPaid()) amb.transakcija.setEnabled(true);
            }
        });

        amb.otvori.setOnClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                Property p=propertyModel.getPropertyBlocking(args.getIndex());
                List<Card> cards=cardModel.getCardsType(p.getType()-3);
                int random=(int)(Math.random()*cards.size());
//                int random=0;
                model.setCardOpen(cards.get(random));
            });
        });

        amb.transakcija.setOnClickListener(e->{
            model.getCardOpen().observe(getViewLifecycleOwner(),c->{
                ((MyApplication)activity.getApplication()).getExecutorService().execute(()->execute(c, args.getUser()));
            });

        });

        amb.layout.setGravity(Gravity.CENTER);
        amb.bottomLayout.setGravity(Gravity.CENTER);


        return amb.getRoot();
    }

    private void execute(Card card, int user) {
        if(card.getPrison()!=-1){
            model.setPaid(true);
            prisonExecute(card,user);
        }
        else if(card.getMovement()!=-1){
            model.setPaid(true);
            movementExecute(card,user);
        }
        else {
            paymentExecute(card,user);
        }
    }

    private void paymentExecute(Card card, int user) {
        Player p=model.getPlayer(user);
        int toPay=0;
        switch (card.getPaymentType()){
            case 1:
                toPay=card.getMoney();
                break;
            case 2:
                List<Property> list=propertyModel.getTypeOfHolderBlocking(user,0);
                for(Property propery:list) toPay+=propery.getHouses()*card.getMoney();
                break;
            case 3:
                toPay=(model.getUserCount()-1)*card.getMoney();
                break;
        }
        if(p.getMoney()+toPay>=0){
            if(card.getPaymentType()<3){
                if(toPay<0)model.setMoneyFromTaxes(model.getMoneyFromTaxes()-toPay);
            }
            else{
                List<Player> players=model.getAllPlayers();
                for(Player player:players){
                    if(player.getIndex()==p.getIndex()) continue;
                    if(player.getMoney()>=card.getMoney()){
                        player.setMoney(player.getMoney()-card.getMoney());
                    }
                    else {
                        toPay-=card.getMoney()-player.getMoney();
                        player.setMoney(0);
                    }
                    model.update(player);
                }
            }
            p.setMoney(p.getMoney()+ toPay);
            model.update(p);
            model.setPaid(true);
            h.post(()->controller.navigateUp());
        }
        else{
            if(propertyModel.isBankruptcy(p.getIndex(),-toPay,p.getMoney())){
                p.setMoney(-1);
                model.update(p);
                model.setPaid(true);
                h.post(()->Toast.makeText(activity,"Bankrotirali ste!",Toast.LENGTH_SHORT).show());
                List<Player> players=model.getAllPlayers();
                if(players.stream().filter(e->{
                    return e.getMoney()!=-1;
                }).count()==1){
                    Player winner=players.stream().filter(e->{
                        return e.getMoney()!=-1;
                    }).findFirst().orElse(null);
                    h.post(()->new MaterialAlertDialogBuilder(activity)
                            .setTitle("Igra je zavrsena!")
                            .setMessage("Pobednik je "+winner.getName())
                            .setPositiveButton((CharSequence) "Return to home", (dialog, which) -> {
                                dialog.cancel();
                                controller.popBackStack();
                                controller.navigateUp();
                            }).show());
                }
                else h.post(()->controller.navigateUp());

            }
            else{
                h.post(()->Toast.makeText(activity,"Nemate dovoljno novca!",Toast.LENGTH_SHORT).show());
            }

        }
    }

    private void movementExecute(Card card, int user) {
        Player p=model.getPlayer(user);
        Property to;
        if(card.getMovement()==1|card.getMovement()==2){
            List<Property> properties=propertyModel.getOfType(card.getMovement());
            to=properties.stream().min((e1,e2)->{
                int dst1=(e1.getId()-p.getPosition())+((e1.getId()>p.getPosition())?0:40);
                int dst2=(e2.getId()-p.getPosition())+((e2.getId()>p.getPosition())?0:40);
                return dst1-dst2;
            }).get();
            p.setPosition(to.getId());
        }
        else{
            int pos=p.getPosition()+card.getMovement()+(p.getPosition()<card.getMovement()?40:0);
            to=propertyModel.getPropertyBlocking(pos);
            p.setPosition(pos);
        }
        model.setMoved(true);
        model.update(p);
        h.post(()->controller.navigateUp());
    }

    private void prisonExecute(Card card, int user) {
        Player p=model.getPlayer(user);
        if(card.getPrison()==1){
            //izadji besplatno
            p.setPrison(p.getPrison()-1);
        }
        if(card.getPrison()==2){
            if(p.getPrison()<0) p.setPrison(p.getPrison()+1);
            else{
                p.setPrison(2);
                p.setPosition(10);
            }
        }
        model.update(p);
        h.post(()->controller.navigateUp());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}