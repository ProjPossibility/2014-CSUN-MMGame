package com.example.gamelogic;

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
    Sensor sensorAccel, sensorOrientation;

    public SensorsHandler(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
    }

    public void stopPolling() {
        if (isPolling) {
            sensorManager.unregisterListener(eventListener);
            isPolling = false;
        }
    }

    public void startPolling() {
        isPolling = true;
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        sensorOrientation = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        sensorManager.registerListener(eventListener, sensorAccel, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(eventListener, sensorOrientation, SensorManager.SENSOR_DELAY_GAME);

    }

    SensorEventListener eventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            synchronized (this) {
                switch (event.sensor.getType()) {
                    case Sensor.TYPE_LINEAR_ACCELERATION:
                        sensorHandlerInterface.newAccelData(event.values);
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



        public void newOrientationData(float[] data);

    }

}
