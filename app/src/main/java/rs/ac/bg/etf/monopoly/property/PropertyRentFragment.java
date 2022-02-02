package rs.ac.bg.etf.monopoly.property;

import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

import rs.ac.bg.etf.monopoly.GameModel;
import rs.ac.bg.etf.monopoly.MainActivity;
import rs.ac.bg.etf.monopoly.MyApplication;
import rs.ac.bg.etf.monopoly.R;
import rs.ac.bg.etf.monopoly.databinding.FragmentPropertyRentBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Property;
import rs.ac.bg.etf.monopoly.db.Repository;


public class PropertyRentFragment extends Fragment {

    private FragmentPropertyRentBinding amb;
    private GameModel model;
    private PropertyModel propertyModel;
    private MainActivity activity;
    private NavController controller;
    private Repository repo;

    public PropertyRentFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= (MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        repo=new Repository(activity,db.getDaoProperty(), db.getDaoPlayer(), db.getDaoCard(),db.getDaoGame());
        model= GameModel.getModel(repo,activity);
        propertyModel=PropertyModel.getModel(repo,activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentPropertyRentBinding.inflate(inflater,container,false);
        TypedArray images=getResources().obtainTypedArray(R.array.images_details);
        PropertyRentFragmentArgs args=PropertyRentFragmentArgs.fromBundle(getArguments());
        amb.posed.setImageDrawable(images.getDrawable(args.getIndex()));
        images.recycle();
        amb.topAppBar.setNavigationOnClickListener(e->{
            controller.navigateUp();
        });

        repo.getProperty(args.getIndex()).observe(getViewLifecycleOwner(),e->{
            if(amb.nekretnine.getChildCount()>1)
                amb.nekretnine.removeViews(0,amb.nekretnine.getChildCount());
            initText(e);
            if(e.getHouses()>4){
                //izbaci kuce
                ImageView v=new ImageView(activity);
                v.setImageDrawable(getResources().getDrawable(R.drawable.outline_apartment_24, activity.getTheme()));

                v.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_red_dark),
                        PorterDuff.Mode.MULTIPLY);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.setTooltipText(getString(R.string.hotel));
                }
                amb.nekretnine.addView(v);
            }
            else{
                for(int i=0;i<e.getHouses();i++){
                    ImageView v=new ImageView(activity);
                    v.setImageDrawable(activity.getDrawable(R.drawable.outline_home_24));
                    v.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_dark),
                            PorterDuff.Mode.MULTIPLY);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.setTooltipText(getString(R.string.kuca));
                    }
                    amb.nekretnine.addView(v);
                }
            }
            amb.nekretnine.setGravity(Gravity.CENTER);


        });

        Handler h=new Handler(Looper.getMainLooper());

        if(!model.isAbleToBuy()||model.isBought()){
            amb.plati.setEnabled(false);
        }
        else{
            amb.plati.setEnabled(true);
            model.setPaid(false);
        }

        amb.plati.setOnClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                Property p=propertyModel.getPropertyBlocking(args.getIndex());
                Player player=model.getPlayer(args.getUser());
                Player owner=model.getPlayer(p.getHolder());
                int price=0;
                if(p.getType()==0) price=calculate(p);
                else price=calculateStation(p);
                if(price<=player.getMoney()){
                    owner.setMoney(owner.getMoney()+price);
                    model.update(owner);
                    player.setMoney(player.getMoney()-price);
                    model.update(player);
                    h.post(()->{
                        controller.navigateUp();
                    });
                    model.setBought(true);
                    model.setPaid(true);
                }
                else{
                    if(propertyModel.isBankruptcy(player.getIndex(),price,player.getMoney())){
                        player.setMoney(-1);
                        model.update(player);
                        model.setPaid(true);
                        h.post(()->Toast.makeText(activity,"Bankrotirali ste!",Toast.LENGTH_SHORT).show());
                        List<Player> players=model.getAllPlayers();
                        if(players.stream().filter(pp->{
                            return pp.getMoney()!=-1;
                        }).count()==1){
                            Player winner=players.stream().filter(pp->{
                                return pp.getMoney()!=-1;
                            }).findFirst().orElse(null);
                            model.finishGame();
                            h.post(()-> {
                                AlertDialog dd=new MaterialAlertDialogBuilder(activity)
                                        .setTitle("Igra je zavrsena!")
                                        .setMessage("Pobednik je "+winner.getName())
                                        .setPositiveButton((CharSequence) "Return to home", (dialog, which) -> {
                                            dialog.cancel();
                                            controller.popBackStack();
                                            controller.navigateUp();
                                        }).create();
                                dd.setCanceledOnTouchOutside(false);
                                dd.show();
                            });
                        }
                        else h.post(()->controller.navigateUp());

                    }
                    else{
                        h.post(()->Toast.makeText(activity,"Nemate dovoljno novca!",Toast.LENGTH_SHORT).show());
                    }

                }
            });
        });
        return amb.getRoot();
    }



    private void initText(Property e){
        if(e.getType()==0){
            amb.bez.setText("Bez nekretnina "+"("+e.getRent_l0()+"M)");
            amb.kuca1.setText("Jedna kuća "+"("+e.getRent_l1()+"M)");
            amb.kuca2.setText("Dve kuće "+"("+e.getRent_l2()+"M)");
            amb.kuca3.setText("Tri kuće "+"("+e.getRent_l3()+"M)");
            amb.kuca4.setText("Četiri kuće "+"("+e.getRent_l4()+"M)");
            amb.hotel.setText("Hotel "+"("+e.getRent_l5()+"M)");
            amb.plati.setText(amb.plati.getText() + "("+calculate(e)+"M)");
        }
        else{
            amb.kuca1.setText("Jedna stanica "+"("+e.getRent_l0()+"M)");
            amb.kuca2.setText("Dve stanice"+"("+e.getRent_l1()+"M)");
            amb.kuca3.setText("Tri stanice"+"("+e.getRent_l2()+"M)");
            amb.kuca4.setText("Cetiri stanice"+"("+e.getRent_l3()+"M)");
            printStationPrices(e);
            amb.sadrzaj.removeView(amb.hotel);
            amb.sadrzaj.removeView(amb.bez);
        }
    }

    private int calculate(Property e) {
        if(e.getHouses()==0) return e.getRent_l0();
        if(e.getHouses()==1) return e.getRent_l1();
        if(e.getHouses()==2) return e.getRent_l2();
        if(e.getHouses()==3) return e.getRent_l3();
        if(e.getHouses()==4) return e.getRent_l4();
        if(e.getHouses()==5) return e.getRent_l5();
        return -1;
    }

    private void printStationPrices(Property e){
        repo.getTypeOfHolder(e.getHolder(),e.getType()).observe(getViewLifecycleOwner(),p->{
            if(p.size()==1) amb.plati.setText(amb.plati.getText() + "("+e.getRent_l0()+"M)");
            if(p.size()==2) amb.plati.setText(amb.plati.getText() + "("+e.getRent_l1()+"M)");
            if(p.size()==3) amb.plati.setText(amb.plati.getText() + "("+e.getRent_l2()+"M)");
            if(p.size()==4) amb.plati.setText(amb.plati.getText() + "("+e.getRent_l3()+"M)");
        });
    }

    private int calculateStation(Property e){
        int p=repo.getTypeOfHolderBlocking(e.getHolder(),e.getType()).size();
        if(p==1) return e.getRent_l0();
        if(p==2) return e.getRent_l1();
        if(p==3) return e.getRent_l2();
        if(p==4) return e.getRent_l3();
        return -1;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}