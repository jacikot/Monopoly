package rs.ac.bg.etf.monopoly.db;

import androidx.room.Dao;
import androidx.room.Insert;

import java.util.List;

@Dao
public interface CardDAO {

    @Insert
    public void insert(List<Card> cards);

}
