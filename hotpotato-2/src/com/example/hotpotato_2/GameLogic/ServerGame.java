package com.example.hotpotato_2.GameLogic;

import android.content.Context;
import android.util.Log;
import com.example.hotpotato_2.Communication.CommsHandler;
import com.example.hotpotato_2.Communication.Server;
import com.example.hotpotato_2.Hardware.SensorsHandler;
import com.example.hotpotato_2.Hardware.VibrationHandler;

/**
 * Created by ngorgi on 1/15/14.
 */
public class ServerGame extends GameHandler {

    public ServerGame(Context context, SensorsHandler sensorsHandler, VibrationHandler vibrationHandler, GameHandlerInterface gameHandlerInterface) {
        super(context, sensorsHandler, vibrationHandler, gameHandlerInterface);
        sensorsHandler.setSensorHandlerInterface(sensorHandlerInterface);
        vibrationHandler.setVibrationCompletedInterface(vibrationCompletedInterface);

    }

    VibrationHandler.VibrationCompletedInterface vibrationCompletedInterface = new VibrationHandler.VibrationCompletedInterface() {
        @Override
        public void vibrationCompleted() {
            if (currentGameState == STATE_GAME_FINISHED) {
                Log.i("workspace", "vibration done");
                gameStateChanged(STATE_GET_IN_POSITIONS, true);
            }
        }
    };

    @Override
    public void gameStateChanged(int newState, boolean broadcast) {
        super.gameStateChanged(newState, broadcast);
        int oldState = currentGameState;
        switch (newState) {
            case STATE_GET_IN_POSITIONS:
                break;
            case STATE_GAME_STARTED:
                if (oldState == STATE_GET_IN_POSITIONS) {
                    vibrationHandler.playGameStartNotified();
                }
                break;
            case STATE_SERVER_STARTED_TURNING:
                break;
            case STATE_GAME_FINISHED:
                break;
        }
        currentGameState = newState;
        if (broadcast) {
            commsHandler.writeData("GS=" + String.valueOf(newState));
        }
        Log.i("workshop", "new game state " + String.valueOf(newState));
    }

    @Override
    public void establishNetworkConnection(int port) {
        super.establishNetworkConnection(port);
        commsHandler = new Server(port, commsHandlerInterface);
        commsHandler.establishConnection();

    }

    @Override
    public void shutDown() {
        super.shutDown();
    }

    CommsHandler.CommsHandlerInterface commsHandlerInterface = new CommsHandler.CommsHandlerInterface() {
        @Override
        public void connectionStateChanged(int newState) {
            Log.i("workshop", "server conn state changed " + newState);
            if (newState == CommsHandler.STATE_CONNECTION_ESTABLISHED &&
                    currentGameState == STATE_GAME_NOT_STARTED) {
                gameStateChanged(STATE_GET_IN_POSITIONS, true);
                sensorsHandler.startPolling();
                //move to game started
            }
        }

        @Override
        public void dataReceived(String data) {
            if (data.startsWith("SV=")) {
                int intensity = new Integer(data.substring(3));
                vibrationHandler.playIntensity(intensity);
            } else if (data.startsWith("GS=")) {

            } else if (data.startsWith("IP=")) {
                boolean clientInPosition = data.charAt(3) == '1';
                if (clientInPosition && isInPosition) {
                    gameStateChanged(STATE_GAME_STARTED, true);
                }
            } else if (data.equals("GP")) {
                if (currentGameState == STATE_GAME_STARTED) {
                    //client lost
                    currentGameState = STATE_GAME_FINISHED;
                    commsHandler.writeData("GS=" + String.valueOf(STATE_GAME_FINISHED) + "0");
                    vibrationHandler.pulseWin();

                } else if (currentGameState == STATE_SERVER_STARTED_TURNING) {
                    currentGameState = STATE_GAME_FINISHED;
                    commsHandler.writeData("GS=" + String.valueOf(STATE_GAME_FINISHED) + "1");
                    vibrationHandler.pulseLose();
                    //client won
                }
            }


        }
    };
    SensorsHandler.SensorHandlerInterface sensorHandlerInterface = new SensorsHandler.SensorHandlerInterface() {
        @Override
        public void newAccelData(float[] data) {

        }

        @Override
        public void newGyroData(float[] data) {
            switch (currentGameState) {
                case STATE_GET_IN_POSITIONS:

                    break;
                case STATE_GAME_STARTED:
                    double mag = Math.sqrt(Math.pow(data[0], 2) + Math.pow(data[1], 2) + Math.pow(data[2], 2));
                    commsHandler.writeData("SV=" + String.valueOf((int) (mag * 10)));

                    break;
                case STATE_SERVER_STARTED_TURNING:
                    mag = Math.sqrt(Math.pow(data[0], 2) + Math.pow(data[1], 2) + Math.pow(data[2], 2));
                    commsHandler.writeData("SV=" + String.valueOf((int) (mag * 10)));

                    break;
                case STATE_GAME_FINISHED:
                    break;
            }
        }

        @Override
        public void newOrientationData(float[] data) {
            switch (currentGameState) {
                case STATE_GET_IN_POSITIONS:
                    isInPosition = Math.abs(Math.abs(data[1])) < 10 && Math.abs(data[2]) < 10;

                    break;
                case STATE_GAME_STARTED:
                    boolean nowInWindow = ((Math.abs(data[1]) < 45 && Math.abs(data[2]) > 45) ||
                            (Math.abs(data[1]) > 135 && (data[2] - 20) < 0) || (Math.abs(data[1]) > 135 && (data[2] + 20) > 0))
                            && (Math.abs(data[1]) > 160 || Math.abs(data[1]) < 10);
                    if (nowInWindow != isInWindow) {
                        if (nowInWindow) {
                            isInWindow = true;
                            gameStateChanged(STATE_SERVER_STARTED_TURNING, false);

                        } else {
                            isInWindow = false;
                            gameStateChanged(STATE_GAME_STARTED, false);
                        }
                    }
                    break;
                case STATE_SERVER_STARTED_TURNING:
                    nowInWindow = ((Math.abs(data[1]) < 45 && Math.abs(data[2]) > 45) ||
                            (Math.abs(data[1]) > 135 && (data[2] - 20) < 0) || (Math.abs(data[1]) > 135 && (data[2] + 20) > 0))
                            && (Math.abs(data[1]) > 160 || Math.abs(data[1]) < 10);
                    if (nowInWindow != isInWindow) {
                        if (nowInWindow) {
                            isInWindow = true;
                            gameStateChanged(STATE_SERVER_STARTED_TURNING, false);

                        } else {
                            isInWindow = false;
                            gameStateChanged(STATE_GAME_STARTED, false);
                        }
                    }
                    if (Math.abs(Math.abs(data[1]) - 180) < 10 && Math.abs(data[2]) < 10) {
                        currentGameState = STATE_GAME_FINISHED;
                        commsHandler.writeData("GS=" + String.valueOf(STATE_GAME_FINISHED) + "0");
                        vibrationHandler.pulseWin();
                    }
                    break;
                case STATE_GAME_FINISHED:
                    break;
            }
        }

        @Override
        public void gotPull(int intensity) {

        }
    };
}
