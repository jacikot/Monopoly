package rs.ac.bg.etf.monopoly;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;

import rs.ac.bg.etf.monopoly.databinding.ActivityMainBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Repository;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding amb;

    private DBMonopoly database;
    private GameModel model;
    private static final String shared_NAME="locals";

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
        model.setSharedPrefs(getSharedPreferences(shared_NAME, Context.MODE_PRIVATE));
    }
}