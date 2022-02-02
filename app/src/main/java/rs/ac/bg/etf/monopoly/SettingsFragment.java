package rs.ac.bg.etf.monopoly;

import android.content.SharedPreferences;
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
    public static final String DIALOG_KEY="dialog";
    public static final String DIALOG_PRESSED_KEY="dialog-pressed";
    public static final String PLAYER_KEY="players";
    public static final String TIME_KEY="duration";
    public static final String TIME_UPDATED="duration-update";

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


        String[]dialog=getResources().getStringArray(R.array.dialog);
        amb.dialogSpinner.setAdapter(new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_dropdown_item,dialog));

        amb.topAppBar.setNavigationOnClickListener(e->{
            controller.navigateUp();
        });

        String[]players=getResources().getStringArray(R.array.players);
        amb.playerSpinner.setAdapter(new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_dropdown_item,players));

        String[]durations=getResources().getStringArray(R.array.time);
        amb.timeSpinner.setAdapter(new ArrayAdapter<String>(activity,android.R.layout.simple_spinner_dropdown_item,durations));



        amb.update.setOnClickListener(e->{
            int sensPos=amb.shakingSpinner.getSelectedItemPosition();
            boolean dialogEnabled=amb.dialogSpinner.getSelectedItemPosition()==0;
            int playerPos=amb.playerSpinner.getSelectedItemPosition();
            int timePos=amb.timeSpinner.getSelectedItemPosition();
            TypedArray sensValues=getResources().obtainTypedArray(R.array.sensitivity_values);
            TypedArray playerValues=getResources().obtainTypedArray(R.array.players_values);
            TypedArray timeValues=getResources().obtainTypedArray(R.array.time_values);
            SharedPreferences preferences=model.getSePreferences();
            preferences.edit()
                    .putInt(SENSITIVITY_KEY,sensValues.getInt(sensPos,10000))
                    .putBoolean(DIALOG_KEY,dialogEnabled)
                    .putBoolean(DIALOG_PRESSED_KEY,false)
                    .putInt(PLAYER_KEY,playerValues.getInt(playerPos,8))
                    .putInt(TIME_KEY,timeValues.getInt(timePos,2))
                    .putBoolean(TIME_UPDATED,true)
                    .commit();
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