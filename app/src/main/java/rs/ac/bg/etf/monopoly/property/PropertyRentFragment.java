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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import rs.ac.bg.etf.monopoly.GameModel;
import rs.ac.bg.etf.monopoly.MainActivity;
import rs.ac.bg.etf.monopoly.R;
import rs.ac.bg.etf.monopoly.databinding.FragmentPropertyDetailsBinding;
import rs.ac.bg.etf.monopoly.databinding.FragmentPropertyRentBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Repository;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PropertyRentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PropertyRentFragment extends Fragment {

    private FragmentPropertyRentBinding amb;
    private GameModel model;
    private MainActivity activity;
    private NavController controller;
    private Repository repo;

    public PropertyRentFragment() {
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
        amb=FragmentPropertyRentBinding.inflate(inflater,container,false);
        TypedArray images=getResources().obtainTypedArray(R.array.images_details);
        PropertyDetailsFragmentArgs args=PropertyDetailsFragmentArgs.fromBundle(getArguments());
        amb.posed.setImageDrawable(images.getDrawable(args.getIndex()));
        images.recycle();

        repo.getProperty(args.getIndex()).observe(getViewLifecycleOwner(),e->{
            if(amb.nekretnine.getChildCount()>1)
                amb.nekretnine.removeViews(0,amb.nekretnine.getChildCount());
            amb.kuca.setText(amb.kuca.getText()+"("+e.getBuilding_price()+"M) =>");
            amb.hotel.setText(amb.hotel.getText()+"("+e.getBuilding_price()+"M) =>");
            amb.prodaj.setText(amb.prodaj.getText() + "("+e.getProperty_price()/2+"M)");
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


        });



        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);
    }
}