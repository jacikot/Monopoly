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

import java.util.List;

import rs.ac.bg.etf.monopoly.CardModel;
import rs.ac.bg.etf.monopoly.GameModel;
import rs.ac.bg.etf.monopoly.MainActivity;
import rs.ac.bg.etf.monopoly.MyApplication;
import rs.ac.bg.etf.monopoly.R;
import rs.ac.bg.etf.monopoly.databinding.FragmentOpenCardBinding;
import rs.ac.bg.etf.monopoly.db.Card;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Property;
import rs.ac.bg.etf.monopoly.db.Repository;


public class OpenCardFragment extends Fragment {

    private FragmentOpenCardBinding amb;
    private GameModel model;
    private PropertyModel propertyModel;
    private CardModel cardModel;
    private MainActivity activity;
    private NavController controller;
    private Repository repo;


    public OpenCardFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= (MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        repo=new Repository(activity,db.getDaoProperty(), db.getDaoPlayer(), db.getDaoCard());
        model= GameModel.getModel(repo,activity);
        propertyModel=PropertyModel.getModel(repo,activity);
        cardModel=CardModel.getModel(repo,activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb= FragmentOpenCardBinding.inflate(inflater,container,false);
        TypedArray images=getResources().obtainTypedArray(R.array.images_details);
        OpenCardFragmentArgs args=OpenCardFragmentArgs.fromBundle(getArguments());
        amb.posed.setImageDrawable(images.getDrawable(args.getIndex()));
        images.recycle();

        Handler h=new Handler(Looper.getMainLooper());

        if(model.isBought()||!model.isAbleToBuy()){
            amb.otvori.setEnabled(false);
        }

        amb.otvori.setOnClickListener(e->{

            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                Property p=propertyModel.getPropertyBlocking(args.getIndex());
                List<Card> cards=cardModel.getCardsType(p.getType()-3);
                int random=(int)(Math.random()*cards.size());
                h.post(()->amb.poruka.setText(cards.get(random).getMessage()));
            });
        });

        amb.layout.setGravity(Gravity.CENTER);


        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}