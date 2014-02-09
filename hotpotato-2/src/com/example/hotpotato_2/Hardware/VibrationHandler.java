package com.example.hotpotato_2.Hardware;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Vibrator;

/**
 * Created by ngorgi on 1/14/14.
 */
public class VibrationHandler {
    Vibrator vibrator;
    boolean rejectNew = false;
    Handler mHandler;

    public VibrationHandler(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mHandler = new Handler();

    }

    public void playPulse(int millis) {
        vibrator.vibrate(millis);
    }

    public void playIntensity(int intensity) {

        int onTime = (int) (30 * ((float) intensity / 100));
        if (onTime > 100) {
            onTime = 100;
        }
        int offTime = 30 - onTime;
        if (offTime < 0) {
            offTime = 0;
        }

        long[] pattern = {0, onTime, offTime};
        if (!rejectNew) {
            vibrator.vibrate(pattern, 0);
        }
    }

    public void stopVibrate() {
        vibrator.cancel();
    }

    public void pulsePosition()
    {
        long[] pattern = {0,100,200,100,200};
        if (!rejectNew) {
            vibrator.vibrate(pattern, -1);
            mHandler.postDelayed(vibrationComplete, 750);
        }
    }

    public void pulseGo()
    {
        long[] pattern = {0,200,300,200,300,200, 300, 450};
        if (!rejectNew) {
            vibrator.vibrate(pattern, -1);
            mHandler.postDelayed(vibrationComplete, 1000);
        }
    }

    public void pulseBear() {
        long[] pattern = {0,950,200,950};
        if (!rejectNew) {
            vibrator.vibrate(pattern, -1);
            mHandler.postDelayed(vibrationComplete, 750);
        }
    }

    public void pulseNinja()
    {
        long[] pattern = {0,60,65,60,65,70,65,70,65,75,70,75,70,80,75,80,75,85,80};
        if (!rejectNew) {
            vibrator.vibrate(pattern, -1);
            mHandler.postDelayed(vibrationComplete, 750);
        }
    }

    public void pulseCowboy()
    {
        long[] pattern = {0,100,200,100,0};
        if (!rejectNew) {
            vibrator.vibrate(pattern, -1);
            mHandler.postDelayed(vibrationComplete, 750);
        }
    }

    public void pulseWin() {
        long[] pattern = {0, 100, 200, 100, 100, 100, 100, 400};
        if (!rejectNew) {
            vibrator.vibrate(pattern, -1);
            mHandler.postDelayed(vibrationComplete, 750);
        }
    }

    public void pulseLose() {
        long[] pattern = {0, 500, 200, 500, 200, 450};
        if (!rejectNew) {
            vibrator.vibrate(pattern, -1);
            mHandler.postDelayed(vibrationComplete, 750);
        }
    }

    public void pulseDraw()
    {
        long[] pattern = {0,100};
        if (!rejectNew) {
            vibrator.vibrate(pattern, -1);
            mHandler.postDelayed(vibrationComplete, 750);
        }
    }

    VibrationCompletedInterface vibrationCompletedInterface;

    public void setVibrationCompletedInterface(VibrationCompletedInterface vibrationCompletedInterface) {
        this.vibrationCompletedInterface = vibrationCompletedInterface;
    }

    public interface VibrationCompletedInterface {
        /**
         * The previously triggered notifiable vibration has completed.
         */
        public void vibrationCompleted();
    }


    public void playGameStartNotified() {
        vibrator.cancel();
        rejectNew = true;
        long[] happyPattern = {0, 100, 100, 100, 100, 100, 100};
        long duration = 0;
        for (long l : happyPattern) {
            duration = duration + l;
        }
        mHandler.postDelayed(vibrationComplete, 750);
        vibrator.vibrate(happyPattern, -1);
    }


    private Runnable vibrationComplete = new Runnable() {
        @Override
        public void run() {

            if (rejectNew) {
                if (vibrationCompletedInterface != null) {
                    vibrationCompletedInterface.vibrationCompleted();
                }
                rejectNew = false;
            }

        }
    };
}

