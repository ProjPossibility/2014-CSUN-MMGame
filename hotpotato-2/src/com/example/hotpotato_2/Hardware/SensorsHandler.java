package com.example.hotpotato_2.Hardware;

import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

/**
 * Created by ngorgi on 1/14/14.
 */
public class SensorsHandler {
    SensorManager sensorManager;
    boolean isPolling, hasGyro;
    boolean beingPulled = false;
    double maBIG;
    static int maCount = 5;

    public void setSensorHandlerInterface(SensorHandlerInterface sensorHandlerInterface) {
        this.sensorHandlerInterface = sensorHandlerInterface;
    }

    SensorHandlerInterface sensorHandlerInterface;
    Sensor sensorAccel, sensorGyro, sensorOrientation;

    public SensorsHandler(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        hasGyro = hasGyro(context);
    }

    public void stopPolling() {
        if (isPolling) {
            sensorManager.unregisterListener(eventListener);
            isPolling = false;
        }
    }

    public void startPolling() {
        isPolling = true;
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        if (hasGyro) {
            sensorGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
            sensorManager.registerListener(eventListener, sensorGyro, SensorManager.SENSOR_DELAY_GAME);

        }
        sensorManager.registerListener(eventListener, sensorAccel, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(eventListener, sensorOrientation, SensorManager.SENSOR_DELAY_GAME);

    }

    SensorEventListener eventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            synchronized (this) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_ACCELEROMETER:
                        maBIG = maBIG + event.values[1] - maBIG / maCount;
                        final double m = (maBIG / maCount) * -10;
                        boolean isPull = m > 40;
                        if (isPull != beingPulled) {
                            beingPulled = isPull;
                            if (isPull) {
                                sensorHandlerInterface.gotPull((int) m);
                            }
                        }
                        sensorHandlerInterface.newAccelData(event.values);
                        break;
                    case Sensor.TYPE_GYROSCOPE:
                        sensorHandlerInterface.newGyroData(event.values);
                        break;
                    case Sensor.TYPE_ORIENTATION:
                        sensorHandlerInterface.newOrientationData(event.values);
                        break;

                }
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    public interface SensorHandlerInterface {
        public void newAccelData(float[] data);

        public void newGyroData(float[] data);

        public void newOrientationData(float[] data);
       public void gotPull(int intensity);
    }


    public static boolean hasGyro(Context context) {
        PackageManager paM = context.getPackageManager();
        boolean hasGyro = paM.hasSystemFeature(PackageManager.FEATURE_SENSOR_GYROSCOPE);
        return hasGyro;
    }

}
