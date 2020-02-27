package com.timan.taroky;

import java.io.FileInputStream;

import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Start game with loaded data
     * @param view	View	data loaded from a file
     */
    public void loadGame(View view)
    {
        String filename = "taroky.txt";
        FileInputStream fin;

        try {
            fin = openFileInput(filename);

            int c;
            String data="";
            while((c = fin.read()) != -1){
                data = data + Character.toString((char)c);
            }
            //string temp contains all the data of the file.
            fin.close();

            Intent intent = new Intent(this, NewGameActivity.class);
            intent.putExtra("loaded_game", data);
            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Start new game
     */
    public void newGame(View view)
    {
        Intent intent = new Intent(this, NewGameActivity.class);
        startActivity(intent);
    }
}
