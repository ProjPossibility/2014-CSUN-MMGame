package Game;

import android.content.Context;
import com.example.mobilegame.Communication.CommsHandler;
import com.example.mobilegame.Communication.SocketClient;
import com.example.mobilegame.Communication.SocketServer;

/**
 * Created by Cory on 2/8/14.
 */
public class GameStateMachine {
    public static final int STATE_WAITING_FOR_GAME = 0;
    public static final int STATE_GAME_INITIAL_POSITIONS = 1;
    public static final int STATE_GAME_STAGE_STARTED = 2;
    public static final int STATE_GAME_STAGE_EVENT_WINDOW = 3;
    public static final int STATE_GAME_STAGE_PLAY_COMPLETED = 4;
    public static final int STATE_GAME_FINISHED = 5;

    int currentGameState = STATE_WAITING_FOR_GAME;
    boolean isInPosition = false;
    boolean isInWindow = false;
    CommsHandler commsHandler;

    Context context;

    public void setGameMachineInterface(GameMachineInterface gameMachineInterface) {
        this.gameMachineInterface = gameMachineInterface;
    }

    GameMachineInterface gameMachineInterface;

    //  protected CommsHandler.CommsHandlerInterface commsHandlerInterface;

    public GameStateMachine(Context context) {
        this.context = context;

    }

    //    public void setMode(boolean serverMode) {
//        isTopHand = serverMode;
//    }
    protected void playCompleted(boolean serverWon, int reason) {
    }

    protected void updateGameState(int newState, boolean broadcast) {
//        Log.i("AMP", "updatingState " + String.valueOf(newState) + " " + String.valueOf(broadcast));
        switch (newState) {
            case STATE_GAME_INITIAL_POSITIONS:
//                if (!sensorHandler.isPolling()) {
//                    sensorHandler.startPolling();
//                }
//                this.gameMachineInterface.updateStatus("get in positions...");
//                isInPosition = false;
//                isInWindow = false;
                break;
            case STATE_GAME_STAGE_STARTED:
                if (broadcast) {
                    //vibrationHandler.playGameStartNotified();
                }
                if (currentGameState != STATE_GAME_STAGE_EVENT_WINDOW) {
                    this.gameMachineInterface.updateStatus("game on!");
                }
                break;
            case STATE_GAME_FINISHED:
                this.gameMachineInterface.updateStatus("game finished");
                break;
            case STATE_GAME_STAGE_EVENT_WINDOW:
//                this.gameMachineInterface.updateStatus("watch out!");
                break;
//            case STATE_GAME_STAGE_PLAY_COMPLETED:
//                isInPosition = false;
//                isInWindow = false;
//                vibrationHandler.stopVibrate();
//                break;
        }
        currentGameState = newState;

        if (broadcast) {
            commsHandler.writeMessage("GS=" + String.valueOf(newState));
        }
    }

    public void shutDown() {
//        try {
//            sensorHandler.stopPolling();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        try {
//            vibrationHandler.stopVibrate();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        try {
            commsHandler.killConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void establishNetworkConnection(String ip, int port) {
        commsHandler = new SocketClient(ip, port);
        this.gameMachineInterface.updateStatus("connecting...");
        //isTopHand = true;
//        commsHandler.setCommsHandlerInterface(this.commsHandlerInterface);
//        commsHandler.startConnection();
    }

    public void establishNetworkConnection(int port) {
        this.gameMachineInterface.updateStatus("waiting for connection...");

        commsHandler = new SocketServer(port);
        //isTopHand = false;
//        commsHandler.setCommsHandlerInterface(this.commsHandlerInterface);
//        commsHandler.startConnection();

    }


//
//    CommsHandler.CommsHandlerInterface commsHandlerInterface = new CommsHandler.CommsHandlerInterface() {
//        @Override
//        public void gotMessage(String message) {
//            float f = Float.valueOf(message);
//            if (f < 25) {
//                vibrationHandler.stopVibrate();
//            } else {
//                vibrationHandler.pulsePWM((int) f);
//            }
////            txtIncoming.setText(message);
//        }
//    };

    public interface GameMachineInterface {
        public void updateStatus(String newStatus);
    }
}
