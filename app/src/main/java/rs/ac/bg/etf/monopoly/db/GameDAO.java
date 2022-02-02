package rs.ac.bg.etf.monopoly.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface GameDAO {

    @Insert
    void insertGame(Game g);

    @Query("SELECT MAX(idGame) FROM Game WHERE status=1")
    int getCurrentGameId();

    @Query("SELECT * FROM Game WHERE status=0")
    List<Game> getAllActiveGames();

    @Query("SELECT * FROM Game WHERE idGame=:id")
    Game getGame(int id);

}
