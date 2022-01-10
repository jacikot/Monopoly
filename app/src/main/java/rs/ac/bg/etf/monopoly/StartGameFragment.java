package rs.ac.bg.etf.monopoly;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

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
        Repository repo=new Repository(activity,db.getDaoProperty(),db.getDaoPlayer(), db.getDaoCard());
        model= GameModel.getModel(repo,activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentStartGameBinding.inflate(inflater,container,false);
        String[] spinnerItems=getResources().getStringArray(R.array.spinner_array);
        amb.spinner.setAdapter(new ArrayAdapter<String>(activity,R.layout.spinner_item,spinnerItems));
        amb.start.setOnClickListener(e->{
            NavDirections action=StartGameFragmentDirections.start();
            controller.navigate(action);
        });
        amb.spinner.setSelection(6);
        amb.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int num=Integer.parseInt(spinnerItems[position]);
                amb.players.removeViewsInLayout(num,8-num);
                Handler h=new Handler(Looper.getMainLooper());
                AtomicReference<Integer>game=new AtomicReference<>(null);
                for(int i=0;i<num;i++){
                    LinearLayout ll=(LinearLayout) amb.players.getChildAt(i);
                    EditText et=(EditText) ll.getChildAt(0);
                    Button b=(Button) ll.getChildAt(1);
                    int index=i;
                    b.setOnClickListener(e->{
                        ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                            if(game.get()==null)game.set(model.getNextGame());
                            Player p=new Player(index,game.get(),et.getText().toString(),1500,0,0);
                            model.insertPlayer(p);
                            h.post(()->amb.players.removeView(ll));
                        });
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}