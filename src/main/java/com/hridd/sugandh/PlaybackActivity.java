package com.hridd.sugandh;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PlaybackActivity extends AppCompatActivity {

    private VideoView mVideoView;
    private ImageView mImageView;
    private String outputFile;
    private MediaPlayer mediaPlayer;
    private ImageView play,pause;
    private int flag;
    public int length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playback);

        mVideoView = findViewById(R.id.videoView);
        mImageView = findViewById(R.id.viewImage);
        play = findViewById(R.id.imageViewPlay);
        pause = findViewById(R.id.imageViewPause);

        flag = 0;

        //Video File

        File path = new File(Environment.getExternalStorageDirectory(),"video");
        File f = new File("/storage/emulated/0/Android/data/com.hridd.sugandh/files", "sugandh.mp4");

        if(!f.exists()){
            Toast.makeText(this,"Record a Video first!",Toast.LENGTH_LONG).show();
        }

        mVideoView.setVideoURI(Uri.fromFile(f));

        //Audio File
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

        try {
            mediaPlayer = new MediaPlayer();

        } catch (Exception e) {
            // make something
        }

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(flag == 0) {
                        mediaPlayer.setDataSource(outputFile);
                        flag++;
                        mediaPlayer.prepare();
                        mediaPlayer.start();
                    }
                    else {
                        mediaPlayer.seekTo(length);
                        mediaPlayer.start();
                    }

                    if(mVideoView.isPlaying()){
                        mVideoView.resume();
                    } else {
                        mVideoView.start();
                    }

                } catch (Exception e) {
                    // make something
                }

                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer!=null){
                    mediaPlayer.pause();
                    length = mediaPlayer.getCurrentPosition();
                }
                mVideoView.pause();

                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
                mediaPlayer.seekTo(0);
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                flag = 0;
            }
        });
        //Image File

        loadImageFromStorage();

    }

    private String getVideoFilePath(Context context) {
        final File dir = context.getExternalFilesDir(null);
        return (dir == null ? "" : (dir.getAbsolutePath() + "/"))
                + "sugandh.mp4";
    }

    private void loadImageFromStorage()
    {
        try {
            File f = new File(Environment.getExternalStorageDirectory()+"/hello.jpg");
            if(f.exists()){
                Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
                mImageView.setImageBitmap(b);
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
    }

    public void onPause() {
        super.onPause();

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}