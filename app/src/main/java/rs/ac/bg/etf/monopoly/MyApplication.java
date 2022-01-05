package rs.ac.bg.etf.monopoly;

import android.app.Application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyApplication extends Application {

    ExecutorService executorService = Executors.newFixedThreadPool(4);

    public ExecutorService getExecutorService() {
        return executorService;
    }
}
