package rs.ac.bg.etf.monopoly;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import rs.ac.bg.etf.monopoly.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding amb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        amb=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(amb.getRoot());
    }
}