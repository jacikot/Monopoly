package rs.ac.bg.etf.monopoly;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDirections;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import rs.ac.bg.etf.monopoly.databinding.FragmentTableBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Repository;


public class TableFragment extends Fragment {

    private FragmentTableBinding amb;
    private GameModel model;
    private MainActivity activity;
    private NavController controller;
    private Repository repo;

    public TableFragment() {
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
        amb=FragmentTableBinding.inflate(inflater,container,false);
        TypedArray images=getResources().obtainTypedArray(R.array.ids);

        for(int i=0;i<images.length();i++){
            int id=images.getResourceId(i,0);
            int index=i;
            int start=R.id.start;

            amb.getRoot().findViewById(id).setOnClickListener(e->{

                repo.getProperty(index).observe(getViewLifecycleOwner(),k->{

                    if(k.getId()%10==0){
                        TableFragmentDirections.Corner action=TableFragmentDirections.corner(index);
                        controller.navigate(action);
                        return;
                    }

                    if(k.getType()==3||k.getType()==4){
                        TableFragmentDirections.Open action=TableFragmentDirections.open(index);
                        controller.navigate(action);
                        return;
                    }
                    if(k.getHolder()!=-1&&k.getHolder()!=model.getCurrentUser()&&k.getType()==2 || k.getType()==5){
                        TableFragmentDirections.Taxes action=TableFragmentDirections.taxes(index);
                        controller.navigate(action);
                        return;
                    }
                    if(k.getHolder()== model.getCurrentUser() && k.getType()==0){
                        TableFragmentDirections.Details action=TableFragmentDirections.details();
                        action.setIndex(index);
                        controller.navigate(action);
                        return;
                    }
                    if(k.getHolder()== model.getCurrentUser() && (k.getType()==1 || k.getType()==2)){
                        TableFragmentDirections.ToStation action=TableFragmentDirections.toStation(index);
                        controller.navigate(action);
                        return;
                    }
                    if(k.getHolder()==-1){
                        TableFragmentDirections.Buy action=TableFragmentDirections.buy();
                        action.setIndex(index);
                        controller.navigate(action);
                        return;
                    }
                    if(k.getHolder()!=-1 && k.getHolder()!=model.getCurrentUser()){
                        TableFragmentDirections.Pay action=TableFragmentDirections.pay(index);
                        controller.navigate(action);
                        return;
                    }

                });

            });
        }

//        model.getPossitions()[0]=(int)(Math.random()*40);
//        model.getPossitions()[1]=(int)(Math.random()*40);
//        model.getPossitions()[2]=(int)(Math.random()*40);
//        model.getPossitions()[3]=(int)(Math.random()*40);
//        ((ImageView)amb.getRoot().findViewById(images.getResourceId(model.getPossitions()[0],0))).setColorFilter(Color.parseColor("#FF8B8B"),
//                PorterDuff.Mode.MULTIPLY);
//((ImageView)amb.getRoot().findViewById(images.getResourceId(model.getPossitions()[1],0))).setColorFilter(Color.parseColor("#C77AFF"),
//                PorterDuff.Mode.MULTIPLY);
//((ImageView)amb.getRoot().findViewById(images.getResourceId(model.getPossitions()[2],0))).setColorFilter(Color.parseColor("#FF9B66"),
//                PorterDuff.Mode.MULTIPLY);
//((ImageView)amb.getRoot().findViewById(images.getResourceId(model.getPossitions()[3],0))).setColorFilter(Color.parseColor("#61914B"),
//                PorterDuff.Mode.MULTIPLY);

        images.recycle();
        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);

    }
}