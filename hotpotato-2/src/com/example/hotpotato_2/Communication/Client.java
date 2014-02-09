package com.example.hotpotato_2.Communication;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ngorgi on 1/14/14.
 */
public class Client extends CommsHandler {
    ClientThread clientThread;

    @Override
    public void establishConnection() {
        super.establishConnection();
        Log.i("workshop", "start establish conn");
        clientThread.start();

    }

    @Override
    public void closeConnection() {
        super.closeConnection();
        clientThread.shutDown();
    }

    @Override
    public void writeData(String data) {
        super.writeData(data);
        Log.i("workshop", "write data received " + data);
        clientThread.writeData(data);

    }

    public Client(int port, String ip, CommsHandlerInterface commsHandlerInterface) {
        super(port, ip, commsHandlerInterface);
        clientThread = new ClientThread();
        clientThread.setCommsHandlerInterface(commsHandlerInterface);
    }

    public class ClientThread extends Thread {
        boolean keepRunning = true;
        CommsHandlerInterface commsHandlerInterface;
        Queue<String> outgoingCommandQueue;

        public ClientThread() {
            outgoingCommandQueue = new LinkedList<String>();
        }

        public void setCommsHandlerInterface(CommsHandlerInterface commsHandlerInterface) {
            this.commsHandlerInterface = commsHandlerInterface;
        }

        synchronized public void writeData(String data) {
//            Log.i("workshop", "before " + String.valueOf(outgoingCommandQueue.size()));
            outgoingCommandQueue.add(data);
//            Log.i("workshop", "after " + String.valueOf(outgoingCommandQueue.size()));
        }


        synchronized public void shutDown() {
            keepRunning = false;
        }

        @Override
        public void run() {
            super.run();
            try {
                commsHandlerInterface.connectionStateChanged(STATE_WAITING_FOR_CONNECTION);
                Socket socket = new Socket(ip, 12345);
                socket.setReuseAddress(true);
                socket.setKeepAlive(true);
                socket.setSoTimeout(10);
                commsHandlerInterface.connectionStateChanged(STATE_CONNECTION_ESTABLISHED);

                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(
                                socket.getOutputStream()));
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                socket.getInputStream()));
//                Log.i("workshop", "connection established");

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
                socket.close();

                commsHandlerInterface.connectionStateChanged(STATE_CONNECTION_FINISHED);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
