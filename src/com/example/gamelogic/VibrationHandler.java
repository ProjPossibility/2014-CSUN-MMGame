package com.example.gamelogic;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Vibrator;
import android.view.ContextThemeWrapper;

/**
 * Created by ngorgi on 1/14/14.
 */
public class VibrationHandler {
    Vibrator vibrator;
    boolean rejectNew = false;
    Handler mHandler;
    Context context;

    public VibrationHandler(Context context) {
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        mHandler = new Handler();
        this.context = context;
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

    public void pulsePosition() {
        long[] pattern = {0, 100, 200, 100, 200};
        if (!rejectNew) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.positionsounds);
            vibrator.vibrate(pattern, -1);

            mediaPlayer.start();
            mHandler.postDelayed(vibrationComplete, patternSum(pattern));
        }
    }

    public void pulseGo() {
        long[] pattern = {0, 200, 300, 200, 300, 200, 300, 450};
        if (!rejectNew) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.gosound);
            vibrator.vibrate(pattern, -1);

            mediaPlayer.start();
            mHandler.postDelayed(vibrationComplete, patternSum(pattern));
        }
    }

    public void pulseBear() {
        long[] pattern = {0, 950, 200, 950};
        if (!rejectNew) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.bearsound);
            vibrator.vibrate(pattern, -1);

            mediaPlayer.start();
            mHandler.postDelayed(vibrationComplete, patternSum(pattern));
        }
    }

    public void pulseNinja() {
        long[] pattern = {0, 60, 65, 60, 65, 70, 65, 70, 65, 75, 70, 75, 70, 80, 75, 80, 75, 85, 80};
        if (!rejectNew) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ninjasound);
            vibrator.vibrate(pattern, -1);

            mediaPlayer.start();
            mHandler.postDelayed(vibrationComplete, patternSum(pattern));
        }
    }

    public void pulseCowboy() {
        long[] pattern = {0, 100, 200, 100, 0};
        if (!rejectNew) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.cowboysound);
            vibrator.vibrate(pattern, -1);

            mediaPlayer.start();
            mHandler.postDelayed(vibrationComplete, patternSum(pattern));
        }
    }

    public void pulseWin() {
        long[] pattern = {0, 100, 200, 100, 100, 100, 100, 400};
        if (!rejectNew) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.winnersound1);
            vibrator.vibrate(pattern, -1);

            mediaPlayer.start();
            mHandler.postDelayed(vibrationComplete, patternSum(pattern));
        }
    }

    public void pulseLose() {
        long[] pattern = {0, 500, 200, 500, 200, 450};
        if (!rejectNew) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.losingsound1);
            vibrator.vibrate(pattern, -1);

            mediaPlayer.start();
            mHandler.postDelayed(vibrationComplete, patternSum(pattern));
        }
    }

    public void pulseDraw() {
        long[] pattern = {0, 100};
        if (!rejectNew) {
            MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.drawsound1);
            vibrator.vibrate(pattern, -1);

            mediaPlayer.start();
            mHandler.postDelayed(vibrationComplete, patternSum(pattern));
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

    private Runnable vibrationComplete = new Runnable() {
        @Override
        public void run() {


                if (vibrationCompletedInterface != null) {
                    vibrationCompletedInterface.vibrationCompleted();
                }



        }
    };

    private long patternSum(long[] pattern) {
        long sum = 0;
        for (long l : pattern) {
             sum += l;
        }
        return sum +750;
    }
}

