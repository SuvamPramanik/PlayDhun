package playdhun.application.com.playdhun;

import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 200;
    private MediaPlayerService player;
    boolean serviceBound = false;
    //to store the local audio files
    ArrayList<Audio> audioFiles;

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            MediaPlayerService.LocalBinder binder = (MediaPlayerService.LocalBinder) iBinder;
            player = binder.getService();
            serviceBound = true;
            Toast.makeText(MainActivity.this, "Service Bound", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            serviceBound = false;
        }
    };
    private void playAudio(String media) {
        if(!serviceBound){
            Intent playerIntent = new Intent(this, MediaPlayerService.class);
            playerIntent.putExtra("media", media);
            startService(playerIntent);
            bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        else{

        }
    }

    private void checkForPermissions(){
        int sawPermissionGranted = ContextCompat.checkSelfPermission(getApplicationContext(), SYSTEM_ALERT_WINDOW);
        int resPermissionGranted = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int rpsPermissionGranted = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        if((sawPermissionGranted != PackageManager.PERMISSION_GRANTED)
                || (resPermissionGranted != PackageManager.PERMISSION_GRANTED)
                || (rpsPermissionGranted != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE, READ_PHONE_STATE, SYSTEM_ALERT_WINDOW}, PERMISSION_REQUEST_CODE);
        }
    }
    private void loadAudio(){

        ContentResolver contentResolver = getContentResolver();
           //table name
           Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
           String selection = MediaStore.Audio.Media.IS_MUSIC + "!=0";
           String sort = MediaStore.Audio.Media.TITLE + " ASC";
           StringBuilder sb = new StringBuilder();
           Cursor cursor = contentResolver.query(uri, null, selection, null, sort);
           if (cursor != null && cursor.getCount() > 0) {
               audioFiles = new ArrayList<Audio>();
               while (cursor.moveToNext()) {
                   String data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                   sb.append(data);
                   String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                   String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                   String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                   Audio audioFile = new Audio(data, title, album, artist);
                   audioFiles.add(audioFile);
               }
               Log.d("MainActivity.java", sb.toString());
          } else {
               Toast.makeText(this, "Sorry, no audio file found", Toast.LENGTH_LONG).show();
            }
            cursor.close();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForPermissions();
        loadAudio();
        //playAudio("https://upload.wikimedia.org/wikipedia/commons/6/6c/Grieg_Lyric_Pieces_Kobold.ogg");
        if(audioFiles != null || audioFiles.size() != 0) {
            playAudio(audioFiles.get(0).getData());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean("serviceState", serviceBound);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle restoreInstanceState) {
        serviceBound = restoreInstanceState.getBoolean("serviceState");
        super.onRestoreInstanceState(restoreInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serviceBound){
            unbindService(serviceConnection);
            player.stopSelf();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean readStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean readPhone = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean saw = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    if (readStorage )
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    else {

                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                        requestPermissions(new String[]{READ_EXTERNAL_STORAGE, READ_PHONE_STATE, SYSTEM_ALERT_WINDOW}, PERMISSION_REQUEST_CODE);
                    }
                }
        }
    }
}
