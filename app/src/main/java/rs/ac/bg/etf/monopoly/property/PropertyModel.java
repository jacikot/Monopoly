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

    public void startGame(){
        repo.initProperties(false);
    }

    public void insert(Property p){
        repo.insert(p);
    }
    public void update(Property p){
        repo.update(p);
    }

    public LiveData<Property> getProperty(int id){
        return repo.getProperty(id);
    }

    public Property getPropertyBlocking(int id){
        return repo.getPropertyBlocking(id);
    }
    public void initDB(){
        repo.initProperties(true);
    }

    public LiveData<List<Property>> getTypeOfHolder(int h, int t){
        return repo.getTypeOfHolder(h,t);
    }

    public List<Property> getTypeOfHolderBlocking(int h, int t){
        return repo.getTypeOfHolderBlocking(h,t);
    }

    public boolean ownsAllSameColor(int holder,int color){
       return repo.getTypeOfHolderBlocking(holder,0).stream().filter(e->{
            return e.getGroup()==color;
        }).count()==repo.getCountSameColor(color);
    }

    public boolean isBankruptcy(int user, int requested, int have){
        List<Property> properties=repo.getOfHolder(user);
        int sum=0;
        for(Property p:properties){
            sum+=p.getProperty_price()/2;
            if(p.getType()==0){
                sum+=p.getBuilding_price()*p.getHouses()/2;
            }
        }
        return sum<(requested-have);
    }

    public List<Property> getOfType(int type){
        return repo.getOfType(type);
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
