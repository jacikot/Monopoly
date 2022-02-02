package rs.ac.bg.etf.monopoly.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;
@Dao
public interface SellingDao {

    @Insert
    void insert(Selling s);

    @Query("SELECT * FROM Selling WHERE move=:m")
    List<Selling> getSellings(int m);

    @Query("DELETE FROM Selling")
    void deleteAll();

}
