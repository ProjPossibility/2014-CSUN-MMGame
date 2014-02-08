package com.example.mobilegame.Communication;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.*;
import java.net.*;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by Cory on 2/8/14.
 */
public class SocketServer extends CommsHandler {
    SockServ serv;

    public static InetAddress getBroadcastAddress(Context context) throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }

    @Override
    public void setCommsHandlerInterface(CommsHandlerInterface commsHandlerInterface) {
        super.setCommsHandlerInterface(commsHandlerInterface);
        serv.setCommsHandlerInterface(commsHandlerInterface);
    }

    @Override
    public boolean isServer() {
        return true;
    }

    public SocketServer(int port) {
        super(port);
        serv = new SockServ(port);
    }

    @Override
    public void killConnection() {
        if (serv != null) {
            serv.stopServer();
            serv = null;
        }
    }

    @Override
    public void startConnection() {
        super.startConnection();
        Log.i("AMP", "Starting Server");
        serv.start();
    }

    @Override
    public void writeMessage(String message) {
        if (serv != null) {
            serv.sendCommand(message);
        } else {
            Log.i("AMP", String.valueOf(message));
        }
    }

    @Override
    public boolean isRunning() {
        return (serv != null && serv.isRunning());
    }


    @Override
    public void findAndSetBroadcastAddress(Context context) {
        try {
            serv.setBroadcastAddress(getBroadcastAddress(context));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public class SockServ extends Thread {
        boolean keepRun = true;
        Socket socket;
        int port;
        Queue<String> outgoingCommandQueue;


        public void setBroadcastAddress(InetAddress broadcastAddress) {
            this.broadcastAddress = broadcastAddress;
        }

        InetAddress broadcastAddress;

        public void setCommsHandlerInterface(CommsHandlerInterface commsHandlerInterface) {
            this.commsHandlerInterface = commsHandlerInterface;

        }

        CommsHandlerInterface commsHandlerInterface;

        public SockServ(int port) {
            this.port = port;
            outgoingCommandQueue = new LinkedList<String>();
        }

        synchronized void stopServer() {
            keepRun = false;
        }

        synchronized void sendCommand(String command) {
            outgoingCommandQueue.add(command);
        }

        synchronized boolean isRunning() {
            return keepRun;
        }


        @Override
        public void run() {
            super.run();
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                serverSocket.setReuseAddress(true);
                Log.i("workshop", "waiting for connection");
                Socket clientSocket = serverSocket.accept();
                Log.i("workshop", "connection established");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));
                String s = reader.readLine();
                Log.i("workshop", "read in " + String.valueOf(s));
                clientSocket.close();
                serverSocket.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
