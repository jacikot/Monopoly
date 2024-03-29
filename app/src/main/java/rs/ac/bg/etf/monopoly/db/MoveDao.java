package rs.ac.bg.etf.monopoly.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MoveDao {

    @Insert
    void insertMove(Move m);

    @Query("DELETE FROM Move")
    void deleteAll();

    @Query("SELECT * FROM Move WHERE game=:g")
    List<Move> getAllMoves(int g);
}
