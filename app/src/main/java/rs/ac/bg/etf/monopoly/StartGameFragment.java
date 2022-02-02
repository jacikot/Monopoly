package rs.ac.bg.etf.monopoly;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import rs.ac.bg.etf.monopoly.databinding.ActivityMainBinding;
import rs.ac.bg.etf.monopoly.databinding.AddPlayerDialogBinding;
import rs.ac.bg.etf.monopoly.databinding.FragmentHomeBinding;
import rs.ac.bg.etf.monopoly.databinding.FragmentStartGameBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Repository;


public class StartGameFragment extends Fragment {

    private FragmentStartGameBinding amb;
    private GameModel model;
    private MainActivity activity;
    private NavController controller;

    public StartGameFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= (MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        Repository repo=new Repository(activity,db.getDaoProperty(),db.getDaoPlayer(), db.getDaoCard(),db.getDaoGame());
        model= GameModel.getModel(repo,activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentStartGameBinding.inflate(inflater,container,false);
        String[] spinnerItems=getResources().getStringArray(R.array.spinner_array);
        Handler h=new Handler(Looper.getMainLooper());
        amb.add.setOnClickListener(e->{
            View infl= LayoutInflater.from(activity).inflate(R.layout.add_player_dialog,null,false);
            new MaterialAlertDialogBuilder(activity)
                    .setTitle("Dodavanje novog igraca:")
                    .setView(infl)
                    .setPositiveButton("Dodaj",(d,w)->{
                        String newPlayer=((EditText)infl.findViewById(R.id.name)).getText().toString();
                        ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                            int index=model.getAllPlayers().size();
                            if(index==8){
                                h.post(()->{
                                    Toast.makeText(activity,"Nije moguce dodati vise od 8 igraca",Toast.LENGTH_SHORT).show();
                                });

                                d.cancel();

                                return;
                            }
                            Player p=new Player(index, model.getCurrentGame(), newPlayer,1500,0,0);
                            model.insertPlayer(p);
                            d.cancel();
                        });

                    }).show();

        });

        amb.start.setOnClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                int index=model.getAllPlayers().size();
                if(index<2){
                    h.post(()->{
                        Toast.makeText(activity,"Nije moguce zapoceti igru sa manje od 2 igraca",Toast.LENGTH_SHORT).show();
                    });
                }
                else{
                    h.post(()->{
                        NavDirections action=StartGameFragmentDirections.start();
                        controller.navigate(action);
                    });
                }
            });
        });
        model.getPlayers().observe(getViewLifecycleOwner(),e->{
            amb.recyclerView.setHasFixedSize(true);
            PlayerAdapter adapter=new PlayerAdapter();
            adapter.setPlayers(e);
            amb.recyclerView.setAdapter(adapter);
            amb.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        });

        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}