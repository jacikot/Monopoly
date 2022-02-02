package rs.ac.bg.etf.monopoly;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import java.util.List;

import rs.ac.bg.etf.monopoly.databinding.ResultListFragmentBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Game;
import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Property;
import rs.ac.bg.etf.monopoly.db.Repository;
import rs.ac.bg.etf.monopoly.property.PropertyModel;


public class ResultListFragment extends Fragment {


    ResultListFragmentBinding amb;
    MainActivity activity;
    GameModel model;
    PropertyModel propertyModel;
    NavController controller;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=(MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        Repository repo= new Repository(activity,db.getDaoProperty(),
                db.getDaoPlayer(), db.getDaoCard(),
                db.getDaoGame(),db.getDaoMove(),db.getSellingDao());
        model= GameModel.getModel(repo,activity);
        propertyModel=PropertyModel.getModel(repo,activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=ResultListFragmentBinding.inflate(inflater,container,false);
       initRecyclerView();
        amb.topAppBar.setNavigationOnClickListener(e->{
            controller.navigateUp();
        });
        amb.topAppBar.getMenu().getItem(0).setOnMenuItemClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                model.deleteAll();
                initRecyclerView();
            });

            return true;
        });
        return amb.getRoot();
    }

    void initRecyclerView(){
        ResultAdapter adapter=new ResultAdapter();
        Handler h=new Handler(Looper.getMainLooper());
        ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
            List<Game> games=model.getAllFinishedGames();
            adapter.setGames(games);
            List<Player> players=model.getAllPlayersEveryGame();
            adapter.setPlayers(players);
            h.post(()->{
                amb.recyclerView.setHasFixedSize(true);
                amb.recyclerView.setAdapter(adapter);
                amb.recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            });
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);

    }
}