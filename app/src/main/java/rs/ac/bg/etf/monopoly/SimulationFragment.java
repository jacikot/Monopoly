package rs.ac.bg.etf.monopoly;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import rs.ac.bg.etf.monopoly.databinding.FragmentSimulationBinding;
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
        propertyModel=PropertyModel.getModel(repo,activity);

    }

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

        Handler h=new Handler(Looper.getMainLooper());

        amb.simulate.setOnClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                List<Move> moves=model.getMoves(game);
                model.setCurrentGame(game);
                List<Player> players=model.getAllPlayers();

                players.forEach(p->{
                    p.setMoney(1500);
                    p.setPrison(0);
                    p.setPosition(0);
                    h.post(()->{
                       amb.board.useFilter(0,p.getIndex());
                       amb.board.invalidate();
                    });
                });
                moves.forEach(move->{
                    Player player=players.stream().filter(p->{
                        return p.getIndex()==move.getPlayer();
                    }).findFirst().orElse(null);
                    AtomicReference<Integer> finish=new AtomicReference<>(0);

                    h.post(()->{
                        amb.board.clearFilter(player.getPosition());
                        player.setPosition(move.getPositionTo());
                        players.forEach(p->{
                            amb.board.useFilter(p.getPosition(),p.getIndex());
                        });
                        amb.board.invalidate();
                        finish.set(1);
                        synchronized (finish){
                            finish.notify();
                        }
                    });
                    synchronized (finish){
                        while(finish.get()==0) {
                            try {
                                finish.wait();
                            } catch (InterruptedException interruptedException) {
                                interruptedException.printStackTrace();
                            }
                        }
                        SystemClock.sleep(1000);
                    }
                });
                h.post(()->{
                    Toast.makeText(activity,"Simulacija zavrsena",Toast.LENGTH_SHORT).show();
                });
            });
        });

        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}