package rs.ac.bg.etf.monopoly;

import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import rs.ac.bg.etf.monopoly.databinding.FragmentTableBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Player;
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
        Repository repo=new Repository(activity,db.getDaoProperty(),db.getDaoPlayer());
        propertyModel=PropertyModel.getModel(repo, activity);
        model=GameModel.getModel(repo,activity);
        ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
            int e=model.getNextGame();
            List<Player> list=new ArrayList<>();
            list.add(new Player(0,e,"Jana",1500,0,0));
            list.add(new Player(1,e,"Lana",1500,0,0));
            list.add(new Player(2,e,"Nana",1500,0,0));
            list.add(new Player(3,e,"Gana",1500,0,0));
            model.startGame(list);
        });


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

        model.getPlayers().observe(getViewLifecycleOwner(),e->{
            TypedArray img=getResources().obtainTypedArray(R.array.ids);
            ((ImageView)amb.getRoot().findViewById(img.getResourceId(model.getOldPossition(),0))).clearColorFilter();
            for(int j=0;j<model.getUserCount();j++){
                ((ImageView)amb.getRoot().findViewById(img.getResourceId(e.get(j).getPosition(),0))).setColorFilter(Color.parseColor(colors[j]),
                        PorterDuff.Mode.MULTIPLY);
            }
            img.recycle();
        });

        Handler mainHanfler=new Handler(Looper.getMainLooper());

        amb.topAppBar.getMenu().findItem(0).setOnMenuItemClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                model.rollTheDice();
                mainHanfler.post(()->amb.dices.setText("Kockica1: "+model.getDice1()+" Kockica2"+model.getDice2()));
            });


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