package com.example.hotpotato_2.GameLogic;

import android.content.Context;
import android.util.Log;
import com.example.hotpotato_2.Communication.Client;
import com.example.hotpotato_2.Communication.CommsHandler;
import com.example.hotpotato_2.Hardware.SensorsHandler;
import com.example.hotpotato_2.Hardware.VibrationHandler;

/**
 * Created by ngorgi on 1/15/14.
 */
public class ClientGame extends GameHandler {

    public ClientGame(Context context, SensorsHandler sensorsHandler, VibrationHandler vibrationHandler, GameHandlerInterface gameHandlerInterface) {
        super(context, sensorsHandler, vibrationHandler, gameHandlerInterface);
        sensorsHandler.setSensorHandlerInterface(sensorHandlerInterface);
    }


    @Override
    public void establishNetworkConnection(int port, String ip) {
        super.establishNetworkConnection(port, ip);
        commsHandler = new Client(port, ip, commsHandlerInterface);
        commsHandler.establishConnection();
    }

    @Override
    public void gameStateChanged(int newState, boolean broadcast) {
        super.gameStateChanged(newState, broadcast);
    }

    @Override
    public void shutDown() {
        super.shutDown();
    }

    CommsHandler.CommsHandlerInterface commsHandlerInterface = new CommsHandler.CommsHandlerInterface() {
        @Override
        public void connectionStateChanged(int newState) {
            Log.i("workshop", "client conn state changed " + newState);
        }

        @Override
        public void dataReceived(String data) {
            Log.i("workshop", "client got data " + data);
            if (data.startsWith("GS=")) {
                int newGameState = data.charAt(3) - '0';
                switch (newGameState) {
                    case STATE_GET_IN_POSITIONS:
                        if (currentGameState == STATE_GAME_NOT_STARTED) {
                            sensorsHandler.startPolling();
                        }
                        gameHandlerInterface.newGameState(newGameState);
                        currentGameState = newGameState;
                        break;
                    case STATE_GAME_STARTED:
                        vibrationHandler.playGameStartNotified();
                        currentGameState = newGameState;
                        gameHandlerInterface.newGameState(newGameState);

                        break;
                    case STATE_SERVER_STARTED_TURNING:
                        break;
                    case STATE_GAME_FINISHED:
                        boolean clientWon = data.charAt(4) == '1';
                        currentGameState = STATE_GAME_FINISHED;
                        if (clientWon) {
                            vibrationHandler.pulseWin();
                        }   else {
                            vibrationHandler.pulseLose();
                        }

                        break;
                }
            } else if (data.startsWith("SV=")) {
                int intensity = new Integer(data.substring(3));
                vibrationHandler.playIntensity(intensity);

            }

        }
    };

    SensorsHandler.SensorHandlerInterface sensorHandlerInterface = new SensorsHandler.SensorHandlerInterface() {
        @Override
        public void newAccelData(float[] data) {
            switch (currentGameState) {
                case STATE_GET_IN_POSITIONS:

                    break;
                case STATE_GAME_STARTED:
                    double mag = Math.abs(data[1]);
                    mag *= 10;
                    commsHandler.writeData("SV=" + String.valueOf((int) mag));

                    break;
                case STATE_SERVER_STARTED_TURNING:
                    break;
                case STATE_GAME_FINISHED:
                    break;
            }
        }

        @Override
        public void newGyroData(float[] data) {

        }

        @Override
        public void newOrientationData(float[] data) {
            switch (currentGameState) {
                case STATE_GET_IN_POSITIONS:
                    isInPosition = Math.abs(Math.abs(data[1]) - 180) < 10 && Math.abs(data[2]) < 10;
                    commsHandler.writeData("IP=" + (isInPosition ? "1" : "0"));
                    break;
                case STATE_GAME_STARTED:
                    break;
                case STATE_SERVER_STARTED_TURNING:
                    break;
                case STATE_GAME_FINISHED:
                    break;
            }
        }

        @Override
        public void gotPull(int intensity) {
            if (currentGameState == STATE_GAME_STARTED) {
                commsHandler.writeData("GP");
                Log.i("workshop", String.valueOf(intensity));
            }
        }
    };
}
