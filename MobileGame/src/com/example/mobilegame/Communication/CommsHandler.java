package com.example.mobilegame.Communication;

import android.content.Context;

/**
 * Created by Cory on 2/8/14.
 */
public class CommsHandler {
    private boolean server;
    public static final int STATE_NOT_STARTED = -1;
    public static final int STATE_WAITING_FOR_CONNECTION = 0;
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_ENDED_CONNECTION = 2;

    public int connectionState = -1;

    public void setCommsHandlerInterface(CommsHandlerInterface commsHandlerInterface) {
        this.commsHandlerInterface = commsHandlerInterface;
    }

    protected CommsHandlerInterface commsHandlerInterface;

    public CommsHandler(String ip, int port) {

    }

    public boolean isServer() {
        return false;
    }

    public CommsHandler(int port) {
//        isServer = true;
//        server = new SocketServer(port)
    }


    public void writeValue(float val) {

    }

    public void writeValues(float[] vals) {

    }


    public void writeMessage(String message) {

    }

    public void startConnection() {

    }

    public int getConnectionState() {
        return connectionState;
    }

    public boolean isRunning() {
        return false;
    }


    public void findAndSetBroadcastAddress(Context context){
    }


    public void killConnection() {

    }

    public interface CommsHandlerInterface {
        public void gotMessage(String message);

        public void connectionStateChanged(int newState);
    }
}
