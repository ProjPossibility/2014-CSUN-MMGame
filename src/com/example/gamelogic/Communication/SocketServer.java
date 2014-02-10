package com.example.gamelogic.Communication;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import com.example.gamelogic.Game.GameStateMachine;
import com.example.gamelogic.PreferencesHandler;

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
                InetAddress group = null;
                MulticastSocket msock = null;
                int x = 0;
                final String data = "bearninjacowboy";
                DatagramPacket sendPacket = new DatagramPacket(data.getBytes(), data.getBytes().length, broadcastAddress, PreferencesHandler.multicastPort);

                try {
                    //Address broadcasting socket
                    msock = new MulticastSocket(PreferencesHandler.multicastPort);
                    msock.setBroadcast(true);
                    msock.setReuseAddress(true);
                    group = InetAddress.getByAddress(new byte[]{(byte) 239, (byte) 255, (byte) 42, (byte) 99});
                    msock.joinGroup(group);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //main server socket
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

                    x++;
                    if (x == 150) {
                        x = 0;
                        if (msock != null) {
                            msock.send(sendPacket);
                        }
                    }
                }
                if (msock != null) {
                    msock.leaveGroup(group);
                    msock.close();
                }

                socket.setReuseAddress(true);
                socket.setSoTimeout(PreferencesHandler.socketTimeout);
                socket.setKeepAlive(true);
                socket.setTcpNoDelay(true);
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
//                            Log.i("AMP", "readIn "+ String.valueOf(readIn));
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
