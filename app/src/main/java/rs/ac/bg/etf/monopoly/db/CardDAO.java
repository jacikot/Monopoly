package rs.ac.bg.etf.monopoly.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CardDAO {

    @Insert
    public void insert(List<Card> cards);

    @Query("SELECT * FROM Card WHERE type=:t")
    public List<Card> getCardsForType(int t);

}
