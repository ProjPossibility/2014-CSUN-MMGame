package com.example.CSUN_MMGame.Communication;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.example.CSUN_MMGame.Game.GameStateMachine;
import com.example.CSUN_MMGame.PreferencesHandler;

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
        Log.i("BearNinjaCowboy", "SocketServer: startConnection");
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
            commsHandlerInterface.connectionStateChanged(STATE_WAITING_FOR_CONNECTION);

            try {
                ServerSocket serverSocket = new ServerSocket(port);
                serverSocket.setReuseAddress(true);
                serverSocket.setSoTimeout(PreferencesHandler.socketTimeout);

                socket = null;

                while(socket == null) {
                    try {
                        socket = serverSocket.accept();
                    } catch (IOException e) {
                        socket = null;
                    }
                }

                socket.setReuseAddress(true);
                socket.setSoTimeout(PreferencesHandler.socketTimeout);
                socket.setKeepAlive(true);
                Log.i("BearNinjaCowboy", "SocketServer: connected");
                commsHandlerInterface.connectionStateChanged(STATE_CONNECTED);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                while (keepRun) {
                    if (outgoingCommandQueue.size() > 0) {
                        bufferedWriter.write(outgoingCommandQueue.remove());
                        bufferedWriter.newLine();
                        bufferedWriter.flush();
                    }

                    try {
                        final String readIn = bufferedReader.readLine();
                        if (readIn != null) {
                            commsHandlerInterface.gotMessage(readIn);
                        }
                    } catch (IOException e) {
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                //final String command = "GS=" + GameStateMachine.STATE_GAME_FINISHED;
                //bufferedWriter.write(command);
                bufferedWriter.newLine();
                bufferedWriter.flush();
                commsHandlerInterface.connectionStateChanged(STATE_ENDED_CONNECTION);

                bufferedWriter.flush();
                bufferedWriter.close();
                bufferedReader.close();
                socket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
