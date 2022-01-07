package rs.ac.bg.etf.monopoly;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;

public class GameModel extends ViewModel {

    private int currentUser=0;
    private int dice1=5;
    private int dice2=6;
    private int attempts=0;
    private ArrayList<Integer> oldPossitions=new ArrayList<>();
    private ArrayList<MutableLiveData<Integer>> possitions=new ArrayList<>();


    public void startGame(int players){
        for(int i=0;i<players;i++){
            oldPossitions.add(0);
            possitions.add(new MutableLiveData<>(new Integer(0)));
        }
    }

    public int getUserCount(){
        return possitions.size();
    }

    public ArrayList<Integer> getOldPossitions() {
        return oldPossitions;
    }

    public void setOldPossitions(ArrayList<Integer> oldPossitions) {
        this.oldPossitions = oldPossitions;
    }

    public void rollTheDice(){
        dice1=(int)(Math.random()*6)+1;
        dice2=(int)(Math.random()*6)+1;
        oldPossitions.set(currentUser,possitions.get(currentUser).getValue());
        possitions.get(currentUser).setValue((possitions.get(currentUser).getValue()+dice1+dice2)%40);
        attempts++;
        if(dice1!=dice2){
            currentUser=(currentUser+1)%possitions.size();
            attempts=0;
        }
        else if(attempts>3){
            //player u zatvor
            currentUser=(currentUser+1)%possitions.size();
            attempts=0;
        }
    }

    public ArrayList<MutableLiveData<Integer>> getPossitions() {
        return possitions;
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
