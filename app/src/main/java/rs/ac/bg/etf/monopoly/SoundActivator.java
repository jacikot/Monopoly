package rs.ac.bg.etf.monopoly;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import java.io.File;
import java.io.IOException;


public class SoundActivator implements DefaultLifecycleObserver {

    private boolean started=false;
    private MediaPlayer player;


    public void start(Context context){
        String path=context.getFilesDir().getAbsolutePath()+ File.separator+"wuerfelbecher.wav";
        player=new MediaPlayer();
        try {
            player.setDataSource(path);
            player.setOnPreparedListener(e->{
                e.start();
                started=true;
            });
            player.setOnCompletionListener(e->{
                if(started){
                    e.start();
                }
            });
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop(){
        if(started){
            started=false;
            player.stop();
            player.release();
        }
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        stop();
    }
}
