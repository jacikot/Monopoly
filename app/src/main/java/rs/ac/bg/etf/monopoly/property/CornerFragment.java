package rs.ac.bg.etf.monopoly.property;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rs.ac.bg.etf.monopoly.GameModel;
import rs.ac.bg.etf.monopoly.MainActivity;
import rs.ac.bg.etf.monopoly.R;
import rs.ac.bg.etf.monopoly.databinding.FragmentCornerBinding;
import rs.ac.bg.etf.monopoly.databinding.FragmentOpenCardBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
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
        repo=new Repository(activity,db.getDao());
        model= new ViewModelProvider(activity).get(GameModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb= FragmentCornerBinding.inflate(inflater,container,false);
        TypedArray images=getResources().obtainTypedArray(R.array.images_details);
        PropertyDetailsFragmentArgs args=PropertyDetailsFragmentArgs.fromBundle(getArguments());
        amb.posed.setImageDrawable(images.getDrawable(args.getIndex()));
        images.recycle();
        switch(args.getIndex()){
            case 0: amb.poruka.setText("Započinjete novi krug! Dobili ste 200M!"); break;
            case 10: amb.poruka.setText("Dosli ste samo u posetu zatvoru! Mozete da nastavite igru!"); break;
            case 20: amb.poruka.setText("Cestitamo! Dobijate novac prikupljen od poreza!"); break;
            case 30: amb.poruka.setText("Idete u zatvor! Pauzirate 2 kruga!"); break;
        }
        amb.layout.setGravity(Gravity.CENTER);


        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}