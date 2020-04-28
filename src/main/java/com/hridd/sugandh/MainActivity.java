package com.hridd.sugandh;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static int VIDEO_REQUEST = 1;
    private static int CAMERA_REQUEST = 2;
    private static int AUDIO_REQUEST = 3;
    private Uri video_data, audio_data, image_data1;
    private Bitmap image_data;

    final int PERMISSION_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    private void requestPermission() {

        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
        },PERMISSION_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:
            {

            }
        }
    }

    public void cameraClick(View view) {
       /* Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (camera_intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(camera_intent, CAMERA_REQUEST);
        } */

        if(checkPermission()){
            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);

        }else{
            requestPermission();

        }
    }

    public void videoClick(View view) {
        /*Intent video_intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        if (video_intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(video_intent, VIDEO_REQUEST);
        }*/

        if(checkPermission()){
            Intent intent = new Intent(this, VideoActivity.class);
            startActivity(intent);

        }else{
            requestPermission();
        }
    }

    public void playClick(View view) {
        Log.d("LOCATION", "playClick");
        if(checkPermission()){
            Intent intent = new Intent(this, PlaybackActivity.class);
            startActivity(intent);

        }else{
            requestPermission();
        }
    }

    public void recordClick(View view) {
        if(checkPermission()){
            Intent intent = new Intent(this, AudioActivity.class);
            startActivity(intent);

        }else{
            requestPermission();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK) {
            video_data = data.getData();
            Log.d("MSG", "OK");
            try
            {
                AssetFileDescriptor videoAsset = getContentResolver().openAssetFileDescriptor(data.getData(), "r");
                FileInputStream fis = videoAsset.createInputStream();
                File root = new File(Environment.getExternalStorageDirectory(),"video");

                if (!root.exists()) {
                    root.mkdirs();
                }

                File file = new File(root,"sugandhVideo.mp4" );

                FileOutputStream fos = new FileOutputStream(file);

                byte[] buf = new byte[1024];
                int len;
                while ((len = fis.read(buf)) > 0) {
                    fos.write(buf, 0, len);
                }
                fis.close();
                fos.close();
                Log.d("VIDEO", "Copy file successful.");
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            image_data = (Bitmap) extras.get("data");
            Log.d("MSG", "OK1");
            saveToInternalStorage(image_data);
        }
    }

    private void saveToInternalStorage(Bitmap bitmapImage) {
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);

        File mypath = new File(directory, "image_sugandh.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean checkPermission(){

        int write_result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int camera_result = ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA);

        return write_result == PackageManager.PERMISSION_GRANTED &&
                record_audio_result == PackageManager.PERMISSION_GRANTED &&
                camera_result == PackageManager.PERMISSION_GRANTED;
    }
}