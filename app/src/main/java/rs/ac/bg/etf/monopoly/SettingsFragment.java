package rs.ac.bg.etf.monopoly;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import rs.ac.bg.etf.monopoly.databinding.FragmentSettingsBinding;
import rs.ac.bg.etf.monopoly.databinding.ResultListFragmentBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Game;
import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Repository;
import rs.ac.bg.etf.monopoly.property.PropertyModel;


public class SettingsFragment extends Fragment {

    FragmentSettingsBinding amb;
    MainActivity activity;
    GameModel model;
    PropertyModel propertyModel;
    NavController controller;

    public static final String SENSITIVITY_KEY="sensitivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=(MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        Repository repo=new Repository(activity,db.getDaoProperty(), db.getDaoPlayer(), db.getDaoCard(),db.getDaoGame());
        model= GameModel.getModel(repo,activity);
        propertyModel=PropertyModel.getModel(repo,activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentSettingsBinding.inflate(inflater,container,false);
        String[]sensitivity=getResources().getStringArray(R.array.sensitivity);
        amb.shakingSpinner.setAdapter(new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_dropdown_item,sensitivity));
        amb.update.setOnClickListener(e->{
            int sensPos=amb.shakingSpinner.getSelectedItemPosition();
            TypedArray sensValues=getResources().obtainTypedArray(R.array.sensitivity_values);
            model.getSePreferences().edit().putInt(SENSITIVITY_KEY,sensValues.getInt(sensPos,10000)).commit();
            controller.navigateUp();
        });

        amb.topAppBar.setNavigationOnClickListener(e->{
            controller.navigateUp();
        });

        return amb.getRoot();
    }




    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);

    }
}