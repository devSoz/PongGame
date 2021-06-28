package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

public class gameActivity extends AppCompatActivity {
    private CanvasView myCanvas;
    private static  final int MENU_NEW = 1;
    private static  final int MENU_SETTING = 2;
    private static  final int MENU_QUIT = 3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Display display = getWindowManager().getDefaultDisplay();

        // Load the resolution into a Point object
        Point size = new Point();

        display.getSize(size); //get size of the window

        // Initialize canvasView and set it as the view
        myCanvas = new CanvasView(this,  size.x, size.y);
        setContentView(myCanvas);

            }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myCanvas.onStop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Intent intent = new Intent(this,MainActivity.class);// New activity
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(intent);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_NEW, 0, R.string.menu_new_game);
        menu.add(0, MENU_SETTING, 0, R.string.menu_setting);
        menu.add(0, MENU_QUIT, 0, R.string.menu_quit);

        return true;
    }

    //Menu settings
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_QUIT:
                this.onStop();
                return true;
            case MENU_SETTING:
                Toast.makeText(gameActivity.this, "Next version is coming soon..", Toast.LENGTH_SHORT).show();
                return true;
            case MENU_NEW:
                Toast.makeText(gameActivity.this, "Coming soon", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }


}