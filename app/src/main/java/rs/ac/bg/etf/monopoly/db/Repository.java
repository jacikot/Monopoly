package rs.ac.bg.etf.monopoly.db;

import android.content.res.TypedArray;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import rs.ac.bg.etf.monopoly.MainActivity;
import rs.ac.bg.etf.monopoly.MyApplication;
import rs.ac.bg.etf.monopoly.R;

public class Repository {


    private PropertyDAO property;
    private PlayerDAO player;
    private CardDAO card;
    private MainActivity activity;
    private GameDAO game;
    private MoveDao move;
    private SellingDao selling;

    public Repository(MainActivity activity,
                      PropertyDAO dao,
                      PlayerDAO dao2,
                      CardDAO cards,
                      GameDAO games,
                      MoveDao moves,
                      SellingDao sellings){
        property=dao;
        player=dao2;
        card=cards;
        game=games;
        move=moves;
        selling=sellings;
        this.activity=activity;
    }

    public void insertMove(Move m){
        move.insertMove(m);
        m.getList().forEach(s->{
            selling.insert(s);
        });
    }

    public int startGame(){
        game.unfinished();
        game.insertGame(new Game(1));
        return game.getCurrentGameId();
    }

    public void finishCurrentGame(){
        game.finish(new Date().getTime());
    }

    public List<Game> getAllFinishedGames(){
        return game.getAllFinishedGames();
    }

    public List<Player> getAllGamePlayers(){
        return player.getAllGamePlayers();
    }

    public void deleteAllGames(){
        player.deleteAll();
        game.deleteAll();
        move.deleteAll();
        selling.deleteAll();
    }

    public void insert(Property p){
        property.insert(p);
    }

    public LiveData<Property> getProperty(int id){
        return property.getProperty(id);
    }
    public Property getPropertyBlocking(int id){
        return property.getPropertyBlocking(id);
    }

    public void update(Property p){
        ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
            property.update(p);
        });
    }

    public void insertCards(){
        String[] text=activity.getResources().getStringArray(R.array.text);
        TypedArray type=activity.getResources().obtainTypedArray(R.array.type_card);
        TypedArray movement=activity.getResources().obtainTypedArray(R.array.movement);
        TypedArray prison=activity.getResources().obtainTypedArray(R.array.prison);
        TypedArray money=activity.getResources().obtainTypedArray(R.array.money);
        TypedArray payementType=activity.getResources().obtainTypedArray(R.array.to_whom);

        ArrayList<Card> cards=new ArrayList<>();
        for(int i=0;i<text.length;i++){
            Card c=new Card(i,type.getInt(i,0),movement.getInt(i,0),money.getInt(i,0),
                   payementType.getInt(i,0),prison.getInt(i,0),text[i]);
            cards.add(c);
        }

        ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
            card.insert(cards);
        });
    }

    public List<Property> getOfHolder(int h){
        return property.getOfHolder(h);
    }

    public List<Card> getCardsForType(int t){
        return card.getCardsForType(t);
    }

    public List<Property> getOfType(int type){
        return property.getOfType(type);
    }

    public List<Player> getAllPlayers(int game){
        return player.getAllPlayers(game);
    }



    public void initProperties(boolean insert){
        TypedArray type=activity.getResources().obtainTypedArray(R.array.type);
        TypedArray group=activity.getResources().obtainTypedArray(R.array.group);
        TypedArray price=activity.getResources().obtainTypedArray(R.array.price);
        TypedArray house=activity.getResources().obtainTypedArray(R.array.house);
        TypedArray z1=activity.getResources().obtainTypedArray(R.array.z1);
        TypedArray z2=activity.getResources().obtainTypedArray(R.array.z2);
        TypedArray z3=activity.getResources().obtainTypedArray(R.array.z3);
        TypedArray z4=activity.getResources().obtainTypedArray(R.array.z4);
        TypedArray z5=activity.getResources().obtainTypedArray(R.array.z5);
        TypedArray z6=activity.getResources().obtainTypedArray(R.array.z6);

        for(int j=0;j<type.length();j++){
            int i=j;
            ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
                Property p=new Property(i,
                        type.getInt(i,0),
                        group.getInt(i,0),
                        price.getInt(i,0),
                        house.getInt(i,0),
                        z1.getInt(i,0),
                        z2.getInt(i,0),
                        z3.getInt(i,0),
                        z4.getInt(i,0),
                        z5.getInt(i,0),
                        z6.getInt(i,0)
                );
                if(insert)property.insert(p);
                else property.update(p);
            });

        }
    }

    public LiveData<List<Property>> getTypeOfHolder(int h, int t){
        return property.getTypeOfHolder(h,t);
    }

    public List<Property> getTypeOfHolderBlocking(int h, int t){
        return property.getTypeOfHolderBlocking(h,t);
    }

    public int getCountSameColor(int color){
        return property.getSameColorCnt(color);
    }

    public int getNextGame(){
        return player.getNextGame();
    }

    public void insertPlayer(List<Player> p){
        ((MyApplication)activity.getApplication()).getExecutorService().execute(()->{
            player.insert(p);
        });
    }

    public LiveData<List<Player>> getPlayers(int game){
        return player.getPlayers(game);
    }

    public Player getPlayer(int p, int game){
        return player.getPosition(p,game);
    }

    public void updatePlayer(Player p){
        player.update(p); //ne zove se u main niti
    }
}
