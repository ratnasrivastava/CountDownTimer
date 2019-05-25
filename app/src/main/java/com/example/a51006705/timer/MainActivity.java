package com.example.a51006705.timer;

import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private TextView textView_timer;
    private Button button_start_pause;
    private Button button_reset;
    private boolean timer_running;
    private final static long start_time_in_millis=600000;
    private long time_left_in_millis = start_time_in_millis;
    private CountDownTimer countDownTimer;
    private long end_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView_timer = findViewById(R.id.textView_timer);
        button_start_pause = findViewById(R.id.button_start_pause);
        button_reset = findViewById(R.id.button_reset);
        button_start_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(!timer_running){
                   startTimer();
               }
               else{
                   pauseTimer();
               }
            }
        });
        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               resetTimer();
            }
        });
    }

    private void startTimer() {
        end_time = System.currentTimeMillis() + time_left_in_millis;
        countDownTimer = new CountDownTimer(time_left_in_millis, 1000) {
            @Override
            public void onTick(long l) {
                time_left_in_millis = l;

                updateTextViewTimer();
            }

            @Override
            public void onFinish() {
                timer_running = false;
                updateButtons();
            }
        }.start();
        timer_running = true;
        updateButtons();
    }

    private void updateTextViewTimer() {
        int minutes =(int) (time_left_in_millis/1000)/60;
        int seconds = (int) ((time_left_in_millis)/1000) % 60;
        String formattedTime = String.format(Locale.getDefault(),"%02d:%02d",minutes,seconds);
        textView_timer.setText(formattedTime);
    }

    private void pauseTimer(){
        countDownTimer.cancel();
       timer_running = false;
        updateButtons();
    }
    private void resetTimer(){
        time_left_in_millis = start_time_in_millis;
        updateTextViewTimer();
        updateButtons();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("timer_running", timer_running);
        outState.putLong("time_left_in_millis", time_left_in_millis);
        outState.putLong("end_time", end_time);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        timer_running = savedInstanceState.getBoolean("timer_running");
        time_left_in_millis = savedInstanceState.getLong("time_left_in_millis");
        updateTextViewTimer();
        updateButtons();
        if(timer_running){
            end_time = savedInstanceState.getLong("end_time");
            time_left_in_millis = end_time - System.currentTimeMillis();
            startTimer();
        }
    }

    private void updateButtons() {
        if(timer_running){
            button_reset.setVisibility(View.INVISIBLE);
            button_start_pause.setText("Pause");
        }
        else{
            button_start_pause.setText("Start");
            //for time's up
            if(time_left_in_millis < 1000){
                button_start_pause.setVisibility(View.INVISIBLE);
            }
            //paused somewhere in between
            else{
                button_start_pause.setVisibility(View.VISIBLE);
            }
            //already started but in pause state
            if(time_left_in_millis < start_time_in_millis){
                button_reset.setVisibility(View.VISIBLE);
            }//not started yet
            else {
                button_reset.setVisibility(View.INVISIBLE);
            }

        }
    }
}
