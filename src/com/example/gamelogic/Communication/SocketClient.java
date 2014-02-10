package com.example.gamelogic.Communication;

import android.util.Log;
import com.example.gamelogic.Game.GameStateMachine;
import com.example.gamelogic.PreferencesHandler;

import java.io.*;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Cory on 2/8/14.
 */
public class SocketClient extends CommsHandler {
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
        Log.i("BearNinjaCowboy", "SocketClient: startConnection");
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
            commsHandlerInterface.connectionStateChanged(STATE_WAITING_FOR_CONNECTION);
            try {
//
//                InetAddress group = null;
//                MulticastSocket msock = null;
//                String data = null;
//
//                byte[] buf = new byte[1024];
//                DatagramPacket receivePacket = new DatagramPacket(buf, buf.length);
//
//
//                try {
//                    msock = new MulticastSocket(PreferencesHandler.multicastPort);
//                    group = InetAddress.getByAddress(new byte[]{(byte) 239, (byte) 255, (byte) 42, (byte) 99});
//                    msock.joinGroup(group);
//                    Log.i("BearNinjaCowboy", group.getHostAddress());
//                    msock.receive(receivePacket);
//                    Log.i("BearNinjaCowboy", "Packet Received");
//                    Log.i("BearNinjaCowboy", receivePacket.getAddress().getHostAddress());
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//                // main client socket
//                ip = receivePacket.getAddress().getHostAddress();
                Socket socket = new Socket(ip, port);
                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);
                socket.setReuseAddress(true);
                socket.setSoTimeout(PreferencesHandler.socketTimeout);
                Log.i("BearNinjaCowboy", "SocketClient: connected");
                commsHandlerInterface.connectionStateChanged(STATE_CONNECTED);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

                while (keepRun) {
                    if (outgoingCommandQueue.size() > 0) {
                        String s = outgoingCommandQueue.remove();
                        Log.i("AMP", "writing "+ String.valueOf(s));
                        bufferedWriter.write(s);
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }

                    if (bufferedReader.ready()) {
                        final String readIn = bufferedReader.readLine();
                        commsHandlerInterface.gotMessage(readIn);
                    }
                }
//                final String command = "GS=" + GameStateMachine.STATE_GAME_FINISHED;
//                bufferedWriter.write(command);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                commsHandlerInterface.connectionStateChanged(STATE_ENDED_CONNECTION);
                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedReader.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
