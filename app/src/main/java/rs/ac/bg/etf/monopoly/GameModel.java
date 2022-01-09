package rs.ac.bg.etf.monopoly;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import rs.ac.bg.etf.monopoly.db.Card;
import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Repository;
import rs.ac.bg.etf.monopoly.property.PropertyModel;

public class GameModel extends ViewModel {

    private static final String  KEY_USER="currentUser";
    private static final String  KEY_DICE="dice";
    private static final String KEY_TIME="time";

    private int currentUser;
    private int lastPlayed=0;
    private LiveData<Integer> dice1;
    private LiveData<Integer> dice2;
    private LiveData<Long> finalTime;
    private MutableLiveData<Card> cardOpen=new MutableLiveData<>(null);
    private int attempts=0;
    private int oldPossition=0;
    private int currentGame;
    private boolean ableToBuy;
    private boolean bought;
    private boolean paid=true;
    private boolean moved;
    private int moneyFromTaxes;
    private MutableLiveData<String> timeString=new MutableLiveData<>("00:00");

    public MutableLiveData<String> getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString.postValue(timeString);
    }

    public LiveData<Long> getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(long finalTime) {
        ssh.set(KEY_TIME,finalTime);
    }

    public boolean isMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public LiveData<Card> getCardOpen() {
        return cardOpen;
    }

    public void setCardOpen(Card cardOpen) {
        this.cardOpen.postValue(cardOpen);
    }

    public List<Player> getAllPlayers(){
        return repo.getAllPlayers(currentGame);
    }

    public int getMoneyFromTaxes() {
        return moneyFromTaxes;
    }

    public void setMoneyFromTaxes(int moneyFromTaxes) {
        this.moneyFromTaxes = moneyFromTaxes;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public void setBought(boolean bought) {
        this.bought = bought;
    }

    public boolean isBought() {
        return bought;
    }

    public boolean isAbleToBuy() {
        return ableToBuy;
    }

    public void setAbleToBuy(boolean ableToBuy) {
        this.ableToBuy = ableToBuy;
    }

    private int playerCnt;
    private SavedStateHandle ssh;
    private Repository repo;
    private ArrayList<MutableLiveData<Integer>> possitions=new ArrayList<>();

    public GameModel(SavedStateHandle ssh){
        this.ssh=ssh;

        dice1= Transformations.map(ssh.getLiveData(KEY_DICE+1, 0), saved->{
            return saved;
        });
        dice2= Transformations.map(ssh.getLiveData(KEY_DICE+2, 0), saved->{
            return saved;
        });

        finalTime=Transformations.map(ssh.getLiveData(KEY_TIME, (long)0), saved->{
            return saved;
        });
    }

    public int getNextGame(){
        return repo.getNextGame();
    }

    public void update(Player p){
        repo.updatePlayer(p);
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
        repo.initProperties(false);
        ableToBuy=false;
        bought=false;
        paid=true;
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


    public Player rollTheDice(LifecycleOwner o){
        bought=false;
        cardOpen.postValue(null);
        int dice1=((int)(Math.random()*6))+1;
        int dice2=((int)(Math.random()*6))+1;

//        int dice1=1;
//        int dice2=1;
        android.os.Handler mainHanfler=new Handler(Looper.getMainLooper());
        mainHanfler.post(()->{
            ssh.set(KEY_DICE+1,dice1);
            ssh.set(KEY_DICE+2,dice2);
        });
        AtomicReference<Player> e=new AtomicReference<>();
        AtomicBoolean finish=new AtomicBoolean(false);
        while(!finish.get()){
            lastPlayed=currentUser;
            e.set(repo.getPlayer(currentUser,currentGame));
            if(e.get().getPrison()>0 || e.get().getMoney()==-1){
                attempts=0;
                if(e.get().getMoney()!=-1) e.get().setPrison(e.get().getPrison()-1);
                currentUser=(currentUser+1)%playerCnt;
            }
            else{
                oldPossition=e.get().getPosition();
                e.get().setPosition((oldPossition+dice1+dice2)%40);
                if(oldPossition>(oldPossition+dice1+dice2)%40){
                    e.get().setMoney(e.get().getMoney()+200);
                }
                attempts++;
                if(dice1!=dice2){
                    currentUser=(currentUser+1)%playerCnt;
                    attempts=0;
                }
                else if(attempts>2){
                    if(e.get().getPrison()<0){
                        e.get().setPrison(e.get().getPrison()+1);
                    }
                    else{
                        e.get().setPrison(2);
                        e.get().setPosition(10); //zatvor
                    }
                    currentUser=(currentUser+1)%playerCnt;
                    attempts=0;
                }
                finish.set(true);

            }
            repo.updatePlayer(e.get());
        }
        return e.get();

    }

    public Player getPlayer(int ind){
        return repo.getPlayer(ind,currentGame);
    }

    public LiveData<List<Player>> getPlayers(){
        return repo.getPlayers(currentGame);
    }

    public ArrayList<MutableLiveData<Integer>> getPossitions() {
        return possitions;
    }


    public LiveData<Integer> getDice1() {
        return dice1;
    }



    public LiveData<Integer> getDice2() {
        return dice2;
    }



    public int getCurrentUser() {
        return currentUser;
    }

    public int getLastPlayer() {
        return lastPlayed;
    }

    public void setCurrentUser(int currentUser) {
        this.currentUser=currentUser;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }

    public static GameModel getModel(Repository repo, MainActivity activity){
        GameModel model=new ViewModelProvider(activity).get(GameModel.class);
        model.setRepo(repo);
        return model;
    }
}
