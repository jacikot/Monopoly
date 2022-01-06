package rs.ac.bg.etf.monopoly;

import androidx.lifecycle.ViewModel;

public class GameModel extends ViewModel {

    private int currentUser=3;
    private int dice1=5;
    private int dice2=6;
    private Integer[] possitions=new Integer[8];

    public Integer[] getPossitions() {
        return possitions;
    }

    public void setPossitions(Integer[] possitions) {
        this.possitions = possitions;
    }

    public int getDice1() {
        return dice1;
    }

    public void setDice1(int dice1) {
        this.dice1 = dice1;
    }

    public int getDice2() {
        return dice2;
    }

    public void setDice2(int dice2) {
        this.dice2 = dice2;
    }

    public int getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(int currentUser) {
        this.currentUser = currentUser;
    }
}
