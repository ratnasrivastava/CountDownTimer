package com.example.a51006705.timer;

import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;

public class MainActivity extends AppCompatActivity {

    private EditText editText_setTime;
    private TextView textView_timer;

    private Button button_set;
    private Button button_start_pause;
    private Button button_reset;
    private boolean timer_running;
    private long start_time_in_millis;
    //Remove this line cause Shared prefs default will set this.
    //private long time_left_in_millis = start_time_in_millis;
    private long time_left_in_millis;
    private CountDownTimer countDownTimer;
    private long end_time;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText_setTime = findViewById(R.id.editText_setTime);
        button_set = findViewById(R.id.button_set);
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
        button_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long millisInSeconds;
                String enteredTime = editText_setTime.getText().toString();
                if(enteredTime == ""){
                    Toast.makeText(MainActivity.this, "Entered Time can not be empty", Toast.LENGTH_SHORT).show();
                }
                else{
                millisInSeconds = Long.parseLong(enteredTime) * 60000;
                if(millisInSeconds == 0){
                    Toast.makeText(MainActivity.this, "Time cannot be zero", Toast.LENGTH_SHORT).show();
                }
                else {
                    setTime(millisInSeconds);
                }
            }}
        });
    }
   // to close keyboard after setting time
    private void closeKeyBoard(){
        View view = this.getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    private void setTime(long millisInSeconds){
        start_time_in_millis = millisInSeconds;

      resetTimer();
      closeKeyBoard();
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
        int hours = (int) ((time_left_in_millis/1000)/3600);
        int minutes =(int) (((time_left_in_millis/1000)%3600)/60);
        int seconds = (int) ((time_left_in_millis)/1000) % 60;
        String formattedTime;
        if(hours > 0){
            formattedTime = String.format(Locale.getDefault(), "%d:%02d:%02d", hours, minutes, seconds);
        }
        else {
            formattedTime = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        }
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
/* for orientation change
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
*/
    private void updateButtons() {
        if(timer_running){
            button_set.setVisibility(View.INVISIBLE);
            editText_setTime.setVisibility(View.INVISIBLE);
            button_reset.setVisibility(View.INVISIBLE);
            button_start_pause.setText("Pause");
        }
        else{
            button_set.setVisibility(View.VISIBLE);
            editText_setTime.setVisibility(View.VISIBLE);
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

    /*
    *Timer will be running even if service is stop. i.e app is cleared from the recent or we press back button.
    */

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        start_time_in_millis = prefs.getLong("start_time_in_millis", start_time_in_millis);
        time_left_in_millis = prefs.getLong("time_left_in_millis", start_time_in_millis);
        timer_running = prefs.getBoolean("timer_running", false);
        updateTextViewTimer();
        updateButtons();
        if(timer_running) {
            end_time = prefs.getLong("end_time", 0);
            time_left_in_millis = end_time - System.currentTimeMillis();

            if(time_left_in_millis < 0){
                time_left_in_millis = 0;
                timer_running = false;
                updateTextViewTimer();
                updateButtons();
            }
            else {
                startTimer();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences prefs = getSharedPreferences("sharedPrefs",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putLong("start_time_in_millis", start_time_in_millis);
        editor.putLong("time_left_in_millis", time_left_in_millis);
        editor.putBoolean("timer_running", timer_running);
        editor.putLong("end_time", end_time);

        editor.apply();
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
