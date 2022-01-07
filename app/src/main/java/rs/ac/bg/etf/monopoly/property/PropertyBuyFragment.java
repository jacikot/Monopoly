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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import rs.ac.bg.etf.monopoly.GameModel;
import rs.ac.bg.etf.monopoly.MainActivity;
import rs.ac.bg.etf.monopoly.MyApplication;
import rs.ac.bg.etf.monopoly.R;
import rs.ac.bg.etf.monopoly.databinding.FragmentPropertyBuyBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Property;
import rs.ac.bg.etf.monopoly.db.Repository;

public class PropertyBuyFragment extends Fragment {

    private FragmentPropertyBuyBinding amb;
    private GameModel gameModel;
    private PropertyModel propertyModel;
    private MainActivity activity;
    private NavController controller;
    private Repository repo;

    public PropertyBuyFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= (MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        repo=new Repository(activity,db.getDaoProperty(),db.getDaoPlayer());
        gameModel= GameModel.getModel(repo,activity);
        propertyModel=PropertyModel.getModel(repo,activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentPropertyBuyBinding.inflate(inflater,container,false);
        TypedArray images=getResources().obtainTypedArray(R.array.images_details);
        PropertyBuyFragmentArgs args=PropertyBuyFragmentArgs.fromBundle(getArguments());
        amb.posed.setImageDrawable(images.getDrawable(args.getIndex()));
        images.recycle();

        repo.getProperty(args.getIndex()).observe(getViewLifecycleOwner(),e->{
            amb.kupi.setText(amb.kupi.getText()+"("+e.getProperty_price()+"M)");
        });
        Handler handler= new Handler(Looper.getMainLooper());
        if(!gameModel.isAbleToBuy()||gameModel.isBought()){
            amb.kupi.setEnabled(false);
        }
        amb.kupi.setOnClickListener(e->{
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                Property p=propertyModel.getPropertyBlocking(args.getIndex());
                Player player=gameModel.getPlayer(args.getUser());
                if(p.getProperty_price()<=player.getMoney()){
                    p.setHouses(0);
                    p.setHolder(gameModel.getLastPlayer());
                    propertyModel.update(p);

                    player.setMoney(player.getMoney()-p.getProperty_price());
                    gameModel.update(player);
                    gameModel.setBought(true);
                    handler.post(()->{
                        RouterUtility.routeBuy(controller,p,player.getIndex());

                    });
                }
                else {
                    handler.post(()-> Toast.makeText(activity,"Nemate dovoljno novca!",Toast.LENGTH_SHORT).show());
                }


            });
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