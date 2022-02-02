package rs.ac.bg.etf.monopoly;

import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
    private Shaker shaker;
    private SoundActivator soundActivator;



    public TableFragment() {
        // Required empty public constructor
    }

    Shaker.Callback callbackEnd;
    Shaker.Callback callbackStart;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= (MainActivity) requireActivity();
        soundActivator=new SoundActivator();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        Repository repo=
                new Repository(activity,db.getDaoProperty(),
                        db.getDaoPlayer(), db.getDaoCard(),
                        db.getDaoGame(),db.getDaoMove(),db.getSellingDao());
        propertyModel=PropertyModel.getModel(repo, activity);
        model=GameModel.getModel(repo,activity);
        ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
            model.startGame();
        });

        timer=new Timer();
        model.setTimer(timer);
        model.setFinalTime(new Date().getTime()+model.getSePreferences().getInt(SettingsFragment.TIME_KEY,2)*60*1000);
        startTimer();
        callbackEnd=()->{
            if(model.isPaid()){
                ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                    Player p=model.rollTheDice(TableFragment.this);
                    Property property=propertyModel.getPropertyBlocking(p.getPosition());
                    model.setAbleToBuy(true);
                    SystemClock.sleep(4000);
                    mainHanfler.post(()->{
                        RouterUtility.route(controller ,property, model.getLastPlayer());
                        model.getSePreferences().edit().putBoolean(SettingsFragment.DIALOG_PRESSED_KEY,false).commit();
                    });

                });
                return true;
            }
            else Toast.makeText(activity,"Niste platili dazbine!",Toast.LENGTH_SHORT).show();
            return false;
        };
        callbackStart=()->{
            if(model.isPaid()){
                soundActivator.start(activity);
            }
            return false;
        };
        shaker=new Shaker(activity,callbackEnd ,callbackStart);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentTableBinding.inflate(inflater,container,false);


        getViewLifecycleOwner().getLifecycle().addObserver(shaker);
        getViewLifecycleOwner().getLifecycle().addObserver(soundActivator);

        if(model.getSePreferences().getBoolean(SettingsFragment.TIME_UPDATED,false)){
            model.getSePreferences().edit().putBoolean(SettingsFragment.TIME_UPDATED,false);
            model.setFinalTime(new Date().getTime()+model.getSePreferences().getInt(SettingsFragment.TIME_KEY,2)*60*1000);
            long elapsed = model.getFinalTime()-new Date().getTime();

            int minutes = (int) ((elapsed / (1000 * 60)) % 60);
            int hours = (int) ((elapsed / (1000 * 60 * 60)) % 24);
            StringBuilder time = new StringBuilder();
            time.append(String.format("%02d", hours)).append(":");
            time.append(String.format("%02d", minutes));

            model.setTimeString(time.toString());
        }
        model.getTimeString().observe(getViewLifecycleOwner(),e->{
            amb.timer.setText(e);
        });
        BoardView.Callback c=(index)->{
            propertyModel.getProperty(index).observe(getViewLifecycleOwner(),k->{
                ((MyApplication)activity.getApplication()).getExecutorService().execute(()-> {
                    Player p=model.getPlayer(model.getLastPlayer());
                    if(p.getPosition()!=index) model.setAbleToBuy(false);
                    else model.setAbleToBuy(true);
                    mainHanfler.post(()->RouterUtility.route(controller,k, model.getLastPlayer()));
                });
            });
        };
        amb.board.setCallback(c);
        amb.topAppBar.getMenu().getItem(0).setOnMenuItemClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()-> {
                callbackStart.call();
                SystemClock.sleep(2000);
                callbackEnd.call();
            });

            return true;
        });

        amb.board.setOnTouchListener(amb.board.listener);


        if(model.isMoved()){
            model.setMoved(false);
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                Player p=model.getPlayer(model.getLastPlayer());
                Property property=propertyModel.getPropertyBlocking(p.getPosition());
                model.setAbleToBuy(true);
                SystemClock.sleep(2000);
                mainHanfler.post(()->{
                    RouterUtility.route(controller ,property, model.getLastPlayer());
                });
            });
        }

        String[]colors=getResources().getStringArray(R.array.colors);

        model.getPlayers().observe(getViewLifecycleOwner(),e->{
            amb.board.clearFilter(model.getOldPossition());
            amb.board.invalidate();
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                for(int j=0;j<model.getUserCount();j++){
                    Player p=model.getPlayer(j);
                    int index=j;
                    if(p.getMoney()>=0)
                        mainHanfler.post(()->{
                            amb.board.useFilter(e.get(index).getPosition(),index);
                            amb.board.invalidate();
                        });
                }
//
            });


        });

        model.getDice1().observe(getViewLifecycleOwner(),e->{
            amb.dices.setText("Kockica 1: "+e);
        });

        model.getDice2().observe(getViewLifecycleOwner(),e->{
            amb.dices2.setText("Kockica 2: "+e);
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
        amb.topAppBar.getMenu().getItem(2).setOnMenuItemClickListener(e-> {
            NavDirections dir=TableFragmentDirections.actionTableFragmentToSettingsFragment();
            controller.navigate(dir);
            return true;
        });
//        images.recycle();
        amb.topAppBar.setNavigationOnClickListener(e->{
            timer.cancel();
            controller.popBackStack();
            controller.navigateUp();
        });
        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);

    }




    private void startTimer() {

        Handler handler = new Handler(Looper.getMainLooper());

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                long elapsed = model.getFinalTime()-new Date().getTime();

                int minutes = (int) ((elapsed / (1000 * 60)) % 60);
                int hours = (int) ((elapsed / (1000 * 60 * 60)) % 24);

                if(minutes==0 && hours==0){
//                    timer.cancel(); vec uradjeno u finish
                    String winner=getWinner();
                    model.finishGame();
                    handler.post(()->{
                        AlertDialog dd=new MaterialAlertDialogBuilder(activity)
                                .setTitle("Igra je zavrsena!")
                                .setMessage("Pobednik je: "+winner)
                                .setNeutralButton((CharSequence) "Cancel", (dialog, which) -> {
                                    dialog.cancel();
                                    NavDirections d=TableFragmentDirections.newGame();
                                    controller.navigate(d);
                                }).create();
                        dd.setCanceledOnTouchOutside(false);
                        dd.show();

                    });
                }
                StringBuilder time = new StringBuilder();
                time.append(String.format("%02d", hours)).append(":");
                time.append(String.format("%02d", minutes));

                model.setTimeString(time.toString());
            }
        }, 0, 1000*60);

    }


    private String getWinner(){
        List<Player> players=model.getAllPlayers();
        Player p=players.stream().max(Comparator.comparingInt(p2 -> model.calculateWorth(p2))).get();
        List<Player> maxs=players.stream().filter(e->{
            return model.calculateWorth(e)== model.calculateWorth(p);
        }).collect(Collectors.toList());
        String ret="";
        for(Player maxp:maxs){
            ret+=maxp.getName()+" ";
        }
        return ret;
    }

}