package playdhun.application.com.playdhun;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

/**
 * Created by Suvam on 8/26/2017.
 */

public class MediaPlayerService extends Service implements MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnErrorListener, MediaPlayer.OnInfoListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnSeekCompleteListener, AudioManager.OnAudioFocusChangeListener {

    private final IBinder mBinder = new LocalBinder();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onAudioFocusChange(int i) {

    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {

    }
}
