package rs.ac.bg.etf.monopoly.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Selling {
    @PrimaryKey(autoGenerate = true)
    int idSelling;
    int field;
    int move;

    public Selling(int idSelling, int field, int move) {
        this.idSelling = idSelling;
        this.field = field;
        this.move = move;
    }

    public int getIdSelling() {
        return idSelling;
    }

    public void setIdSelling(int idSelling) {
        this.idSelling = idSelling;
    }

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }
}
