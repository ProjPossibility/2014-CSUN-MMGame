package Game;

import android.content.Context;
import com.example.mobilegame.Communication.CommsHandler;

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

    CommsHandler.CommsHandlerInterface commsHandlerInterface = new CommsHandler.CommsHandlerInterface() {
        @Override
        public void gotMessage(String message) {
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
                    break;
                case CommsHandler.STATE_CONNECTED:
                    break;
                case CommsHandler.STATE_ENDED_CONNECTION:
                    break;

            }
        }
    };
}
