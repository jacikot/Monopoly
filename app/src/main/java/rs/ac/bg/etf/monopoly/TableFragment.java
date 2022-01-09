package rs.ac.bg.etf.monopoly;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import rs.ac.bg.etf.monopoly.databinding.FragmentTableBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Property;
import rs.ac.bg.etf.monopoly.db.Repository;
import rs.ac.bg.etf.monopoly.property.PropertyModel;
import rs.ac.bg.etf.monopoly.property.RouterUtility;


public class TableFragment extends Fragment {

    private FragmentTableBinding amb;
    private GameModel model;
    private PropertyModel propertyModel;
    private MainActivity activity;
    private NavController controller;
    private Timer timer;
    private Handler mainHanfler=new Handler(Looper.getMainLooper());


    public TableFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= (MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        Repository repo=new Repository(activity,db.getDaoProperty(),db.getDaoPlayer(), db.getDaoCard());
        propertyModel=PropertyModel.getModel(repo, activity);
        model=GameModel.getModel(repo,activity);
        ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
            int e=model.getNextGame();
            List<Player> list=new ArrayList<>();
            list.add(new Player(0,e,"Jana",100,0,0));
            list.add(new Player(1,e,"Lana",100,0,0));
            list.add(new Player(2,e,"Nana",100,0,0));
            list.add(new Player(3,e,"Gana",100,0,0));
            long gameDuration=60*2;
            model.startGame(list);
            mainHanfler.post(()->model.setFinalTime(new Date().getTime()+gameDuration*1000));
        });

        model.getFinalTime().observe(this,e->{
            if(e!=0) startTimer(e);
        });

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentTableBinding.inflate(inflater,container,false);
        TypedArray images=getResources().obtainTypedArray(R.array.ids);

        timer=new Timer();

        model.getTimeString().observe(getViewLifecycleOwner(),e->{
            amb.timer.setText(e);
        });
        for(int i=0;i<images.length();i++){
            int id=images.getResourceId(i,0);
            int index=i;
            int start=R.id.start;

            amb.getRoot().findViewById(id).setOnClickListener(e->{
                propertyModel.getProperty(index).observe(getViewLifecycleOwner(),k->{
                    ((MyApplication)activity.getApplication()).getExecutorService().execute(()-> {
                        Player p=model.getPlayer(model.getLastPlayer());
                        if(p.getPosition()!=index) model.setAbleToBuy(false);
                        else model.setAbleToBuy(true);
                        mainHanfler.post(()->RouterUtility.route(controller,k, model.getLastPlayer()));
                    });
                });
            });
        }

        if(model.isMoved()){
            model.setMoved(false);
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                Player p=model.getPlayer(model.getLastPlayer());
                Property property=propertyModel.getPropertyBlocking(p.getPosition());
                model.setAbleToBuy(true);
                SystemClock.sleep(4000);
                mainHanfler.post(()->{
                    RouterUtility.route(controller ,property, model.getLastPlayer());
                });
            });
        }

        String[]colors=getResources().getStringArray(R.array.colors);

        model.getPlayers().observe(getViewLifecycleOwner(),e->{
            TypedArray img=getResources().obtainTypedArray(R.array.ids);
            ImageView k=((ImageView)amb.getRoot().findViewById(img.getResourceId(model.getOldPossition(),0)));
            k.clearColorFilter();
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                for(int j=0;j<model.getUserCount();j++){
                    Player p=model.getPlayer(j);
                    int index=j;
                    if(p.getMoney()>=0)
                        mainHanfler.post(()->{
                            ((ImageView)amb.getRoot().findViewById(img.getResourceId(e.get(index).getPosition(),0))).setColorFilter(Color.parseColor(colors[index]),
                                    PorterDuff.Mode.MULTIPLY);
                        });

                }
                mainHanfler.post(()-> img.recycle());
            });


        });

        model.getDice1().observe(getViewLifecycleOwner(),e->{
            amb.dices.setText("Kockica 1: "+e);
        });

        model.getDice2().observe(getViewLifecycleOwner(),e->{
            amb.dices2.setText("Kockica 2: "+e);
        });

        amb.topAppBar.getMenu().getItem(0).setOnMenuItemClickListener(e->{
            if(model.isPaid()){
                ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                    Player p=model.rollTheDice(TableFragment.this);
                    Property property=propertyModel.getPropertyBlocking(p.getPosition());
                    model.setAbleToBuy(true);
                    SystemClock.sleep(4000);
                    mainHanfler.post(()->{
                        RouterUtility.route(controller ,property, model.getLastPlayer());
                    });
                });
            }
            else Toast.makeText(activity,"Niste platili dazbine!",Toast.LENGTH_SHORT).show();
           return true;
        });



        amb.topAppBar.getMenu().getItem(1).setOnMenuItemClickListener(e-> {
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                Player current= model.getPlayer(model.getLastPlayer());
                String msg="Na racunu imate: "+current.getMoney()+
                        "\nNovac sakupljen od poreza: "+model.getMoneyFromTaxes();
                mainHanfler.post(()->new MaterialAlertDialogBuilder(activity)
                        .setTitle("Novac")
                        .setMessage(msg)
                        .setNeutralButton((CharSequence) "Cancel", (dialog, which) -> {
                            dialog.cancel();
                        }).show());
            });
            return true;
        });
        images.recycle();
        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);

    }


    private void startTimer(long finalTime) {

        Handler handler = new Handler(Looper.getMainLooper());

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long elapsed = finalTime-new Date().getTime();

                int minutes = (int) ((elapsed / (1000 * 60)) % 60);
                int hours = (int) ((elapsed / (1000 * 60 * 60)) % 24);

                if(minutes==0 && hours==0){
                    timer.cancel();
                    String winner=getWinner();
                    handler.post(()->new MaterialAlertDialogBuilder(activity)
                            .setTitle("Igra je zavrsena!")
                            .setMessage("Pobednik je: "+winner)
                            .setNeutralButton((CharSequence) "Cancel", (dialog, which) -> {
                                dialog.cancel();
                                NavDirections d=TableFragmentDirections.newGame();
                                controller.navigate(d);
                            }).show());
                }
                StringBuilder time = new StringBuilder();
                time.append(String.format("%02d", hours)).append(":");
                time.append(String.format("%02d", minutes));

                model.setTimeString(time.toString());
            }
        }, 0, 1000*60);

    }

    private int calculateWorth(Player p){
        if(p.getMoney()==-1) return -1;
        int worth= p.getMoney();
        List<Property> properties=propertyModel.getOfHolder(p.getIndex());
        for(Property prop:properties){
            worth+=prop.getProperty_price();
            if(prop.getType()==0){
                worth+=prop.getBuilding_price()*prop.getHouses();
            }
        }
        return worth;
    }

    private String getWinner(){
        List<Player> players=model.getAllPlayers();
        Player p=players.stream().max(Comparator.comparingInt(this::calculateWorth)).get();
        List<Player> maxs=players.stream().filter(e->{
            return calculateWorth(e)==calculateWorth(p);
        }).collect(Collectors.toList());
        String ret="";
        for(Player maxp:maxs){
            ret+=maxp.getName()+" ";
        }
        return ret;
    }

}