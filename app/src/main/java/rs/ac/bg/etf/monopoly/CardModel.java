package rs.ac.bg.etf.monopoly;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import rs.ac.bg.etf.monopoly.db.Card;
import rs.ac.bg.etf.monopoly.db.Repository;
import rs.ac.bg.etf.monopoly.property.PropertyModel;

public class CardModel extends ViewModel{

    private Repository repo;
    public CardModel(Repository r){
        repo=r;
    }

    public List<Card> getCardsType(int type){
        return repo.getCardsForType(type);
    }

    public static CardModel getModel(Repository repo, MainActivity activity){
        ViewModelProvider.Factory factory=new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
                return (T)new CardModel(repo);
            }
        };
        return new ViewModelProvider(activity, factory).get(CardModel.class);
    }
}
