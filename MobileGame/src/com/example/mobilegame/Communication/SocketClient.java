package com.example.mobilegame.Communication;

import android.util.Log;

import java.io.*;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Cory on 2/8/14.
 */
public class SocketClient extends CommsHandler{
    SockClient client;

    @Override
    public void setCommsHandlerInterface(CommsHandlerInterface commsHandlerInterface) {
        super.setCommsHandlerInterface(commsHandlerInterface);
        client.setCommsHandlerInterface(commsHandlerInterface);
    }

    public SocketClient(String ip, int port) {
        super(ip, port);
        client = new SockClient(ip, port);
    }


    @Override
    public boolean isServer() {
        return false;
    }

    @Override
    public void killConnection() {
        if (client != null) {
            Log.i("AMP", "killClient");
//            client.sendCommand("GS=" + GameStateMachine.STATE_GAME_FINISHED);
            client.stopServer();
            client = null;
        }
    }

    @Override
    public void startConnection() {
        super.startConnection();
        client.start();
    }

    @Override
    public boolean isRunning() {
        return (client != null && client.isRunning());
    }

    @Override
    public void writeMessage(String message) {
//        Log.i("AMP", "preThreadClient "+ String.valueOf(message));
        if (client != null) {
            client.sendCommand(message);
        }

    }


    public class SockClient extends Thread {
        boolean keepRun = true;
        Socket socket;
        String ip;
        int port;
        Queue<String> outgoingCommandQueue;


        synchronized boolean isRunning() {
            return keepRun;
        }

        public SockClient(String ip, int port) {
            this.port = port;
            this.ip = ip;
            outgoingCommandQueue = new LinkedList<String>();
        }

        public void setCommsHandlerInterface(CommsHandlerInterface commsHandlerInterface) {
            this.commsHandlerInterface = commsHandlerInterface;
        }

        CommsHandlerInterface commsHandlerInterface;


        synchronized void sendCommand(String command) {
            outgoingCommandQueue.add(command);
        }


        synchronized void stopServer() {
            keepRun = false;
        }


        @Override
        public void run() {
            super.run();

        }
    }

}
