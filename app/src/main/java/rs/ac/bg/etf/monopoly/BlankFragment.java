package rs.ac.bg.etf.monopoly;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import rs.ac.bg.etf.monopoly.databinding.FragmentBlankBinding;

public class BlankFragment extends Fragment {


    FragmentBlankBinding amb;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        amb=FragmentBlankBinding.inflate(inflater,container,false);

        return amb.getRoot();
    }
}