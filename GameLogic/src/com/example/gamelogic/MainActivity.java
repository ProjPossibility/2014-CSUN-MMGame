//Chris Bowles - 2/8/13
//SS12 Coding Competition 2014 Project
//MainActivity.java

package com.example.gamelogic;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.TextView;
import android.app.Activity;

public class MainActivity extends Activity
{
	private TextView gameStatus;
	private CheckBox checkBox1, checkBox2, checkBox3, checkBox4;
	private RadioButton randomRadio1, bearRadio1, ninjaRadio1, cowboyRadio1, beginRadio1,
						randomRadio2, bearRadio2, ninjaRadio2, cowboyRadio2, beginRadio2;
	private Button delayButton;
	private boolean p1Connection, p2Connection, p1Stability, p2Stability;
	private int gameState, p1Position, p2Position, p1Result, p2Result;
	private int[][] result =   {{0, 0, 0, 0},
								{0, 3, 1, 2},
								{0, 2, 3, 1},
								{0, 1, 2, 3}};
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
	
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		gameStatus = (TextView)findViewById(R.id.textView1);
		
		checkBox1 = (CheckBox)findViewById(R.id.checkBox1);
		checkBox1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p1Connection = !p1Connection;
				gameLogic();
			}
		});
		
		checkBox2 = (CheckBox)findViewById(R.id.checkBox2);
		checkBox2.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p2Connection = !p2Connection;
				gameLogic();
			}
		});
		
		checkBox3 = (CheckBox)findViewById(R.id.checkBox3);
		checkBox3.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p1Stability = !p1Stability;
				gameLogic();
			}
		});
		
		checkBox4 = (CheckBox)findViewById(R.id.checkBox4);
		checkBox4.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p2Stability = !p2Stability;
				gameLogic();
			}
		});
		
		randomRadio1 = (RadioButton)findViewById(R.id.randomRadio1);
		randomRadio1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p1Position = 0;
				gameLogic();
			}
		});
		
		bearRadio1 = (RadioButton)findViewById(R.id.bearRadio1);
		bearRadio1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p1Position = 1;
				gameLogic();
			}
		});
		
		ninjaRadio1 = (RadioButton)findViewById(R.id.ninjaRadio1);
		ninjaRadio1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p1Position = 2;
				gameLogic();
			}
		});
		
		cowboyRadio1 = (RadioButton)findViewById(R.id.cowboyRadio1);
		cowboyRadio1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p1Position = 3;
				gameLogic();
			}
		});
		
		beginRadio1 = (RadioButton)findViewById(R.id.beginRadio1);
		beginRadio1.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p1Position = 4;
				gameLogic();
			}
		});
		
		randomRadio2 = (RadioButton)findViewById(R.id.randomRadio2);
		randomRadio2.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p2Position = 0;
				gameLogic();
			}
		});
		
		bearRadio2 = (RadioButton)findViewById(R.id.bearRadio2);
		bearRadio2.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p2Position = 1;
				gameLogic();
			}
		});
		
		ninjaRadio2 = (RadioButton)findViewById(R.id.ninjaRadio2);
		ninjaRadio2.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p2Position = 2;
				gameLogic();
			}
		});
		
		cowboyRadio2 = (RadioButton)findViewById(R.id.cowboyRadio2);
		cowboyRadio2.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p2Position = 3;
				gameLogic();
			}
		});
		
		beginRadio2 = (RadioButton)findViewById(R.id.beginRadio2);
		beginRadio2.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				p2Position = 4;
				gameLogic();
			}
		});
		
		delayButton = (Button)findViewById(R.id.button1);
		delayButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if (gameState == 2)
				{
					stabilityCheck();
				}
				else if (gameState == 4)
				{
					gameOver();
				}
				else if (gameState == 5)
				{
					beginGame();
				}
			}
		});
		
		beginGame();
	}
	
	public void gameLogic()
	{
		if (gameState == 0)
		{
			if (p1Connection && p2Connection)
			{
				getIntoPosition();
			}
		}
		else //Game State is 1, 2, 3 or 4
		{
			if (p1Connection && p2Connection)
			{
				if (gameState == 1)
				{
					if (p1Position == 4 && p2Position == 4 && p1Stability && p2Stability)
					{
						commenceGame();
					}
				}
				else if (gameState == 3)
				{
					if (p1Position > 0 && p1Position < 4 && p2Position > 0 && p2Position < 4 && p1Stability && p2Stability)
					{
						determineResult();
					}
				}
			}
			else
			{
				beginGame();
			}
		}
	}
	
	public void beginGame()
	{
		gameStatus.setText("Both players: Please connect!");
		gameState = 0;
		checkBox1.setChecked(false);
		p1Connection = false;
		checkBox2.setChecked(false);
		p2Connection = false;
		checkBox3.setChecked(false);
		p1Stability = false;
		checkBox4.setChecked(false);
		p2Stability = false;
		randomRadio1.setChecked(true);
		bearRadio1.setChecked(false);
		ninjaRadio1.setChecked(false);
		cowboyRadio1.setChecked(false);
		beginRadio1.setChecked(false);
		p1Position = 0;
		randomRadio2.setChecked(true);
		bearRadio2.setChecked(false);
		ninjaRadio2.setChecked(false);
		cowboyRadio2.setChecked(false);
		beginRadio2.setChecked(false);
		p2Position = 0;
		p1Result = 0;
		p2Result = 0;
	}
	
	public void getIntoPosition()
	{
		gameStatus.setText("Both players: Get Into Position!");
		//Play the audio & rumble for Get into Position here
		gameState = 1;
	}
	
	public void commenceGame()
	{
		gameStatus.setText("3, 2, 1, Go!");
		//Play the audio & rumble for 3 2 1 Go here
		gameState = 2;
	}
	
	public void stabilityCheck()
	{
		gameStatus.setText("Waiting for both players to make a move.");
		gameState = 3;
	}
	
	public void determineResult()
	{
		gameState = 4;
		switch (p2Position)
		{
		case 1:
			gameStatus.setText("Player 2 chose Bear!");
			//Play the audio & rumble for bear here
			break;
		case 2:
			gameStatus.setText("Player 2 chose Ninja!");
			//Play the audio & rumble for ninja here
			break;
		case 3:
			gameStatus.setText("Player 2 chose Cowboy!");
			//Play the audio & rumble for cowboy here
			break;
		default:
			gameStatus.setText("Error: Bad position...");
		}
		
		p1Result = p1Position;
		p2Result = p2Position;
	}
	
	public void gameOver()
	{
		gameState = 5;
		switch (result[p1Result][p2Result])
		{
		case 1:
			gameStatus.setText("Game over. Player 1 wins!");
			//Play the audio & rumble for winning here
			break;
		case 2:
			gameStatus.setText("Game over. Player 2 wins!");
			//Play the audio & rumble for losing here
			break;
		case 3:
			gameStatus.setText("Game over. It was a draw...");
			//Play the audio & rumble for a draw here
			break;
		default:
			gameStatus.setText("Error: Bad positions...");
		}
	}
}