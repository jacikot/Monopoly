package rs.ac.bg.etf.monopoly;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

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
        Repository repo=new Repository(activity,db.getDaoProperty(),db.getDaoPlayer());
        model= GameModel.getModel(repo,activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentHomeBinding.inflate(inflater,container,false);
        amb.start.setOnClickListener(e->{
            NavDirections action=HomeFragmentDirections.actionStart();
            controller.navigate(action);
        });
        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}