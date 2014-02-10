package com.example.gamelogic.Game;

import android.content.Context;
import android.util.Log;
import com.example.gamelogic.Communication.CommsHandler;

/**
 * Created by Cory on 2/8/14.
 */
public class ClientGame extends GameStateMachine{
    public ClientGame(Context context){
        super(context);
    }

    @Override
    public void establishNetworkConnection(String ip, int port) {
        super.establishNetworkConnection(ip, port);
        commsHandler.setCommsHandlerInterface(commsHandlerInterface);
        commsHandler.startConnection();
    }

    @Override
    public void establishClientConnection(int port) {
        super.establishClientConnection(port);
        commsHandler.setCommsHandlerInterface(commsHandlerInterface);
        commsHandler.startConnection();
    }

    @Override
    public void writeData() {
        //super.writeData();
        commsHandler.writeMessage("I am a client...");
    }

    CommsHandler.CommsHandlerInterface commsHandlerInterface = new CommsHandler.CommsHandlerInterface() {
        @Override
        public void gotMessage(String message) {
            if (message.equals("I am a server...")) {
                Log.i("BearNinjaCowboy", "Incoming Message from server.");
            }
//            if (message.equals("I AM SERVER")) {
//                Log.i("BearNinjaCowboy", "Server says: I AM SERVER");
//                commsHandler.writeMessage("RAWR!");
//            }
//            else if (message.equals("BANG!")) {
//                Log.i("BearNinjaCowboy", "Server says: BANG!");
//                commsHandler.writeMessage("I AM CLIENT");
//            }
//            if (message.startsWith("SV=")) {                 //sensor value
//                int intensity = Integer.valueOf(message.substring(3));
//                vibrationHandler.pulsePWM(intensity);
//
//            } else if (message.startsWith("GS=")) {   //game state
//                switch ((int) (message.charAt(3) - '0')) {
//
//                    case STATE_GAME_INITIAL_POSITIONS:
//                        updateGameState(STATE_GAME_INITIAL_POSITIONS, false);
//                        break;
//                    case STATE_GAME_STAGE_STARTED:
//                        updateGameState(STATE_GAME_STAGE_STARTED, false);
//                        break;
//                    case STATE_GAME_STAGE_EVENT_WINDOW:
//                        updateGameState(STATE_GAME_STAGE_EVENT_WINDOW, false);
//                        break;
//                    case STATE_GAME_FINISHED:
//                        sensorHandler.stopPolling();
//                        vibrationHandler.stopVibrate();
//                        updateGameState(STATE_GAME_FINISHED, false);
//                        break;
//                    case STATE_GAME_STAGE_PLAY_COMPLETED: {
//                        message = message.substring(3);
//                        boolean serverWon = message.split(",")[1].equals("s");
//                        int reason = Integer.valueOf(message.split(",")[2]);
//                        playCompleted(serverWon, reason);
//                    }
//                }
//            }
        }


        @Override
        public void connectionStateChanged(int newState) {
            switch (newState) {
                case CommsHandler.STATE_WAITING_FOR_CONNECTION:
                    Log.i("BearNinjaCowboy", "ClientGame: waiting for connection");
                    break;
                case CommsHandler.STATE_CONNECTED:
                    Log.i("BearNinjaCowboy", "ClientGame: connected");
                    commsHandler.writeMessage("I AM CLIENT");
                    break;
                case CommsHandler.STATE_ENDED_CONNECTION:
                    break;

            }
        }
    };
}
