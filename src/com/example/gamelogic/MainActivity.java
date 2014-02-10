//Chris Bowles - 2/8/13
//SS12 Coding Competition 2014 Project
//MainActivity.java

package com.example.gamelogic;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.app.Activity;
import com.example.gamelogic.Communication.PeerFinder;
import com.example.gamelogic.Game.GameHandler;

public class MainActivity extends Activity {
//	private TextView gameStatus;
//	private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
//	private RadioButton randomRadio1, bearRadio1, ninjaRadio1, cowboyRadio1, beginRadio1,
//						randomRadio2, bearRadio2, ninjaRadio2, cowboyRadio2, beginRadio2;
//	private Button delayButton;

    //GameState values:
    //0 - Finding server
    //1 - Waiting for position
    //2 - 3 2 1 Go
    //3 - Waiting for stability
    //4 - Opponent's choice
    //5 - Game Over

    //Position values:
    //0 - Random
    //1 - Bear
    //2 - Ninja
    //3 - Cowboy
    //4 - Begin

    //Result values:
    //0 - Error
    //1 - Server Win & Client Lose
    //2 - Server Lose & Client Win
    //3 - Server Draw & Client Draw
    Context context;

    Button btnHost;
    Button btnConnect;

    GameHandler gameHandler;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        btnConnect = (Button) findViewById(R.id.btnConnect);
        btnHost = (Button) findViewById(R.id.btnHost);

        btnHost.setOnClickListener(onClickListener);
        btnConnect.setOnClickListener(onClickListener);


//		gameStatus = (TextView)findViewById(R.id.textView1);
//
//		checkBox1 = (CheckBox)findViewById(R.id.checkBox1);
//		checkBox1.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p1Connection = !p1Connection;
//				gameLogic();
//			}
//		});
//
//		checkBox2 = (CheckBox)findViewById(R.id.checkBox2);
//		checkBox2.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p2Connection = !p2Connection;
//				gameLogic();
//			}
//		});
//
//		checkBox3 = (CheckBox)findViewById(R.id.checkBox3);
//		checkBox3.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p1Stability = !p1Stability;
//				gameLogic();
//			}
//		});
//
//		checkBox4 = (CheckBox)findViewById(R.id.checkBox4);
//		checkBox4.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p2Stability = !p2Stability;
//				gameLogic();
//			}
//		});
//
//		randomRadio1 = (RadioButton)findViewById(R.id.randomRadio1);
//		randomRadio1.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p1Position = 0;
//				gameLogic();
//			}
//		});
//
//		bearRadio1 = (RadioButton)findViewById(R.id.bearRadio1);
//		bearRadio1.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p1Position = 1;
//				gameLogic();
//			}
//		});
//
//		ninjaRadio1 = (RadioButton)findViewById(R.id.ninjaRadio1);
//		ninjaRadio1.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p1Position = 2;
//				gameLogic();
//			}
//		});
//
//		cowboyRadio1 = (RadioButton)findViewById(R.id.cowboyRadio1);
//		cowboyRadio1.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p1Position = 3;
//				gameLogic();
//			}
//		});
//
//		beginRadio1 = (RadioButton)findViewById(R.id.beginRadio1);
//		beginRadio1.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p1Position = 4;
//				gameLogic();
//			}
//		});
//
//		randomRadio2 = (RadioButton)findViewById(R.id.randomRadio2);
//		randomRadio2.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p2Position = 0;
//				gameLogic();
//			}
//		});
//
//		bearRadio2 = (RadioButton)findViewById(R.id.bearRadio2);
//		bearRadio2.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p2Position = 1;
//				gameLogic();
//			}
//		});
//
//		ninjaRadio2 = (RadioButton)findViewById(R.id.ninjaRadio2);
//		ninjaRadio2.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p2Position = 2;
//				gameLogic();
//			}
//		});
//
//		cowboyRadio2 = (RadioButton)findViewById(R.id.cowboyRadio2);
//		cowboyRadio2.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p2Position = 3;
//				gameLogic();
//			}
//		});
//
//		beginRadio2 = (RadioButton)findViewById(R.id.beginRadio2);
//		beginRadio2.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				p2Position = 4;
//				gameLogic();
//			}
//		});
//
//		delayButton = (Button)findViewById(R.id.button1);
//		delayButton.setOnClickListener(new OnClickListener()
//		{
//			public void onClick(View v)
//			{
//				if (gameState == 2)
//				{
//					stabilityCheck();
//				}
//				else if (gameState == 4)
//				{
//					gameOver();
//				}
//				else if (gameState == 5)
//				{
//					beginGame();
//				}
//			}
//		});
//
//		beginGame();
    }

    OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.equals(btnHost)) {
                gameHandler = new GameHandler(true, context);
                gameHandler.initiateConnection();
            } else if (v.equals(btnConnect)) {
                gameHandler = new GameHandler(false, context);
                new PeerFinder(context, peerFinderInterface).execute();
//                gameHandler.initiateConnection();
            }
            btnConnect.setEnabled(false);
            btnHost.setEnabled(false);

        }
    };

    PeerFinder.PeerFinderInterface peerFinderInterface = new PeerFinder.PeerFinderInterface() {
        @Override
        public void peerFound(String ip) {
                gameHandler.initiateConnection(ip);

        }

        @Override
        public void findFailed() {

        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        finish();

    }
    //	     public View.OnClickListener onClickListener = new View.OnClickListener() {
//    @Override
//    public void onClick(View v) {
//        if (v.equals(btnPosition))
//        {
//            vibrationHandler.pulsePosition();
//
//        }
//        else if (v.equals(btnGo))
//        {
//            vibrationHandler.pulseGo();
//
//        }
//        else if (v.equals(btnBear))
//        {
//            vibrationHandler.pulseBear();
//        }
//        else if (v.equals(btnNinja))
//        {
//            vibrationHandler.pulseNinja();
//        }
//        else if (v.equals(btnCowboy))
//        {
//            vibrationHandler.pulseCowboy();
//        }
//        else if (v.equals(btnWin))
//        {
//            vibrationHandler.pulseWin();
//        }
//        else if (v.equals(btnLose))
//        {
//            vibrationHandler.pulseLose();
//
//        }
//        else if (v.equals(btnDraw))
//        {
//            vibrationHandler.pulseDraw();
//        }
//
//    }
}

