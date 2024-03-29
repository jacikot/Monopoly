package rs.ac.bg.etf.monopoly;

import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class Shaker implements DefaultLifecycleObserver {

    private SensorManager manager;
    private long lastUpdate;
    private int SHAKE_TRESHOLD;
    private float lastAxis[]=new float[3];
    private MainActivity context;
    private boolean active=false;
    private Callback start;
    private Callback end;
    private boolean opened=false;

    private boolean shakingDetected;

    public interface Callback{
        boolean call();
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        active=true;
        manager=context.getSystemService(SensorManager.class);
        manager.registerListener(new SensorListener(),manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);
    }

    public class SensorListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {
            if(!active) return;
            long currTime=System.currentTimeMillis();
            if((currTime-lastUpdate)>100){
                long diffTime=currTime-lastUpdate;
                lastUpdate=currTime;
                float x=event.values[0];
                float y=event.values[1];
                float z=event.values[2];
                float speed=Math.abs(x+y+z-lastAxis[0]-lastAxis[1]-lastAxis[2])/diffTime*1000;
                float a=(float) Math.abs(Math.sqrt(x*x+y*y+z*z)
                        -Math.sqrt(lastAxis[0]*lastAxis[0]+lastAxis[1]*lastAxis[1]+lastAxis[2]*lastAxis[2]));
                lastAxis[0] = x;
                lastAxis[1] = y;
                lastAxis[2] = z;
                Log.d("accel",""+x+" "+y+" "+z+" "+speed+" "+Math.sqrt(x*x+y*y+z*z)+" "+Math.sqrt(x*x+y*y+z*z));
                SHAKE_TRESHOLD=preferences.getInt(SettingsFragment.SENSITIVITY_KEY,SHAKE_TRESHOLD);
                if(speed>SHAKE_TRESHOLD && !shakingDetected){
                    if(preferences.getBoolean(SettingsFragment.DIALOG_KEY,false)
                            && !preferences.getBoolean(SettingsFragment.DIALOG_PRESSED_KEY,false)){
                        if(!opened){
                            opened=true;
                            showDialog();
                        }
                        return;
                    }
                    shakingDetected=true;
                    start.call();

                }
                if(a<1 && Math.sqrt(x*x+y*y+z*z)<10 &&shakingDetected){
                    shakingDetected=false;
                    active=!end.call();
                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }

    private void showDialog(){
        AlertDialog dd=new MaterialAlertDialogBuilder(context)
                .setTitle("Da li ste spremni za bacanje kockica?")
                .setPositiveButton((CharSequence) "Potvrdi",(dialog, which) -> {
                    preferences.edit().putBoolean(SettingsFragment.DIALOG_PRESSED_KEY,true).commit();
                    dialog.cancel();
                    opened=false;

                })
                .create();
        dd.setCanceledOnTouchOutside(false);
        dd.show();
    }

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        active=false;
        //end.call();
    }



    private SharedPreferences preferences;
    public Shaker(MainActivity context, Callback end, Callback start){
        this.context=context;
        this.start=start;
        this.end=end;
        preferences=context.getSharedPreferences(MainActivity.shared_NAME, Context.MODE_PRIVATE);
        SHAKE_TRESHOLD=preferences.getInt(SettingsFragment.SENSITIVITY_KEY,10000);
    }
}
