package rs.ac.bg.etf.monopoly.property;

import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
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
import android.widget.ImageView;
import android.widget.Toast;

import rs.ac.bg.etf.monopoly.GameModel;
import rs.ac.bg.etf.monopoly.MainActivity;
import rs.ac.bg.etf.monopoly.MyApplication;
import rs.ac.bg.etf.monopoly.R;
import rs.ac.bg.etf.monopoly.databinding.FragmentPropertyDetailsBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Property;
import rs.ac.bg.etf.monopoly.db.Repository;


public class PropertyDetailsFragment extends Fragment {

    private FragmentPropertyDetailsBinding amb;
    private GameModel model;
    private PropertyModel propertyModel;
    private MainActivity activity;
    private NavController controller;
    private Repository repo;

    public PropertyDetailsFragment() {
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentPropertyDetailsBinding.inflate(inflater,container,false);
        TypedArray images=getResources().obtainTypedArray(R.array.images_details);
        PropertyDetailsFragmentArgs args=PropertyDetailsFragmentArgs.fromBundle(getArguments());
        amb.posed.setImageDrawable(images.getDrawable(args.getIndex()));
        images.recycle();

        if(!model.isAbleToBuy()||model.isBought()){
            amb.hotelButton.setEnabled(false);
            amb.kucaButton.setEnabled(false);
        }

        Handler h=new Handler(Looper.getMainLooper());

        amb.kucaButton.setOnClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                Property p=propertyModel.getPropertyBlocking(args.getIndex());
                if(propertyModel.ownsAllSameColor(args.getUser(),p.getGroup())&&p.getHouses()<4){
                    Player player=model.getPlayer(args.getUser());
                    if(p.getBuilding_price()<= player.getMoney()) {
                        model.setBought(true);
                        amb.kucaButton.setEnabled(false);
                        p.setHouses(p.getHouses() + 1);
                        propertyModel.update(p);
                        player.setMoney(player.getMoney() - p.getBuilding_price());
                    }
                    else {
                        h.post(()-> Toast.makeText(activity,"Nemate dovoljno novca!",Toast.LENGTH_SHORT).show());
                    }
                }
                else{
                    h.post(()-> Toast.makeText(activity,"Ne posedujete sve posede ove grupe!",Toast.LENGTH_SHORT).show());
                }
            });
        });



        amb.hotelButton.setOnClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                Property p=propertyModel.getPropertyBlocking(args.getIndex());
                if(p.getHouses()==4){
                    Player player=model.getPlayer(args.getUser());
                    if(p.getBuilding_price()> player.getMoney()){
                        model.setBought(true);
                        amb.hotelButton.setEnabled(false);
                        p.setHouses(p.getHouses()+1);
                        propertyModel.update(p);
                        player.setMoney(player.getMoney()-p.getBuilding_price());
                    }
                    else {
                        h.post(()-> Toast.makeText(activity,"Nemate dovoljno novca!",Toast.LENGTH_SHORT).show());
                    }

                }
            });
        });


        amb.prodaj.setOnClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                Property p=propertyModel.getPropertyBlocking(args.getIndex());
                Player player=model.getPlayer(args.getUser());
                player.setMoney(player.getMoney()+(p.getProperty_price()+p.getBuilding_price()*p.getHouses())/2);
                p.setHolder(-1);
                p.setHouses(-1);
                propertyModel.update(p);
                model.update(player);
                h.post(()->{
                   controller.navigateUp();
                });
            });
        });

        repo.getProperty(args.getIndex()).observe(getViewLifecycleOwner(),e->{
            if(amb.nekretnine.getChildCount()>0)
                amb.nekretnine.removeViews(0,amb.nekretnine.getChildCount());
            amb.kuca.setText("Jedna kuca "+"("+e.getBuilding_price()+"M) =>");
            amb.hotel.setText("Jedan hotel "+"("+e.getBuilding_price()+"M) =>");
            amb.prodaj.setText("Prodaj " + "("+(e.getProperty_price()+e.getHouses()*e.getBuilding_price())/2+"M)");
            if(e.getHouses()>4){
                //izbaci kuce
                ImageView v=new ImageView(activity);
                v.setImageDrawable(getResources().getDrawable(R.drawable.outline_apartment_24, activity.getTheme()));

                v.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_red_dark),
                        PorterDuff.Mode.MULTIPLY);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.setTooltipText(getString(R.string.hotel));
                }
                amb.nekretnine.addView(v);
                amb.hotelButton.setEnabled(false);
                amb.kucaButton.setEnabled(false);
            }
            else{
                if(e.getHouses()==4) amb.kucaButton.setEnabled(false);
                else amb.hotelButton.setEnabled(false);
                for(int i=0;i<e.getHouses();i++){
                    ImageView v=new ImageView(activity);
                    v.setImageDrawable(activity.getDrawable(R.drawable.outline_home_24));
                    v.setColorFilter(ContextCompat.getColor(activity, android.R.color.holo_green_dark),
                            PorterDuff.Mode.MULTIPLY);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.setTooltipText(getString(R.string.kuca));
                    }
                    amb.nekretnine.addView(v);
                }
            }
            amb.nekretnine.setGravity(Gravity.CENTER);

        });

        amb.topAppBar.setNavigationOnClickListener(e->{
            controller.navigateUp();
        });

        return amb.getRoot();
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}