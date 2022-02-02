package rs.ac.bg.etf.monopoly.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PlayerDAO {

    @Insert
    public void insert(List<Player> p);


    @Update
    public void update(Player p);

    @Query("SELECT MAX(game)+1 FROM Player")
    public int getNextGame();

    @Query("SELECT * FROM Player WHERE game=:game")
    public LiveData<List<Player>> getPlayers(int game);

    @Query("SELECT * FROM Player WHERE game=:game")
    public List<Player> getAllPlayers(int game);

    @Query("SELECT * FROM Player")
    public List<Player> getAllGamePlayers();

    @Query("SELECT * FROM Player WHERE `index`=:player AND game=:game")
    public Player getPosition(int player, int game);

}
