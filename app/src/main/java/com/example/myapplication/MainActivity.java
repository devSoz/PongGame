package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import static java.lang.Boolean.FALSE;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showScore();
        clickListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("test","resume");
        showScore();
    }

    //get score from the shared preferences
    private void showScore()
    {
        SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref",MODE_PRIVATE);
        Integer highScore;
        highScore = Integer.parseInt(sharedPreferences.getString("highScore","0")) ;
        TextView score = (TextView) findViewById(R.id.txtScore);
        score.setText(String.valueOf(highScore));
    }


    private void clickListener() {
        ImageButton b1 = (ImageButton) findViewById(R.id.btn1);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2 = new Intent(MainActivity.this, gameActivity.class);
                startActivity(intent2);
            }
        });
    }


}