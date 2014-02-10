package com.example.gamelogic.Communication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import com.example.gamelogic.PreferencesHandler;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

/**
 * Created by Cory on 2/8/14.
 */
public class PeerFinder extends AsyncTask<String, String, String> {

    Context context;

    PeerFinderInterface peerFinderInterface;

    public PeerFinder(Context context, PeerFinderInterface peerFinderInterface) {
        this.context = context;
        this.peerFinderInterface = peerFinderInterface;
    }

    ProgressDialog progressDialog;

    boolean keepSearching = true;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Searching...");
        progressDialog.setOnCancelListener(onCancelListener);
        progressDialog.setCancelable(true);
        progressDialog.show();
    }

    DialogInterface.OnCancelListener onCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            keepSearching = false;
        }
    };

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (s != null) {
            peerFinderInterface.peerFound(s);
        } else {
            peerFinderInterface.findFailed();
        }
        progressDialog.dismiss();

    }

    @Override
    protected String doInBackground(String... params) {
        byte[] incoming = new byte[256];
        MulticastSocket msock = null;
        while (msock == null) {
            try {
                msock = new MulticastSocket(PreferencesHandler.multicastPort);
                msock.setBroadcast(true);
                msock.setReuseAddress(true);
                msock.setSoTimeout(50);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        InetAddress group = null;
        try {
            group = InetAddress.getByAddress(new byte[]{(byte) 239, (byte) 255, (byte) 42, (byte) 99});
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        try {
            msock.joinGroup(group);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String otherIP = null;
        while (keepSearching) {
            try {
                DatagramPacket receivePacket = new DatagramPacket(incoming, incoming.length);
                msock.receive(receivePacket);

                String sf = new String(incoming);
                int first0 = 1;
                for (byte b : incoming) {
                    if (b == 0) {
                        break;
                    }
                    first0++;
                }
                sf = sf.substring(0, first0).trim();
                if (sf.equals("bearninjacowboy")) {
                    keepSearching = false;
                    otherIP = receivePacket.getAddress().getHostAddress();
                }
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            msock.leaveGroup(group);
            msock.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return otherIP;
    }

    public interface PeerFinderInterface {
        public void peerFound(String ip);

        public void findFailed();
    }
}
