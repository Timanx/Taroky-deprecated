package com.timan.taroky;

import java.io.FileInputStream;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	Button new_game;
	Button load_game;
	
	private void setUpNewGame(String data)
	{
		Intent intent = new Intent(this, NewGameActivity.class);
		intent.putExtra("loaded_game",data);
	    startActivity(intent);
	}
	
	private void setUpNewGame()
	{
		Intent intent = new Intent(this, NewGameActivity.class);
	    startActivity(intent);
	}
	
	private OnClickListener new_game_click = 
    		new OnClickListener()
    {
    	
    	public void onClick(View view)
        {
    		setUpNewGame();
        }

    };  
    

	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        new_game = (Button)findViewById(R.id.new_game);
        new_game.setOnClickListener(new_game_click);
        
    }
    
    public void load(View view)
	{
		String filename = "myfile.txt";
		FileInputStream fin;
		
		try {
		  fin = openFileInput(filename);
		  
		  int c;
		  String temp="";
		  while( (c = fin.read()) != -1){
		     temp = temp + Character.toString((char)c);
		  }
		  //string temp contains all the data of the file.
		  fin.close();
		  
		   
		  
		  
		  setUpNewGame(temp);
		  
		  
		} catch (Exception e) {
		  e.printStackTrace();
		}
	}
        
}
