package rs.ac.bg.etf.monopoly.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import rs.ac.bg.etf.monopoly.MainActivity;

@Database(entities = {Property.class, Player.class, Card.class, Game.class},version = 1, exportSchema = false)
public abstract class DBMonopoly extends RoomDatabase {

    public abstract PropertyDAO getDaoProperty();
    public abstract PlayerDAO getDaoPlayer();
    public abstract CardDAO getDaoCard();
    public abstract GameDAO getDaoGame();

    private static DBMonopoly instance=null;
    private static final String DBname="monopoly-database-prep5";

    public static DBMonopoly getInstance(MainActivity activity){
        if(instance==null){
            //double check u javi pattern!!!
            synchronized (DBMonopoly.class){
                if(instance==null){
                    instance= Room.databaseBuilder(activity.getApplicationContext(), DBMonopoly.class,DBname).build();

                }
            }

        }
        return instance;
    }
}
