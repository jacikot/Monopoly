package rs.ac.bg.etf.monopoly;

import android.content.SharedPreferences;
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
import java.util.Timer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import rs.ac.bg.etf.monopoly.db.Card;
import rs.ac.bg.etf.monopoly.db.Game;
import rs.ac.bg.etf.monopoly.db.Player;
import rs.ac.bg.etf.monopoly.db.Property;
import rs.ac.bg.etf.monopoly.db.Repository;
import rs.ac.bg.etf.monopoly.property.PropertyModel;

public class GameModel extends ViewModel {

    private static final String  KEY_USER="currentUser";
    private static final String  KEY_DICE="dice";
    private static final String KEY_TIME="time";

    private int currentUser;
    private int lastPlayed=0;
    private MutableLiveData<Integer> dice1=new MutableLiveData<>();
    private MutableLiveData<Integer> dice2=new MutableLiveData<>();
    private long finalTime;
    private Timer timer;
    private MutableLiveData<Card> cardOpen=new MutableLiveData<>(null);
    private int attempts=0;
    private int oldPossition=0;
    private int currentGame;
    private boolean ableToBuy;
    private boolean bought;
    private boolean paid=true;
    private boolean moved;
    private int moneyFromTaxes;

    public Timer getTimer() {
        return timer;
    }

    public void stopTimer(){
        if(timer!=null) timer.cancel();
    }

    public void setTimer(Timer timer) {
        this.timer = timer;
    }

    public int getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(int currentGame) {
        this.currentGame = currentGame;
    }

    private MutableLiveData<String> timeString=new MutableLiveData<>("00:00");

    public MutableLiveData<String> getTimeString() {
        return timeString;
    }

    public void setTimeString(String timeString) {
        this.timeString.postValue(timeString);
    }

    public long getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(long finalTime) {
        preferences.edit().putLong(KEY_TIME,finalTime).commit();
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
    public List<Player> getAllPlayersEveryGame(){
        return repo.getAllGamePlayers();
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
    private Repository repo;
    private ArrayList<MutableLiveData<Integer>> possitions=new ArrayList<>();

    public GameModel(){ }

    private SharedPreferences preferences;
    public void setSharedPrefs(SharedPreferences pref){
        preferences=pref;
        finalTime=preferences.getLong(KEY_TIME,0);
        dice1.postValue(preferences.getInt(KEY_DICE+1,0));
        dice2.postValue(preferences.getInt(KEY_DICE+2,0));
    }

    public int getNextGame(){
        return repo.getNextGame();
    }

    public void startNextGame(){
        currentGame= repo.startGame();
    }

    public int calculateWorth(Player p){
        if(p.getMoney()==-1) return -1;
        int worth= p.getMoney();
        List<Property> properties=repo.getOfHolder(p.getIndex());
        for(Property prop:properties){
            worth+=prop.getProperty_price();
            if(prop.getType()==0){
                worth+=prop.getBuilding_price()*prop.getHouses();
            }
        }
        return worth;
    }

    public void finishGame(){
        repo.finishCurrentGame();
        repo.getAllPlayers(currentGame).forEach(p->{
            p.setEvaluation(calculateWorth(p));
            repo.updatePlayer(p);
        });
        if(timer!=null) timer.cancel();
    }

    public void update(Player p){
        repo.updatePlayer(p);
    }

    public void insertPlayer(Player p){
        ArrayList<Player> list=new ArrayList<>();
        list.add(p);
        repo.insertPlayer(list);
    }
    public void startGame(){
        currentGame=repo.getNextGame()-1;
        playerCnt=repo.getAllPlayers(currentGame).size();
        repo.initProperties(false);
        ableToBuy=false;
        bought=false;
        paid=true;
    }


    //promeni ovo
    public int getUserCount(){
        return playerCnt;
    }

    public int getOldPossition() {
        return oldPossition;
    }

    public void setOldPossition(int pos){
        oldPossition=pos;
    }

    public List<Game> getAllFinishedGames(){
        return repo.getAllFinishedGames();
    }

    public Player rollTheDice(LifecycleOwner o){
        bought=false;
        cardOpen.postValue(null);
        int dice1=((int)(Math.random()*6))+1;
        int dice2=((int)(Math.random()*6))+1;

        preferences.edit().putInt(KEY_DICE+1,dice1).putInt(KEY_DICE+2,dice2).commit();
        this.dice1.postValue(dice1);
        this.dice2.postValue(dice2);

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
