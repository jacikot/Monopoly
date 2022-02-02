package rs.ac.bg.etf.monopoly.db;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Move {
    @PrimaryKey(autoGenerate = true)
    int idMove;

    int game;
    int player;
    int positionTo;
    int buyAction;
    int cardOpen;

    @Ignore
    List<Selling> list=new ArrayList<>();

    @Ignore
    public void addSelling(Selling s){
        list.add(s);
    }

    @Ignore
    public List<Selling> getList() {
        return list;
    }

    @Ignore
    public Move(){

    }
    public Move(int idMove, int game, int player, int positionTo, int buyAction, int cardOpen) {
        this.idMove = idMove;
        this.game = game;
        this.player = player;
        this.positionTo = positionTo;
        this.buyAction = buyAction;
        this.cardOpen = cardOpen;
    }

    public int getGame() {
        return game;
    }

    public void setGame(int game) {
        this.game = game;
    }

    public int getIdMove() {
        return idMove;
    }

    public void setIdMove(int idMove) {
        this.idMove = idMove;
    }

    public int getPlayer() {
        return player;
    }

    public void setPlayer(int player) {
        this.player = player;
    }

    public int getPositionTo() {
        return positionTo;
    }

    public void setPositionTo(int positionTo) {
        this.positionTo = positionTo;
    }

    public int getBuyAction() {
        return buyAction;
    }

    public void setBuyAction(int buyAction) {
        this.buyAction = buyAction;
    }

    public int getCardOpen() {
        return cardOpen;
    }

    public void setCardOpen(int cardOpen) {
        this.cardOpen = cardOpen;
    }
}
