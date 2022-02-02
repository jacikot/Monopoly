package rs.ac.bg.etf.monopoly.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Move {
    @PrimaryKey(autoGenerate = true)
    int idMove;

    int game;
    int player;
    int positionTo;
    int buyAction;
    int cardOpen;

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
