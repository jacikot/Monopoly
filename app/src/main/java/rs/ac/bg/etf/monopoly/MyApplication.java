package rs.ac.bg.etf.monopoly;

import android.app.Application;
import android.content.Context;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {

    ExecutorService executorService = Executors.newFixedThreadPool(4);

    public ExecutorService getExecutorService() {
        return executorService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        getSharedPreferences(MainActivity.shared_NAME, Context.MODE_PRIVATE).edit().putBoolean(GameModel.GAME_STARTED,false).commit();
    }
}
