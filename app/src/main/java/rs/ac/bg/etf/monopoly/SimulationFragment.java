package rs.ac.bg.etf.monopoly;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import rs.ac.bg.etf.monopoly.databinding.FragmentSimulationBinding;
import rs.ac.bg.etf.monopoly.db.Card;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Move;
import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Property;
import rs.ac.bg.etf.monopoly.db.Repository;
import rs.ac.bg.etf.monopoly.property.PropertyModel;


public class SimulationFragment extends Fragment {

    private FragmentSimulationBinding amb;
    private GameModel model;
    private PropertyModel propertyModel;
    private MainActivity activity;
    private NavController controller;
    private Repository repo;
    private CardModel cardModel;

    public SimulationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= (MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        repo= new Repository(activity,db.getDaoProperty(),
                db.getDaoPlayer(), db.getDaoCard(),
                db.getDaoGame(),db.getDaoMove(),db.getSellingDao());
        model= GameModel.getModel(repo,activity);
        cardModel=CardModel.getModel(repo,activity);
        propertyModel=PropertyModel.getModel(repo,activity);

    }
    private Handler h;

    private List<Property> properties;
    private List<Player> players;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentSimulationBinding.inflate(inflater,container,false);
        amb.topAppBar.setNavigationOnClickListener(e->{
            controller.navigateUp();
        });
        SimulationFragmentArgs args=SimulationFragmentArgs.fromBundle(getArguments());
        int game=args.getGame();

        h=new Handler(Looper.getMainLooper());

        amb.simulate.setOnClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                repo.initProperties(false);
                model.setMoneyFromTaxes(0);
                List<Move> moves=model.getMoves(game);
                model.setCurrentGame(game);
                players=model.getAllPlayers();

                players.forEach(p->{
                    p.setMoney(1500);
                    p.setPrison(0);
                    h.post(()->{
                        amb.board.clearFilter(p.getPosition());
                       amb.board.useFilter(0,p.getIndex());
                       amb.board.invalidate();
                        p.setPosition(0);
                    });
                    //model.update(p);

                });


                moves.forEach(move->{
                    Player player=players.stream().filter(p->{
                        return p.getIndex()==move.getPlayer();
                    }).findFirst().orElse(null);
                    if(player.getPrison()>0)player.setPrison(0);
                    AtomicReference<Integer> finish =new AtomicReference<>(0);
                    moveOnBoard(finish,player,move.getPositionTo(),player.getPosition(),players);
                    if(move.getCardOpen()!=-1){
                        Card card=cardModel.getCard(move.getCardOpen());
                        execute(card,player);
                    }
                    else if(move.getPositionTo()==30){
                        if(player.getPrison()<0) player.setPrison(player.getPrison()+1);
                        else{
                            SystemClock.sleep(2000);
                            player.setPrison(2);
                            player.setPosition(10);
                            AtomicReference<Integer> finish2 =new AtomicReference<>(0);
                            moveOnBoard(finish2,player,10,30,players);
                        }
                    }
                    synchronized (finish){
                        while(finish.get()==0) {
                            try {
                                finish.wait();
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                        //model.update(player);
                        SystemClock.sleep(2000);
                    }
                });
                h.post(()->{
                    Toast.makeText(activity,"Simulacija zavrsena",Toast.LENGTH_SHORT).show();
                });
            });
        });

        return amb.getRoot();
    }

    private void moveOnBoard(AtomicReference<Integer> finish, Player player, int position, int oldPosition, List<Player>players){

        h.post(()->{
            amb.board.clearFilter(oldPosition);
            player.setPosition(position);
            players.forEach(p->{
                amb.board.useFilter(p.getPosition(),p.getIndex());
            });
            amb.board.invalidate();
            finish.set(1);
            synchronized (finish){
                finish.notify();
            }

        });
    }

    private void execute(Card card, Player p) {
        h.post(()->Toast.makeText(activity,"Otvorena je kartica "+card.getId()+": "+card.getMessage(),Toast.LENGTH_SHORT).show());
        int oldPosition=p.getPosition();
        if(card.getPrison()!=-1){
            prisonExecute(card,p);
        }
        else if(card.getMovement()!=-1){
            movementExecute(card,p);
        }
        if(oldPosition!=p.getPosition()){
            AtomicReference<Integer> finish =new AtomicReference<>(0);
            moveOnBoard(finish,p,p.getPosition(),oldPosition,players);
            synchronized (finish){
                while(finish.get()==0) {
                    try {
                        finish.wait();
                    } catch (InterruptedException interruptedException) {
                        interruptedException.printStackTrace();
                    }
                }
                SystemClock.sleep(2000);
            }
        }


    }


    private void movementExecute(Card card, Player p) {
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
            p.setPosition(pos);
        }
    }

    private void prisonExecute(Card card, Player p) {
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
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}