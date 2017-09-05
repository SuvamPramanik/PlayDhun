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

import java.util.ArrayList;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;

public class MainActivity extends AppCompatActivity {

    //fields to take care of the permissions
    private static final int RES_PERMISSION_REQUEST_CODE = 200;
    private static final int RPS_PERMISSION_REQUEST_CODE = 201;

    //fields to play audio
    public static final String Broadcast_PLAY_NEW_AUDIO = "playdhun.application.com.playdhun.PlayNewAudio";
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

    /*
     * This function is used to play the media form any index
     */
    private void playAudio(int index) {
        if(!serviceBound){
            StorageUtil storageUtil = new StorageUtil(getApplicationContext());
            storageUtil.storeAudio(audioFiles);
            storageUtil.storeAudioIndex(index);
            Intent intent = new Intent(this, MediaPlayerService.class);
            startService(intent);
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }
        else{
            StorageUtil storageUtil = new StorageUtil(getApplicationContext());
            storageUtil.storeAudioIndex(index);
            Intent intent = new Intent(Broadcast_PLAY_NEW_AUDIO);
            sendBroadcast(intent);
        }
    }

    private void checkForPermissions(){
        int resPermissionGranted = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int rpsPermissionGranted = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        if(resPermissionGranted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_EXTERNAL_STORAGE}, RES_PERMISSION_REQUEST_CODE);
        }
        if(rpsPermissionGranted != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{READ_PHONE_STATE}, RPS_PERMISSION_REQUEST_CODE);
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
            //playAudio(audioFiles.get(0).getData());
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
            case RES_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean readStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (readStorage )
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    else {

                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case RPS_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {

                    boolean readPhone = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    if (readPhone )
                        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                    else {

                        Toast.makeText(this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
}
