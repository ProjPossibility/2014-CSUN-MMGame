package com.example.CSUN_MMGame.Game;

import android.content.Context;
import android.util.Log;
import com.example.CSUN_MMGame.Communication.CommsHandler;

/**
 * Created by Cory on 2/8/14.
 */
public class ServerGame extends GameStateMachine {

    public ServerGame(Context context){
        super(context);

    }

    @Override
    public void establishNetworkConnection(int port) {
        super.establishNetworkConnection(port);
        commsHandler.findAndSetBroadcastAddress(context);
        commsHandler.setCommsHandlerInterface(commsHandlerInterface);
        commsHandler.startConnection();
    }

    CommsHandler.CommsHandlerInterface commsHandlerInterface = new CommsHandler.CommsHandlerInterface() {
        @Override
        public void gotMessage(String message) {
//            if (message.startsWith("SV=")) {
//                int intensity = Integer.valueOf(message.substring(3));
//                vibrationHandler.pulsePWM(intensity);
//
//            } else if (message.startsWith("GS=")) {
//                switch ((int) (message.charAt(3) - '0')) {
//                    case STATE_GAME_FINISHED:
//                        sensorHandler.stopPolling();
//                        vibrationHandler.stopVibrate();
//                        updateGameState(STATE_GAME_FINISHED, false);
//                        break;
//                }
//            } else if (message.startsWith("GP")) {                                //Got pull (from client)
//                if (currentGameState == STATE_GAME_STAGE_EVENT_WINDOW) {
//                    playCompleted(false, REASON_TOP_PULLED_AWAY);
//                } else if (currentGameState == STATE_GAME_STAGE_STARTED) {
//                    playCompleted(true, REASON_TOP_FLINCHED);
//                }
//            } else if (message.startsWith("IP=")) {                                //in position (from client)
//                if (message.charAt(3) == '1' && isInPosition) {
//                    updateGameState(STATE_GAME_STAGE_STARTED, true);
//                }
//            }
        }

        @Override
        public void connectionStateChanged(int newState) {
            switch (newState) {
                case CommsHandler.STATE_WAITING_FOR_CONNECTION:
                    Log.i("BearNinjaCowboy", "ServerGame: waiting for connection");
                    break;
                case CommsHandler.STATE_CONNECTED:
                    updateGameState(STATE_GAME_INITIAL_POSITIONS, true);
                    Log.i("BearNinjaCowboy", "ServerGame: connected");
                    break;
                case CommsHandler.STATE_ENDED_CONNECTION:
                    break;

            }
        }
    };
}
