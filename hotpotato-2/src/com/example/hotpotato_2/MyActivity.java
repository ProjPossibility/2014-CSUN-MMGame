package com.example.hotpotato_2;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.hotpotato_2.Communication.Client;
import com.example.hotpotato_2.Communication.CommsHandler;
import com.example.hotpotato_2.Communication.Server;
import com.example.hotpotato_2.GameLogic.ClientGame;
import com.example.hotpotato_2.GameLogic.GameHandler;
import com.example.hotpotato_2.GameLogic.ServerGame;
import com.example.hotpotato_2.Hardware.SensorsHandler;
import com.example.hotpotato_2.Hardware.VibrationHandler;

public class MyActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    Context context;
    VibrationHandler vibrationHandler;
    SensorsHandler sensorsHandler;
    GameHandler gameHandler;
    Button btnPosition, btnGo, btnBear, btnNinja, btnCowboy, btnWin, btnLose, btnDraw;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        btnPosition = (Button) findViewById(R.id.btnPosition);
        btnPosition.setOnClickListener(onClickListener);
        btnGo = (Button) findViewById(R.id.btnGo);
        btnGo.setOnClickListener(onClickListener);
        btnBear = (Button) findViewById(R.id.btnBear);
        btnBear.setOnClickListener(onClickListener);
        btnNinja = (Button) findViewById(R.id.btnNinja);
        btnNinja.setOnClickListener(onClickListener);
        btnCowboy = (Button) findViewById(R.id.btnCowboy);
        btnCowboy.setOnClickListener(onClickListener);
        btnWin = (Button) findViewById(R.id.btnWin);
        btnWin.setOnClickListener(onClickListener);
        btnLose = (Button) findViewById(R.id.btnLose);
        btnLose.setOnClickListener(onClickListener);
        btnDraw = (Button) findViewById(R.id.btnDraw);
        btnDraw.setOnClickListener(onClickListener);






        vibrationHandler = new VibrationHandler(this);
        sensorsHandler = new SensorsHandler(this);
        context = this;
    }

    SensorsHandler.SensorHandlerInterface sensorHandlerInterface = new SensorsHandler.SensorHandlerInterface() {
        @Override
        public void newAccelData(float[] data) {

        }

        @Override
        public void newGyroData(float[] data) {
//            StringBuilder stringBuilder = new StringBuilder();
//            for (float f : data) {
//                stringBuilder.append(f);
//                stringBuilder.append("   ");
//            }
//            Log.i("workshop", stringBuilder.toString());
//            int intensity = ((int) Math.abs(data[1] * 5));
//            vibrationHandler.playIntensity(intensity);

//            StringBuilder stringBuilder = new StringBuilder();
//            for (float f : data) {
//                stringBuilder.append(f);
//                stringBuilder.append(",");
//            }
//            commsHandler.writeData(stringBuilder.toString());

        }

        @Override
        public void newOrientationData(float[] data) {
            StringBuilder stringBuilder = new StringBuilder();
            StringBuilder stringBuilder2 = new StringBuilder();

            for (float f : data) {
                stringBuilder.append(f);
                stringBuilder2.append(f);
                stringBuilder.append("\n");
                stringBuilder2.append("\t");
            }
            Log.i("workshop", stringBuilder2.toString());
        }

        @Override
        public void gotPull(int intensity) {

        }
    };


    CommsHandler commsHandler;

    public View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(btnPosition))
            {
                vibrationHandler.pulsePosition();
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.positionsounds);
                mediaPlayer.start();
            }
           else if (v.equals(btnGo))
            {
                vibrationHandler.pulseGo();
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.gosound);
                mediaPlayer.start();
            }
            else if (v.equals(btnBear))
            {
                vibrationHandler.pulseBear();
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.bearsound);
                mediaPlayer.start();
            }
            else if (v.equals(btnNinja))
            {
                vibrationHandler.pulseNinja();
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.ninjasound);
                mediaPlayer.start();
            }
            else if (v.equals(btnCowboy))
            {
                vibrationHandler.pulseCowboy();
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.cowboysound);
                mediaPlayer.start();
            }
            else if (v.equals(btnWin))
            {
                vibrationHandler.pulseWin();
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.winnersound1);
                mediaPlayer.start();
            }
            else if (v.equals(btnLose))
            {
                vibrationHandler.pulseLose();
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.losingsound1);
                mediaPlayer.start();

            }
            else if (v.equals(btnDraw))
            {
                vibrationHandler.pulseDraw();
                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.drawsound1);
                mediaPlayer.start();
            }

        }
    };



    CommsHandler.CommsHandlerInterface commsHandlerInterface =
            new CommsHandler.CommsHandlerInterface() {
                @Override
                public void connectionStateChanged(int newState) {
                    switch (newState) {
                        case CommsHandler.STATE_CONNECTION_ESTABLISHED:
                            if (commsHandler.isServer) {
                                sensorsHandler.startPolling();
                            }
                            break;
                        case CommsHandler.STATE_WAITING_FOR_CONNECTION:
                            break;
                        case CommsHandler.STATE_CONNECTION_FINISHED:
                            break;
                    }
                }

                @Override
                public void dataReceived(String data) {

                    final String ns = data;
//                    runOnUiThread();
                    String[] strings = data.split(",");
                    double mag = 0.0f;
                    for (int i = 0; i < strings.length; i++) {
                        double f = new Double(strings[i]);
                        mag += Math.pow(f, 2);
                    }
                    mag = Math.sqrt(mag);
                    mag *= 3;
                    vibrationHandler.playIntensity((int) mag);
//                    final double mag2 = mag;
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
////                            accessibilityHandler.announce(ns);//                    txtInfo.setText(String.valueOf(ns));
//                            txtInfo.setText(String.valueOf(mag2));
//                        }
//                    });
//                    Log.i("workshop", "received " + data);
                }
            };

    GameHandler.GameHandlerInterface gameHandlerInterface = new GameHandler.GameHandlerInterface() {
        @Override
        public void newGameState(int newState) {

        }
    };
}





















