package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import androidx.annotation.NonNull;


import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

//Custom view class
public class CanvasView extends SurfaceView implements Runnable {


    public Paint paint;
    public Path mPath;
    public int yCnt, xCnt;
    SurfaceHolder surf;
    Thread gameThread=null;
    public Canvas mCanvas;
    private long prevTime;

    enum POSITION{LEFT, RIGHT, STOP};
    public Context context;
    public Paint mPaint;
    public float mX, mY;
    public int screenx=0, screeny=0,init=1, delaytime=100,score=0, sliderSpeed=30;
    public Slider slider;   //Object of slider class
    public Ball ball;       //Object of ball class
    private SoundPool soundPool;
    private int gameOver, sliderHit, wallHit;
    float pressX, pressY;
    private Activity activity;
    public Boolean bRun=true,bStart=false;

    public  CanvasView(Context context, int x, int y){
      super(context) ;
        prevTime= System.currentTimeMillis();
        screenx= x;
        screeny=y-160;
        xCnt=screenx/30;
        yCnt=screeny/30;
        slider=new Slider(screenx,screeny); //create object and position to center of screen
        ball=new Ball(slider.x ,slider.y-slider.height, slider.height-1);
        ball.frameX=screenx;
        ball.frameY=screeny;
        surf=getHolder();
        soundEnable(context);
        activity=(Activity) context;
        surf.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(@NonNull SurfaceHolder holder) {
                mPath = new Path();
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                paint.setStrokeJoin(Paint.Join.ROUND);
                paint.setStrokeWidth(4f);
                onStart();
                ballCollision(); //Check if ball collides the slider
                drawCanvas(); //keep redrawing canvas
            }

            @Override
            public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

            }
        });




    }
    //Enable sound when hit on wall or slider or when game is over
    public void soundEnable(Context context){
        if (Build.VERSION.SDK_INT
                >= Build.VERSION_CODES.LOLLIPOP) {
            AudioAttributes
                    audioAttributes
                    = new AudioAttributes
                    .Builder()
                    .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build();
            soundPool= new SoundPool
                    .Builder()
                    .setMaxStreams(3)
                    .setAudioAttributes(audioAttributes)
                    .build();
        }
        else {
            soundPool= new SoundPool(3, AudioManager.STREAM_MUSIC,0);
        }

        //beep sound for hit and game over
        gameOver= soundPool.load(context, R.raw.beep2,1);
        wallHit= soundPool.load(context, R.raw.beep4,1);
        sliderHit= soundPool.load(context, R.raw.beep4,1);
    }

    @Override
    public void run(    ){
        // Capture the current time in milliseconds in startFrameTime
        while(bRun){
        long currentFrameTime = System.currentTimeMillis();
        //draw every 50 millisecond based on delaytime variable
        if( System.currentTimeMillis() - prevTime >= delaytime)
        {
            prevTime = System.currentTimeMillis();
            //update ball and slider position
            currentState();

            // Draw the frame
            drawCanvas();
        }

        }
    }

    //play different sound based on the action
    public void playSound(int i)
    {
        switch (i) {
            case 1:
                soundPool.play(gameOver, 1, 1, 0, 0, 1);
                break;
            case 2:
                soundPool.play(wallHit, 1, 1, 0, 0, 1);
                break;
            case 3:
                soundPool.play(sliderHit, 1, 1, 0, 0, 1);
                break;
        }
    }
    //called through thread to draw the canvas
    public void drawCanvas(){
        if (surf.getSurface().isValid()) {
            // Lock the mCanvas ready to draw
            mCanvas = surf.lockCanvas();

            mCanvas.drawColor(Color.WHITE);
            paint.setColor(Color.BLUE);

            mCanvas.drawRect(slider.getValues(),paint);//Slider refresh
            paint.setColor(getResources().getColor(R.color.ball));
            mCanvas.drawRect(ball.getValues(),paint);//Ball refresh

            // Draw the mScore
            paint.setColor(Color.BLACK);
            paint.setTextSize(40); //Refresh score
            mCanvas.drawText("Score: " + String.valueOf( score),  10, 50, paint);
           // Log.d("test",String.valueOf(ball.y) + "," + String.valueOf( ball.x) + "," + String.valueOf(ball.xvelocity) + "," + String.valueOf(ball.yvelocity) );

            surf.unlockCanvasAndPost(mCanvas);

    }}



    //Called thru thread to update the postion of ball and slider
    public void currentState()
    {
        if(slider.position==POSITION.LEFT)
        {
            slider.x =slider.x-sliderSpeed; //set based on speed
        }
        else if (slider.position==POSITION.RIGHT)
        {
            slider.x=slider.x+sliderSpeed; //set based on speed
        }
        if(slider.x<0) slider.x=0;
        if(slider.x+slider.width>screenx) slider.x=screenx-slider.width;
        ball.moveBall();
        //if ball collides the slider change direction of ball and move towards random y coordinate
        //if ball misses, end the game
        ballCollision();

    }



    public void ballCollision(){
        if (ball.getValues().bottom > ball.frameY){
            //missed, so update the score in shared preferences and end the game
            storeScore();
            playSound(1);
            ((Activity) getContext()).finish();
        }

    else if (RectF.intersects(ball.getValues(), slider.getValues()))
        {
            //if ball collides the slider, increase score and move ball in next random direction
            score+=2;

            //get random for left or right;
            Random generator = new Random();
           // ball.maxY=generator.nextInt(ball.frameY);
            //if (ball.maxY < 20) ball.maxY=20;
            if(generator.nextInt(2)==0)
                ball.xvelocity = -ball.xvelocity;
            if(ball.yvelocity>0)
                ball.yvelocity=-ball.yvelocity;
            playSound(3);
        }

    }



    @Override
    public boolean onTouchEvent(MotionEvent event){
        Log.d("test","Touch");
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
            {
                //drags the slider if bStart is true and set the direction of movement
                if (bStart) {
                    if(slider.getValues().contains(pressX,pressY))
                    {
                    if (event.getX() > pressX)
                        batMoveState(POSITION.RIGHT); //RIght
                   else
                        batMoveState(POSITION.LEFT); //RIght
                    }

                }
            }

            case MotionEvent.ACTION_DOWN: {

                pressX=event.getX();
                pressY=event.getY();
                //set bStart to true if user presses on the slider
                if(slider.getValues().contains(pressX,pressY))
                     bStart=true;

               break;
            }
            case MotionEvent.ACTION_UP:
                //stop slider movement
                batMoveState(POSITION.STOP);
                bStart=false;
                break;
        }
        return true;
    }

    //stores the highest score in shared preferences to show in main screen
    //This function is called when the balls misses
    private void storeScore() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", MODE_PRIVATE);
        Integer highScore;
        highScore = Integer.parseInt(sharedPreferences.getString("highScore", "0"));

        if (score > highScore) {
            SharedPreferences.Editor myEdit = sharedPreferences.edit();
            myEdit.putString("highScore", String.valueOf(score));
            myEdit.commit();
        }
    }

    public void batMoveState(POSITION pos)
    {
        slider.position=pos;
    }


    public void onStop(){
        bRun = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            Log.e("Error:", "joining thread");
        }
    }

    public void onStart(){
        bRun = true;
        gameThread = new Thread(this);
        gameThread.start();
    }


    ///////////////////////////Ball class
    public class Ball{
        public POSITION position;
        public int x, y, size,speed;
        //maxX & maxY shows the maximum ball can move
        public int xvelocity, yvelocity,frameX, frameY, maxX, maxY ;
        boolean left, up, flag=false;
        private RectF rect;
        public Ball(int sx,int sy,int size){
            this.size=size;
            resetBall(sx,sy-110);
         }
         //calls initially and when it hits the slider
        public void resetBall(int sx, int sy)
        {
            this.x=sx;
            this.y=sy-this.size;
            this.rect=new RectF(this.x,this.y,this.x+this.size ,this.y+this.size);
            speed=-10;
            xvelocity=speed;
            yvelocity=speed;
            up=true;
            left=true;
        }
        public RectF getValues()
        {
            x+=xvelocity;
            y+=yvelocity;
            rect.left=x;
            rect.top=y;
            rect.right=x+size;
            rect.bottom=y+size;
            return rect;
        }

        //logic to move ball in the required direction(based on velocity attribute
        //and play sound when hits the wall
        public void moveBall(){
            up=true;
            left=true;
            if(x<20 && y<20)
            {
                ball.x=10;
                ball.y=30;
            }
            else {
                if (y < 0) {
                    yvelocity = -yvelocity;
                    playSound(2);
                } else if (x < 0) {
                    xvelocity = -xvelocity;
                    playSound(2);
                } else if ((x + size) > frameX) {
                    xvelocity = -xvelocity;
                    playSound(2);
                }
            }
        }


    };


    ///////////////////////////Slider class
    public class Slider{
        public POSITION position;
        public int x, y, frameX, frameY,width, height;
        public RectF rect;
        public Slider(int sx,int sy){
            frameX=sx;
            frameY=sy;
            this.width=sx/3;
            this.height=sy/21;
            resetSlider();
        }
        //Called Initially to position the slider
        public void resetSlider()
        {
            this.x=(frameX/2)-(this.width/2);
            this.y=(frameY-this.height);
            this.rect=new RectF(this.x,this.y,this.x+this.width ,this.y+this.height);

        }

        public void setBatpos(POSITION pos){
            this.position=pos;
        }

        public RectF getValues()
        {
            rect.left=x;
            rect.top=y;
            rect.right=x+width;
            rect.bottom=y+height;
            return rect;
        }

    };


}