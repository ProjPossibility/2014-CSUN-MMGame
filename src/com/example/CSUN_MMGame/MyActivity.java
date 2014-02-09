package com.example.CSUN_MMGame;

import android.util.Log;
import com.example.CSUN_MMGame.Game.ClientGame;
import com.example.CSUN_MMGame.Game.GameStateMachine;
import com.example.CSUN_MMGame.Game.ServerGame;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.CSUN_MMGame.Communication.IPfinder;

public class MyActivity extends Activity {

    Button startHost, connect;
    EditText txtIP;
    TextView txtServerIPs;
    Context context;
    Button btnGetIP;

    GameStateMachine gameStateMachine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setContentView(R.layout.main);
        startHost = (Button) findViewById(R.id.btnHost);
        connect = (Button) findViewById(R.id.btnConnect);
        txtIP = (EditText) findViewById(R.id.textIP);
        btnGetIP = (Button) findViewById(R.id.btnGetIP);
        txtIP.setText(PreferencesHandler.getLastIP(context));

        txtServerIPs = (TextView) findViewById(R.id.txtServerIP);
        txtIP.setOnClickListener(onClickListener);
        startHost.setOnClickListener(onClickListener);
        connect.setOnClickListener(onClickListener);
        btnGetIP.setOnClickListener(onClickListener);
    }

    GameStateMachine.GameMachineInterface gameMachineInterface = new GameStateMachine.GameMachineInterface() {
        @Override
        public void updateStatus(String newStatus) {
            final String ns = newStatus;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //txtIncoming.setText(String.valueOf(ns));
                    //accessibilityHandler.announce(ns);

                }
            });


        }
    };

    public void enableAll() {
        //startHost.setEnabled(sensorHandler.gyroExists);
        startHost.setEnabled(true);
        connect.setEnabled(true);
        //autoCon.setEnabled(true);
    }

    public void disableAll() {
        startHost.setEnabled(false);
        connect.setEnabled(false);
        //autoCon.setEnabled(false);
    }

    public void startHosting() {
        gameStateMachine = new ServerGame(context);
        gameStateMachine.setGameMachineInterface(gameMachineInterface);
        gameStateMachine.establishNetworkConnection(PreferencesHandler.normalPort);
        txtServerIPs.setVisibility(View.VISIBLE);
        txtServerIPs.setText(IPfinder.getIPstring());
        disableAll();
    }

    public void startConnect(String ip) {
        gameStateMachine = new ClientGame(context);
        gameStateMachine.setGameMachineInterface(gameMachineInterface);
        gameStateMachine.establishNetworkConnection(ip, PreferencesHandler.normalPort);
        disableAll();
        Log.i("BearNinjaCowboy", "MyActivity: StartConnect");
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(startHost)) {
                startHosting();
            } else if (v.equals(connect)) {
                startConnect(String.valueOf(txtIP.getText()));
            } else if (v.equals(txtIP)) {
                PreferencesHandler.setLastIP(context, String.valueOf(txtIP.getText()));
            } else if (v.equals(btnGetIP)) {
                txtServerIPs.setText(IPfinder.getIPstring());
            }
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        PreferencesHandler.setLastIP(context, String.valueOf(txtIP.getText()));
        try {
            gameStateMachine.shutDown();
        } catch (Exception e) {
            e.printStackTrace();
        }
        finish();

    }
}
