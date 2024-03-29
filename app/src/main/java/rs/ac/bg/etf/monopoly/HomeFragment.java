package rs.ac.bg.etf.monopoly;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rs.ac.bg.etf.monopoly.databinding.FragmentHomeBinding;
import rs.ac.bg.etf.monopoly.databinding.FragmentTableBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Repository;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding amb;
    private GameModel model;
    private MainActivity activity;
    private NavController controller;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= (MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        Repository repo= new Repository(activity,db.getDaoProperty(),
                db.getDaoPlayer(), db.getDaoCard(),
                db.getDaoGame(),db.getDaoMove(),db.getSellingDao());
        model= GameModel.getModel(repo,activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentHomeBinding.inflate(inflater,container,false);
        Handler h=new Handler(Looper.getMainLooper());
        amb.start.setOnClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                model.startNextGame();
                h.post(()->{
                    NavDirections action=HomeFragmentDirections.actionStart();
                    controller.navigate(action);
                });
            });

        });
        amb.results.setOnClickListener(e->{
            NavDirections dir=HomeFragmentDirections.actionShowList();
            controller.navigate(dir);
        });

        amb.settings.setOnClickListener(e->{
            NavDirections dir=HomeFragmentDirections.actionHomeFragmentToSettingsFragment();
            controller.navigate(dir);
        });
        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}