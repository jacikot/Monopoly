package rs.ac.bg.etf.monopoly;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import rs.ac.bg.etf.monopoly.databinding.FragmentTableBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Repository;
import rs.ac.bg.etf.monopoly.property.PropertyModel;
import rs.ac.bg.etf.monopoly.property.RouterUtility;


public class TableFragment extends Fragment {

    private FragmentTableBinding amb;
    private GameModel model;
    private PropertyModel propertyModel;
    private MainActivity activity;
    private NavController controller;


    public TableFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= (MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        Repository repo=new Repository(activity,db.getDao());
        propertyModel=PropertyModel.getModel(repo, activity);
        model= new ViewModelProvider(activity).get(GameModel.class);
        model.startGame(4);
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentTableBinding.inflate(inflater,container,false);
        TypedArray images=getResources().obtainTypedArray(R.array.ids);

        for(int i=0;i<images.length();i++){
            int id=images.getResourceId(i,0);
            int index=i;
            int start=R.id.start;

            amb.getRoot().findViewById(id).setOnClickListener(e->{
                propertyModel.getProperty(index).observe(getViewLifecycleOwner(),k->{
                    RouterUtility.route(controller,k,model.getCurrentUser());
                });
            });
        }

        String[]colors=getResources().getStringArray(R.array.colors);

        //pomeranje igraca
        for(int i=0;i<model.getUserCount();i++){
            model.getPossitions().get(i).observe(getViewLifecycleOwner(),e->{
                TypedArray img=getResources().obtainTypedArray(R.array.ids);
                ((ImageView)amb.getRoot().findViewById(img.getResourceId(model.getOldPossitions().
                        get(model.getCurrentUser()),0))).clearColorFilter();

                for(int j=0;j<model.getUserCount();j++){
                    ((ImageView)amb.getRoot().findViewById(img.getResourceId(model.getPossitions().get(j).getValue(),0))).setColorFilter(Color.parseColor(colors[j]),
                            PorterDuff.Mode.MULTIPLY);
                }
                img.recycle();
            });
        }

        amb.topAppBar.getMenu().findItem(0).setOnMenuItemClickListener(e->{
           model.rollTheDice();
           amb.dices.setText("Kockica1: "+model.getDice1()+" Kockica2"+model.getDice2());
           return true;
        });

        images.recycle();
        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);

    }
}