package rs.ac.bg.etf.monopoly.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PropertyDAO {

    @Update
    public void update(Property p);

    @Insert
    public void insert(Property p);

    @Query("SELECT * FROM Property WHERE id=:id")
    public LiveData<Property> getProperty(int id);

    @Query("SELECT * FROM Property WHERE id=:id")
    public Property getPropertyBlocking(int id);

    @Query("SELECT * FROM Property WHERE holder=:h AND type=:type")
    public LiveData<List<Property>> getTypeOfHolder(int h, int type);

    @Query("SELECT * FROM Property WHERE holder=:h AND type=:type")
    public List<Property> getTypeOfHolderBlocking(int h, int type);

    @Query("SELECT COUNT(*) FROM Property WHERE `group`=:color")
    public int getSameColorCnt(int color);

    @Query("SELECT * FROM Property WHERE type=:type")
    public List<Property> getOfType(int type);

    @Query("SELECT * FROM PROPERTY WHERE holder=:h")
    public List<Property> getOfHolder(int h);
}
