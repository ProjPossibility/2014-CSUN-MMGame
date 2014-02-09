package com.example.hotpotato_2.GameLogic;

import android.content.Context;
import com.example.hotpotato_2.Communication.CommsHandler;
import com.example.hotpotato_2.Hardware.SensorsHandler;
import com.example.hotpotato_2.Hardware.VibrationHandler;

/**
 * Created by ngorgi on 1/15/14.
 */
public class GameHandler {
    public static final int STATE_GAME_NOT_STARTED = 0;
    public static final int STATE_GET_IN_POSITIONS = 1;
    public static final int STATE_GAME_STARTED = 2;
    public static final int STATE_SERVER_STARTED_TURNING = 3;
    public static final int STATE_GAME_FINISHED = 4;
    public  boolean isInPosition = false;
    public boolean isInWindow = false;
    public int currentGameState = 0;
    Context context;
    SensorsHandler sensorsHandler;
    VibrationHandler vibrationHandler;
    CommsHandler commsHandler;
    GameHandlerInterface gameHandlerInterface;

    public GameHandler(Context context, SensorsHandler sensorsHandler, VibrationHandler vibrationHandler, GameHandlerInterface gameHandlerInterface) {
        this.context = context;
        this.sensorsHandler = sensorsHandler;
        this.vibrationHandler = vibrationHandler;
        this.gameHandlerInterface = gameHandlerInterface;

    }

    public void establishNetworkConnection(int port) {

    }

    public void establishNetworkConnection(int port, String ip) {

    }

    public void gameStateChanged(int newState, boolean broadcast) {
//        int oldState = currentGameState;
//        switch (newState) {
//            case STATE_GET_IN_POSITIONS:
//                break;
//            case STATE_GAME_STARTED:
//                break;
//            case STATE_SERVER_STARTED_TURNING:
//                break;
//            case STATE_GAME_FINISHED:
//                break;
//        }

    }

    public void shutDown() {

    }


    public interface GameHandlerInterface {
        public void newGameState(int newState);
    }
}
