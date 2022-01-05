package rs.ac.bg.etf.monopoly.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface PropertyDAO {

    @Insert
    public void insert(Property p);

    @Query("SELECT * FROM Property WHERE id=:id")
    public LiveData<Property> getProperty(int id);
}
