package rs.ac.bg.etf.monopoly.db;

import android.content.res.TypedArray;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import rs.ac.bg.etf.monopoly.MainActivity;
import rs.ac.bg.etf.monopoly.MyApplication;
import rs.ac.bg.etf.monopoly.R;

public class Repository {


    private PropertyDAO property;
    private MainActivity activity;

    public Repository(MainActivity activity, PropertyDAO dao){
        property=dao;
        this.activity=activity;
    }

    public void insert(Property p){
        property.insert(p);
    }

    public LiveData<Property> getProperty(int id){
        return property.getProperty(id);
    }


    public void initDB(){
        TypedArray type=activity.getResources().obtainTypedArray(R.array.type);
        TypedArray group=activity.getResources().obtainTypedArray(R.array.group);
        TypedArray price=activity.getResources().obtainTypedArray(R.array.price);
        TypedArray house=activity.getResources().obtainTypedArray(R.array.house);
        TypedArray z1=activity.getResources().obtainTypedArray(R.array.z1);
        TypedArray z2=activity.getResources().obtainTypedArray(R.array.z2);
        TypedArray z3=activity.getResources().obtainTypedArray(R.array.z3);
        TypedArray z4=activity.getResources().obtainTypedArray(R.array.z4);
        TypedArray z5=activity.getResources().obtainTypedArray(R.array.z5);
        TypedArray z6=activity.getResources().obtainTypedArray(R.array.z6);

        for(int j=0;j<type.length();j++){
            int i=j;
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                property.insert(new Property(i,
                        type.getInt(i,0),
                        group.getInt(i,0),
                        price.getInt(i,0),
                        house.getInt(i,0),
                        z1.getInt(i,0),
                        z2.getInt(i,0),
                        z3.getInt(i,0),
                        z4.getInt(i,0),
                        z5.getInt(i,0),
                        z6.getInt(i,0)
                ));
            });

        }
    }

    public LiveData<List<Property>> getTypeOfHolder(int h, int t){
        return property.getTypeOfHolder(h,t);
    }
}
