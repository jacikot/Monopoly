package rs.ac.bg.etf.monopoly.property;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import rs.ac.bg.etf.monopoly.GameModel;
import rs.ac.bg.etf.monopoly.MainActivity;
import rs.ac.bg.etf.monopoly.MyApplication;
import rs.ac.bg.etf.monopoly.R;
import rs.ac.bg.etf.monopoly.databinding.FragmentCornerBinding;
import rs.ac.bg.etf.monopoly.db.Card;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Property;
import rs.ac.bg.etf.monopoly.db.Repository;

public class CornerFragment extends Fragment {

    private FragmentCornerBinding amb;
    private GameModel model;
    private MainActivity activity;
    private NavController controller;
    private Repository repo;

    public CornerFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= (MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        repo=new Repository(activity,db.getDaoProperty(), db.getDaoPlayer(), db.getDaoCard(),db.getDaoGame());
        model= new ViewModelProvider(activity).get(GameModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb= FragmentCornerBinding.inflate(inflater,container,false);
        TypedArray images=getResources().obtainTypedArray(R.array.images_details);
        CornerFragmentArgs args=CornerFragmentArgs.fromBundle(getArguments());
        amb.posed.setImageDrawable(images.getDrawable(args.getIndex()));
        images.recycle();

        ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
            Player p= model.getPlayer(args.getUser());
            h.post(()->{
                switch(args.getIndex()){
                    case 0: amb.poruka.setText("Započinjete novi krug! Dobili ste 200M!"); break;
                    case 10: if(p.getPrison()<=0) amb.poruka.setText("Dosli ste samo u posetu zatvoru! Mozete da nastavite igru!");
                            else amb.poruka.setText("U zatvoru ste! Pauzirate 2 kruga!"); break;
                    case 20: amb.poruka.setText("Cestitamo! Dobijate novac prikupljen od poreza!"); break;
                    case 30: amb.poruka.setText("Idete u zatvor! Pauzirate 2 kruga!"); break;
                }
            });

        });

        amb.layout.setGravity(Gravity.CENTER);


        switch(args.getIndex()){
            case 0: break;
            case 10: break;
            case 20: takeFromBoard(args.getUser()); break;
            case 30: gotoPrison(args.getUser());break;
        }

        return amb.getRoot();
    }

    Handler h=new Handler(Looper.getMainLooper());

    private void gotoPrison(int user) {
        ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
            Player p= model.getPlayer(user);
            if(p.getPrison()<0){
                p.setPrison(p.getPrison()+1);
                h.post(()->Toast.makeText(activity,"Ne morate u zatvor imae besplatnu kartu!",Toast.LENGTH_SHORT).show());
            }
            else{
                p.setPrison(2);
                p.setPosition(10);

            }
            model.update(p);
        });
    }

    private void takeFromBoard(int user) {
        ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
            Player p= model.getPlayer(user);
            p.setMoney(p.getMoney()+model.getMoneyFromTaxes());
            model.update(p);
            model.setMoneyFromTaxes(0);
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}