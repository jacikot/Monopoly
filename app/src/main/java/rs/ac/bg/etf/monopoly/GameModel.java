package rs.ac.bg.etf.monopoly;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Repository;
import rs.ac.bg.etf.monopoly.property.PropertyModel;

public class GameModel extends ViewModel {

    private int currentUser=0;
    private int dice1=5;
    private int dice2=6;
    private int attempts=0;
    private int oldPossition=0;
    private int currentGame;
    private int playerCnt;
    private Repository repo;
    private ArrayList<MutableLiveData<Integer>> possitions=new ArrayList<>();

    public GameModel(Repository repo){
        this.repo=repo;
    }

    public int getNextGame(){
        return repo.getNextGame();
    }

    public void startGame(List<Player> players){
        currentGame=players.get(0).getGame();
        playerCnt=players.size();
        for(int i=0;i<players.size();i++){
            players.get(i).setMoney(1500);
            players.get(i).setPosition(0);
            players.get(i).setPrison(0);
        }
        repo.insertPlayer(players);
    }

    public void finishGame(){
        //radi nesto
    }

    //promeni ovo
    public int getUserCount(){
        return playerCnt;
    }

    public int getOldPossition() {
        return oldPossition;
    }


    public void rollTheDice(){
        dice1=(int)(Math.random()*6)+1;
        dice2=(int)(Math.random()*6)+1;


        AtomicBoolean finish=new AtomicBoolean(false);
        while(!finish.get()){
            Player e=repo.getPlayer(currentUser,currentGame);
            if(e.getPrison()>0){
                e.setPrison(e.getPrison()-1);
                currentUser=(currentUser+1)%playerCnt;
            }
            else{
                oldPossition=e.getPosition();
                e.setPosition((oldPossition+dice1+dice2)%40);
                attempts++;
                if(dice1!=dice2){
                    currentUser=(currentUser+1)%playerCnt;
                    attempts=0;
                }
                else if(attempts>2){
                    e.setPrison(2);
                    e.setPosition(10); //zatvor
                    currentUser=(currentUser+1)%playerCnt;
                    attempts=0;
                }
                finish.set(true);
            }
            repo.updatePlayer(e);


        }


    }

    public LiveData<List<Player>> getPlayers(){
        return repo.getPlayers(currentGame);
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


    public static GameModel getModel(Repository repo, MainActivity activity){
        ViewModelProvider.Factory factory=new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> aClass) {
                return (T)new GameModel(repo);
            }
        };
        return new ViewModelProvider(activity, factory).get(GameModel.class);
    }
}
