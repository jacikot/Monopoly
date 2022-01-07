package rs.ac.bg.etf.monopoly;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import rs.ac.bg.etf.monopoly.databinding.ActivityMainBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Repository;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding amb;

    private DBMonopoly database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amb=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(amb.getRoot());
        database=DBMonopoly.getInstance(this);
        Repository repo=new Repository(this,database.getDaoProperty(),database.getDaoPlayer());
        repo.getProperty(0).observe(this,e->{
            if(e==null){
                repo.initProperties();
            }
        });
    }
}