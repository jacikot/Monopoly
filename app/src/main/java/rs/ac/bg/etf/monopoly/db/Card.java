package rs.ac.bg.etf.monopoly.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Card {
    @PrimaryKey
    int id;
    int type;


    int movement;//1, 2, +-3

    int money;
    int paymentType; //1-a bankom, 3-sa igracima, 2-po kuci/hotelu

    int prison; //1,2

    String message;

    public Card(int id, int type, int movement, int money, int paymentType, int prison, String message) {
        this.id = id;
        this.type = type;
        this.movement = movement;
        this.money = money;
        this.paymentType = paymentType;
        this.prison = prison;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMovement() {
        return movement;
    }

    public void setMovement(int movement) {
        this.movement = movement;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public int getPrison() {
        return prison;
    }

    public void setPrison(int prison) {
        this.prison = prison;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
