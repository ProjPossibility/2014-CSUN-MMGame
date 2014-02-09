package com.example.hotpotato_2.Communication;

import android.util.Log;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ngorgi on 1/14/14.
 */
public class Server extends CommsHandler {
    ServerThread serverThread;

    public Server(int port, CommsHandlerInterface commsHandlerInterface) {
        super(port, commsHandlerInterface);
        serverThread = new ServerThread();
        serverThread.setCommsHandlerInterface(commsHandlerInterface);

    }

    @Override
    public void establishConnection() {
        super.establishConnection();
        serverThread.start();
    }

    @Override
    public void writeData(String data) {
        super.writeData(data);
//        Log.i("workshop", "write data received " + data);
        serverThread.writeData(data);

    }

    @Override
    public void closeConnection() {
        super.closeConnection();
        serverThread.shutDown();
    }

    public class ServerThread extends Thread {
        boolean keepRunning = true;
        Queue<String> outgoingCommandQueue;

        public void setCommsHandlerInterface(CommsHandlerInterface commsHandlerInterface) {
            this.commsHandlerInterface = commsHandlerInterface;
        }

        CommsHandlerInterface commsHandlerInterface;

        public ServerThread() {
            outgoingCommandQueue = new LinkedList<String>();
        }

        synchronized public void shutDown() {
            keepRunning = false;
        }


        synchronized public void writeData(String data) {
            outgoingCommandQueue.add(data);
        }


        @Override
        public void run() {
            super.run();
            try {
                commsHandlerInterface.connectionStateChanged(STATE_WAITING_FOR_CONNECTION);

                ServerSocket serverSocket = new ServerSocket(12345);
                serverSocket.setReuseAddress(true);
                serverSocket.setSoTimeout(10);

                Socket clientSocket = null;
                while (clientSocket == null) {
                    try {
                        clientSocket = serverSocket.accept();
                    } catch (Exception e) {

                    }
                }
                commsHandlerInterface.connectionStateChanged(STATE_CONNECTION_ESTABLISHED);
                clientSocket.setKeepAlive(true);
                clientSocket.setTcpNoDelay(true);
                clientSocket.setReuseAddress(true);

                clientSocket.setSoTimeout(10);

//                Log.i("workshop", "connection established");
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                clientSocket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(
                                clientSocket.getOutputStream()));


                while (keepRunning) {


                    if (outgoingCommandQueue.size() > 0) {
                        final String data = outgoingCommandQueue.remove();
//                        Log.i("workshop", "wrote out data " + data);
                        writer.write(data);
                        writer.newLine();
                        writer.flush();
                    }

                    try {
                        final String data = reader.readLine();
                        if (data != null) {
//                            Log.i("AMP", data);
                            commsHandlerInterface.dataReceived(data);
                        }


                    } catch (Exception e) {
                    }

                }
                reader.close();
                writer.close();
                clientSocket.close();
                serverSocket.close();

                commsHandlerInterface.connectionStateChanged(STATE_CONNECTION_FINISHED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
