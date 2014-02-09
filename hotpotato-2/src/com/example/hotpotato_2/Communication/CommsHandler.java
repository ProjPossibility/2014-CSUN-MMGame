package com.example.hotpotato_2.Communication;

/**
 * Created by ngorgi on 1/14/14.
 */
public class CommsHandler {
    public static final int STATE_WAITING_FOR_CONNECTION = 1;
    public static final int STATE_CONNECTION_ESTABLISHED = 2;
    public static final int STATE_CONNECTION_FINISHED = 3;
    public boolean isServer;
    public int port;
    public String ip;
    public CommsHandlerInterface commsHandlerInterface;

    public CommsHandler(int port,
                        CommsHandlerInterface commsHandlerInterface) {
        isServer = true;
        this.port = port;
        this.commsHandlerInterface = commsHandlerInterface;
    }

    public CommsHandler(int port, String ip,
                        CommsHandlerInterface commsHandlerInterface) {
        isServer = false;
        this.port = port;
        this.commsHandlerInterface = commsHandlerInterface;
        this.ip = ip;
    }

    public void establishConnection() {

    }

    public void writeData(String data) {

    }

    public void closeConnection() {

    }

    public interface CommsHandlerInterface {

        public void connectionStateChanged(int newState);

        public void dataReceived(String data);
    }
}
