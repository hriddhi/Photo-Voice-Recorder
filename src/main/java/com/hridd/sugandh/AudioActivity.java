package com.hridd.sugandh;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class AudioActivity extends AppCompatActivity {

    private Button play;
    private ImageView stop, record;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    private MediaPlayer mediaPlayer;

    TextView timer ;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L ;
    Handler handler;
    int Seconds, Minutes;
    boolean flag,flag_1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);
        play = (Button) findViewById(R.id.play);
        stop = (ImageView) findViewById(R.id.stop);
        record = (ImageView) findViewById(R.id.record);
        timer = findViewById(R.id.tvTimer);

        flag = false;
        flag_1 = false;

        handler = new Handler() ;

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mediaPlayer!=null && flag_1){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    flag_1 = false;
                }

                play.setText("PLAYBACK AUDIO");

                flag = true;

                StartTime = 0L ;
                TimeBuff = 0L ;
                UpdateTime = 0L ;
                Seconds = 0 ;
                Minutes = 0 ;

                timer.setText("00:00");

                StartTime = SystemClock.uptimeMillis();
                handler.postDelayed(runnable, 0);

                outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

                setupMediaRecorder();

                try {
                    myAudioRecorder.prepare();
                    myAudioRecorder.start();
                } catch (IllegalStateException ise) {
                    // make something ...
                } catch (IOException ioe) {
                    // make something
                }
                record.setVisibility(View.GONE);
                stop.setVisibility(View.VISIBLE);
                play.setEnabled(false);
                Toast.makeText(getApplicationContext(), "Recording Started", Toast.LENGTH_SHORT).show();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                flag = false;

                TimeBuff += MillisecondTime;
                handler.removeCallbacks(runnable);

                myAudioRecorder.stop();
                myAudioRecorder.release();
                myAudioRecorder = null;
                play.setEnabled(true);

                record.setVisibility(View.VISIBLE);
                stop.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(), "Audio Recorded Successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void play_audio(View view){

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";
        Toast.makeText(getApplicationContext(), "CLICKED", Toast.LENGTH_SHORT).show();

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
            play.setEnabled(false);
            play.setText("Playing...");
            flag_1 = true;
            //Toast.makeText(AudioActivity.this,"Playback Playing",Toast.LENGTH_SHORT).show();

        } catch (Exception e) {

        }

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
                mediaPlayer.release();
                play.setEnabled(true);
                play.setText("PLAYBACK AUDIO");
                flag_1 = false;
                //Toast.makeText(AudioActivity.this,"Playback Finished",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public Runnable runnable = new Runnable() {

        public void run() {

            MillisecondTime = SystemClock.uptimeMillis() - StartTime;

            UpdateTime = TimeBuff + MillisecondTime;

            Seconds = (int) (UpdateTime / 1000);

            Minutes = Seconds / 60;

            Seconds = Seconds % 60;

            timer.setText("" + Minutes + ":" + String.format("%02d", Seconds));

            handler.postDelayed(this, 0);
        }

    };

    private void setupMediaRecorder(){

        myAudioRecorder = new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

    }

    @Override
    public void onPause(){
        super.onPause();

        if(mediaPlayer!=null && flag_1){
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        if(flag) {

            TimeBuff += MillisecondTime;
            handler.removeCallbacks(runnable);

            myAudioRecorder.stop();
            myAudioRecorder.release();
            myAudioRecorder = null;
            play.setEnabled(true);

            record.setVisibility(View.VISIBLE);
            stop.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Audio Recorded Successfully", Toast.LENGTH_LONG).show();
        }

    }
}