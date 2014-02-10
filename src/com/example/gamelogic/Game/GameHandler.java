package com.example.gamelogic.Game;

import android.content.Context;
import android.util.Log;
import com.example.gamelogic.Communication.CommsHandler;
import com.example.gamelogic.Communication.SocketClient;
import com.example.gamelogic.Communication.SocketServer;
import com.example.gamelogic.SensorsHandler;
import com.example.gamelogic.VibrationHandler;

/**
 * Created by ngorgi on 2/9/14.
 */
public class GameHandler {

    // Game States
    // 0 - waiting for connection
    // 1 - wait for position
    // 2 - delay for 321 go
    // 3 - game: waiting for stability and choices
    // 4 - waiting for result to play
    // 5 - result play for win/lose/draw

    //GameState values:
    //0 - Finding server
    //1 - Waiting for position
    //2 - 3 2 1 Go
    //3 - Waiting for stability
    //4 - Opponent's choice
    //5 - Game Over

    private boolean p1Stability, p2Stability;
    private int gameState, p1Position, p2Position, p1Result, p2Result;
    private int[][] result = {{0, 0, 0, 0},
            {0, 3, 1, 2},
            {0, 2, 3, 1},
            {0, 1, 2, 3}};

    public static final int WAIT_CONNECTION = 0;
    public static final int WAIT_POSITION = 1;
    public static final int THREE_TWO_ONE_GO = 2;
    public static final int WAIT_STABILITY = 3;
    public static final int WAIT_OPPONENT_CHOICE = 4;
    public static final int GAME_OVER = 5;


    boolean isServer;
    String ip = "192.168.45.141";

    Context context;
    int clientPos = 0;

    SensorsHandler sensorHandler;
    SensorsHandler.SensorHandlerInterface sensorHandlerInterface = new SensorsHandler.SensorHandlerInterface() {
        @Override
        public void newAccelData(float[] data) {
            if (isServer) {
                Log.i("AMP", String.valueOf(p1Position) + "," + String.valueOf(p2Position) + ","
                        + String.valueOf(p1Stability) + "," + String.valueOf(p2Stability));
                p1Stability = PositionHandler.IsStable(data);
                gameLogic();
            } else {
                if (gameState == WAIT_POSITION || gameState == WAIT_STABILITY) {

                    commsHandler.writeMessage("SP="
                            + ((PositionHandler.IsStable(data) ? "T" : "F"))
                            + String.valueOf(clientPos));
                }
                Log.i("AMP", "clientstable " + String.valueOf(PositionHandler.IsStable(data)));
            }
        }

        @Override
        public void newOrientationData(float[] data) {
            if (isServer) {
                p1Position = PositionHandler.getPosition(data[0], data[1], data[2]);
                gameLogic();
            } else {
                clientPos = PositionHandler.getPosition(data[0], data[1], data[2]);

            }
        }
    };

    CommsHandler commsHandler;
    CommsHandler.CommsHandlerInterface commsHandlerInterface = new CommsHandler.CommsHandlerInterface() {
        @Override
        public void gotMessage(String message) {

            if (isServer) {
                if (gameState == WAIT_POSITION || gameState == WAIT_STABILITY) {

                    if (message.startsWith("SP=")) {
                        switch ((message.charAt(3))) {
                            case 'T':
                                // client stable
                                p2Stability = true;
                                break;
                            case 'F':
                                // client unstable
                                p2Stability = false;
                                break;
                        }
                        p2Position = (int) (message.charAt(4) - '0');
                    }
                }
            } else {
                if (message.startsWith("GS=")) {
                    Log.i("AMP", message);
                    int result;
                    gameState = (int) (message.charAt(3) - '0');
                    switch (gameState) {
                        case THREE_TWO_ONE_GO:
                            vibrationHandler.pulseGo();
                            break;
                        case WAIT_OPPONENT_CHOICE:
                            result = (int) (message.charAt(4) - '0');
                            switch (result) {
                                case 1:
                                    vibrationHandler.pulseBear();
                                    break;
                                case 2:
                                    vibrationHandler.pulseNinja();
                                    break;
                                case 3:
                                    vibrationHandler.pulseCowboy();
                                    break;
                            }
                        case GAME_OVER:
                            result = (int) (message.charAt(4) - '0');
                            switch (result) {
                                case 1:
                                    vibrationHandler.pulseDraw();
                                    break;
                                case 2:
                                    vibrationHandler.pulseLose();
                                    break;
                                case 3:
                                    vibrationHandler.pulseWin();
                                    break;
                                case 4:
                                    vibrationHandler.pulseWin();
                                    break;
                                case 5:
                                    vibrationHandler.pulseDraw();
                                    break;
                                case 6:
                                    vibrationHandler.pulseLose();
                                    break;
                                case 7:
                                    vibrationHandler.pulseLose();
                                    break;
                                case 8:
                                    vibrationHandler.pulseWin();
                                    break;
                                case 9:
                                    vibrationHandler.pulseDraw();
                                    break;

                            }


                    }
                }
            }
        }

        @Override
        public void connectionStateChanged(int newState) {
            Log.i("AMP", "connstate " + String.valueOf(newState));
            if (newState == CommsHandler.STATE_CONNECTED) {
                sensorHandler = new SensorsHandler(context);
                sensorHandler.setSensorHandlerInterface(sensorHandlerInterface);
                //turn sensors on here
                if (isServer) {
                    getIntoPosition();
                }
                sensorHandler.startPolling();
            }
        }
    };

    VibrationHandler vibrationHandler;
    VibrationHandler.VibrationCompletedInterface vibrationCompletedInterface = new VibrationHandler.VibrationCompletedInterface() {
        @Override
        public void vibrationCompleted() {
            if (isServer && gameState == THREE_TWO_ONE_GO) {
                gameState = WAIT_STABILITY;
                commsHandler.writeMessage("GS=3");
            } else if (isServer && gameState == WAIT_OPPONENT_CHOICE) {
                gameState = GAME_OVER;
                gameOver();
            } else if (gameState == GAME_OVER) {
                gameState = WAIT_POSITION;
                commsHandler.writeMessage("GS=1");
            }
        }
    };

    public GameHandler(boolean isServ, Context context) {
        isServer = isServ;
        vibrationHandler = new VibrationHandler(context);
        vibrationHandler.setVibrationCompletedInterface(vibrationCompletedInterface);
        beginGame();
        this.context = context;
    }

    public void initiateConnection(String mip) {
        if (isServer) {
            commsHandler = new SocketServer(12344);
            commsHandler.findAndSetBroadcastAddress(context);
            commsHandler.setCommsHandlerInterface(commsHandlerInterface);
            commsHandler.startConnection();
        } else {
//            client connection
            commsHandler = new SocketClient(mip, 12344);
            commsHandler.setCommsHandlerInterface(commsHandlerInterface);
            commsHandler.startConnection();
        }
    }

    public void initiateConnection() {
        if (isServer) {
            commsHandler = new SocketServer(12344);
            commsHandler.findAndSetBroadcastAddress(context);
            commsHandler.setCommsHandlerInterface(commsHandlerInterface);
            commsHandler.startConnection();
        } else {
            // client connection
//            commsHandler = new SocketClient(ip, 12344);
//            commsHandler.setCommsHandlerInterface(commsHandlerInterface);
//            commsHandler.startConnection();
        }
    }


    public void gameLogic() {
        if (gameState == WAIT_POSITION) {
            if (p1Position == 4 && p2Position == 4 && p1Stability && p2Stability) {
                gameState = THREE_TWO_ONE_GO;
                commsHandler.writeMessage("GS=2");
                vibrationHandler.pulseGo();
            }
        } else if (gameState == WAIT_STABILITY) {
            if (p1Position > 0 && p1Position < 4 && p2Position > 0 && p2Position < 4 && p1Stability && p2Stability) {
                determineResult();
            }
        }
    }

    public void getIntoPosition() {
        //gameStatus.setText("Both players: Get Into Position!");
        //Play the audio & rumble for Get into Position here
        commsHandler.writeMessage("GS=1");
        gameState = WAIT_POSITION;
    }

    public void commenceGame() {
        //gameStatus.setText("3, 2, 1, Go!");
        //Play the audio & rumble for 3 2 1 Go here
        gameState = THREE_TWO_ONE_GO;
    }

    public void stabilityCheck() {
        //gameStatus.setText("Waiting for both players to make a move.");
        gameState = WAIT_STABILITY;
    }

    public void determineResult() {
        gameState = WAIT_OPPONENT_CHOICE;
        Log.i("AMP", "determineResult " + String.valueOf(p1Position) + " " + String.valueOf(p2Position));
        switch (p2Position) {
            case 1:
                //gameStatus.setText("Player 2 chose Bear!");
                vibrationHandler.pulseBear();
                //Play the audio & rumble for bear here
                break;
            case 2:
                //gameStatus.setText("Player 2 chose Ninja!");
                vibrationHandler.pulseNinja();
                //Play the audio & rumble for ninja here
                break;
            case 3:
                //gameStatus.setText("Player 2 chose Cowboy!"); b
                vibrationHandler.pulseCowboy();
                //Play the audio & rumble for cowboy here
                break;
            default:
                //gameStatus.setText("Error: Bad position...");
        }
        switch (p1Position) {
            case 1:
                //gameStatus.setText("Player 2 chose Bear!");
                commsHandler.writeMessage("GS=41");
                //Play the audio & rumble for bear here
                break;
            case 2:
                //gameStatus.setText("Player 2 chose Ninja!");
                commsHandler.writeMessage("GS=42");
                //Play the audio & rumble for ninja here
                break;
            case 3:
                //gameStatus.setText("Player 2 chose Cowboy!");
                commsHandler.writeMessage("GS=43");
                //Play the audio & rumble for cowboy here
                break;
            default:
                //gameStatus.setText("Error: Bad position...");
        }

        p1Result = p1Position;
        p2Result = p2Position;
    }

    public void gameOver() {
        gameState = GAME_OVER;
        if (p1Position == 1) {
            if (p2Position == 1) {
                //draw
                vibrationHandler.pulseDraw();
                commsHandler.writeMessage("GS=51");
            }
            if (p2Position == 2) {
                //win
                vibrationHandler.pulseWin();
                commsHandler.writeMessage("GS=52");
            }
            if (p2Position == 3) {
                //lose
                vibrationHandler.pulseLose();
                commsHandler.writeMessage("GS=53");
            }
        } else if (p1Position == 2) {
            if (p2Position == 1) {
                //lose
                vibrationHandler.pulseLose();
                commsHandler.writeMessage("GS=54");
            }
            if (p2Position == 2) {
                //draw
                vibrationHandler.pulseDraw();
                commsHandler.writeMessage("GS=55");
            }
            if (p2Position == 3) {
                //win
                vibrationHandler.pulseWin();
                commsHandler.writeMessage("GS=56");
            }
        } else if (p1Position == 3) {
            if (p2Position == 1) {
                //win
                vibrationHandler.pulseWin();
                commsHandler.writeMessage("GS=57");
            }
            if (p2Position == 2) {
                //lose
                vibrationHandler.pulseLose();
                commsHandler.writeMessage("GS=58");
            }
            if (p2Position == 3) {
                //draw
                vibrationHandler.pulseDraw();
                commsHandler.writeMessage("GS=59");
            }
        }
    }

    public void beginGame() {
        gameState = WAIT_CONNECTION;
        p1Stability = false;
        p2Stability = false;

        p1Position = 0;

        p2Position = 0;
        p1Result = 0;
        p2Result = 0;
    }
}
