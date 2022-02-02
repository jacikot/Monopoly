package rs.ac.bg.etf.monopoly.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface GameDAO {

    @Insert
    void insertGame(Game g);

    @Query("SELECT idGame FROM Game WHERE status=1")
    int getCurrentGameId();

    @Query("SELECT * FROM Game WHERE status=0")
    List<Game> getAllFinishedGames();

    @Query("SELECT * FROM Game WHERE idGame=:id")
    Game getGame(int id);

    @Query("UPDATE Game SET status=-1 WHERE status=1")
    void unfinished();

    @Query("UPDATE Game SET status=0,duration=:finishTime-start WHERE status=1")
    void finish(double finishTime);

    @Query("DELETE FROM Game")
    void deleteAll();

}
