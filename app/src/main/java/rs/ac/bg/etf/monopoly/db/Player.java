package rs.ac.bg.etf.monopoly.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import rs.ac.bg.etf.monopoly.MainActivity;

@Entity(primaryKeys ={"index","game"})
public class Player {
    int index;
    int game;

    String name;
    int money;
    int position;
    int prison;


    int evaluation;

    public int getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(int evaluation) {
        this.evaluation = evaluation;
    }

    public Player(int index, int game, String name, int money, int position, int prison,int evaluation) {
        this.index = index;
        this.game = game;
        this.name = name;
        this.money = money;
        this.position = position;
        this.prison = prison;
        this.evaluation=evaluation;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getGame() {
        return game;
    }

    public void setGame(int game) {
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPrison() {
        return prison;
    }

    public void setPrison(int prison) {
        this.prison = prison;
    }
}
