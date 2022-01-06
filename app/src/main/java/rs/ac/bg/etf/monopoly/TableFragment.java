package rs.ac.bg.etf.monopoly;

import android.content.res.TypedArray;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
                    if(k.getHolder()== model.getCurrentUser() && k.getType()==0){
                        TableFragmentDirections.Details action=TableFragmentDirections.details();
                        action.setIndex(index);
                        controller.navigate(action);
                    }

                });

            });
        }
        images.recycle();
        return amb.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        controller= Navigation.findNavController(view);

    }
}