package rs.ac.bg.etf.monopoly.property;



import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import rs.ac.bg.etf.monopoly.MainActivity;
import rs.ac.bg.etf.monopoly.db.Property;
import rs.ac.bg.etf.monopoly.db.Repository;

public class PropertyModel extends ViewModel {

    private Repository repo=null;

    public PropertyModel(Repository repo) {
        this.repo = repo;
    }

    public void insert(Property p){
        repo.insert(p);
    }

    public LiveData<Property> getProperty(int id){
        return repo.getProperty(id);
    }

    public Property getPropertyBlocking(int id){
        return repo.getPropertyBlocking(id);
    }
    public void initDB(){
        repo.initProperties();
    }

    public LiveData<List<Property>> getTypeOfHolder(int h, int t){
        return repo.getTypeOfHolder(h,t);
    }

    public static PropertyModel getModel(Repository repo, MainActivity activity){
        ViewModelProvider.Factory factory=new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
                return (T)new PropertyModel(repo);
            }
        };
        return new ViewModelProvider(activity, factory).get(PropertyModel.class);
    }
}
