package rs.ac.bg.etf.monopoly;

import androidx.lifecycle.ViewModel;

public class GameModel extends ViewModel {

    private int currentUser=3;

    public int getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(int currentUser) {
        this.currentUser = currentUser;
    }
}
