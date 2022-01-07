package rs.ac.bg.etf.monopoly.property;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rs.ac.bg.etf.monopoly.GameModel;
import rs.ac.bg.etf.monopoly.MainActivity;
import rs.ac.bg.etf.monopoly.R;
import rs.ac.bg.etf.monopoly.databinding.FragmentPropertyPayBinding;
import rs.ac.bg.etf.monopoly.db.DBMonopoly;
import rs.ac.bg.etf.monopoly.db.Repository;


public class PropertyPayFragment extends Fragment {
    private FragmentPropertyPayBinding amb;
    private GameModel model;
    private MainActivity activity;
    private NavController controller;
    private Repository repo;

    public PropertyPayFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity= (MainActivity) requireActivity();
        DBMonopoly db=DBMonopoly.getInstance(activity);
        repo=new Repository(activity,db.getDaoProperty(),db.getDaoPlayer());
        model= new ViewModelProvider(activity).get(GameModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb= FragmentPropertyPayBinding.inflate(inflater,container,false);
        TypedArray images=getResources().obtainTypedArray(R.array.images_details);
        PropertyDetailsFragmentArgs args=PropertyDetailsFragmentArgs.fromBundle(getArguments());
        amb.posed.setImageDrawable(images.getDrawable(args.getIndex()));
        images.recycle();



        repo.getProperty(args.getIndex()).observe(getViewLifecycleOwner(),e->{
            if(e.getType()==2){
                repo.getTypeOfHolder(e.getHolder(), e.getType()).observe(getViewLifecycleOwner(),p->{
                    if(p.size()==1){
                        model.getDice2().observe(getViewLifecycleOwner(),k->{
                            amb.prodaj.setText("Platite rezije " + "("+4*(model.getDice1().getValue()+ k)+"M)");
                        });
                    }
                    if(p.size()==2){
                        model.getDice2().observe(getViewLifecycleOwner(),k->{
                            amb.prodaj.setText("Platite rezije " + "("+4*(model.getDice1().getValue()+ k)+"M)");
                        });
                    }
                });
                if(e.getId()==12){
                    amb.poruka.setText("Niste platili struju ovaj mesec!");
                }
                else amb.poruka.setText("Niste platili vodu ovaj mesec!");
            }
            else{
                amb.poruka.setText("Niste platili porez ovaj mesec!");
                amb.prodaj.setText("Platite rezije " + "("+e.getRent_l0()+"M)");
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