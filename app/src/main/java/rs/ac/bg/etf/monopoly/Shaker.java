package rs.ac.bg.etf.monopoly;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class Shaker implements DefaultLifecycleObserver {

    private SensorManager manager;
    private long lastUpdate;
    private static final int SHAKE_TRESHOLD=200;
    private float lastAxis[]=new float[3];
    private Context context;
    private boolean active=false;
    private Callback start;
    private Callback end;

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
                if(speed>SHAKE_TRESHOLD && !shakingDetected){
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

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
        active=false;
        //end.call();
    }



    public Shaker(Context context, Callback end, Callback start){
        this.context=context;
        this.start=start;
        this.end=end;
    }
}
