package rs.ac.bg.etf.monopoly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import rs.ac.bg.etf.monopoly.databinding.ActivityMainBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Repository;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding amb;

    private DBMonopoly database;
    private GameModel model;
    public static final String shared_NAME="locals";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amb=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(amb.getRoot());
        database=DBMonopoly.getInstance(this);
        Repository repo=new Repository(this,database.getDaoProperty(),database.getDaoPlayer(),database.getDaoCard(),database.getDaoGame());
        repo.getProperty(0).observe(this,e->{
            if(e==null){
                repo.initProperties(true);
                repo.insertCards();
            }
        });
        model=GameModel.getModel(repo,this);
        SharedPreferences preferences=getSharedPreferences(shared_NAME, Context.MODE_PRIVATE);
        model.setSharedPrefs(preferences);
        if(!preferences.contains(SettingsFragment.SENSITIVITY_KEY))
            preferences.edit()
                    .putInt(SettingsFragment.SENSITIVITY_KEY,200)
                    .putBoolean(SettingsFragment.DIALOG_KEY,false)
                    .putBoolean(SettingsFragment.DIALOG_PRESSED_KEY,false)
                    .commit();
        else preferences.edit()
                .putBoolean(SettingsFragment.DIALOG_PRESSED_KEY,false)
                .commit();
    }
}