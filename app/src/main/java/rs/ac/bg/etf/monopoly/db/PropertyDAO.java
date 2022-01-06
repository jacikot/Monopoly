package rs.ac.bg.etf.monopoly.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PropertyDAO {

    @Insert
    public void insert(Property p);

    @Query("SELECT * FROM Property WHERE id=:id")
    public LiveData<Property> getProperty(int id);

    @Query("SELECT * FROM Property WHERE holder=:h AND type=:type")
    public LiveData<List<Property>> getTypeOfHolder(int h, int type);
}
