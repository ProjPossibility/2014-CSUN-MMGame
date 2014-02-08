package com.example.mobilegame;

import Game.GameStateMachine;
import Game.ServerGame;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.example.mobilegame.Communication.IPfinder;

public class MainActivity extends Activity {

    Button startHost, connect;
    EditText txtIP;
    TextView txtServerIPs;
    Context context;

    GameStateMachine gameStateMachine;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        startHost = (Button) findViewById(R.id.btnHost);
        connect = (Button) findViewById(R.id.btnConnect);
        txtIP = (EditText) findViewById(R.id.textIP);

        txtServerIPs = (TextView) findViewById(R.id.txtServerIP);
        txtIP.setOnClickListener(onClickListener);
        startHost.setOnClickListener(onClickListener);
        connect.setOnClickListener(onClickListener);
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
        //gameStateMachine.establishNetworkConnection(PreferencesHandler.normalPort);
        gameStateMachine.establishNetworkConnection(12345);
        txtServerIPs.setVisibility(View.VISIBLE);
        txtServerIPs.setText(IPfinder.getIPstring());
        disableAll();
    }

    public void startConnect(String ip) {

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(startHost)) {
                startHosting();
            } else if (v.equals(connect)) {
                startConnect(String.valueOf(txtIP.getText()));
            } else if (v.equals(txtIP)) {
                //PreferencesHandler.setLastIP(context, String.valueOf(txtIP.getText()));
            }


        }
    };

}
