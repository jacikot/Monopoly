package rs.ac.bg.etf.monopoly.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class Game {
    @PrimaryKey(autoGenerate = true)
    int idGame;

    int status;
    long start;
    long duration;

    @Ignore
    public Game(int status) {
        this.status = status;
        start=new Date().getTime();
        duration=-1;
    }

    public Game(int idGame, int status, long start, long duration) {
        this.idGame = idGame;
        this.status = status;
        this.start = start;
        this.duration = duration;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int getIdGame() {
        return idGame;
    }

    public void setIdGame(int idGame) {
        this.idGame = idGame;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
