package playdhun.application.com.playdhun;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Type;
import java.util.ArrayList;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/**
 * Created by Suvam on 9/4/2017.
 */

/*
 * This util is used to store the shared preferences
 * like
 */
public class StorageUtil {

    private static final String STORAGE = "playdhun.application.com.playdhun.STORAGE";
    private SharedPreferences sharedPreferences;
    private Context context;

    public StorageUtil(Context context) {
        this.context = context;
    }

    /*
     To store the audio
     */
    public void storeAudio(ArrayList<Audio> audioFiles) {
        sharedPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(audioFiles);
        editor.putString("audioFiles", json);
        editor.apply();
    }

    public ArrayList<Audio> getAudio() {
        sharedPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("audioFiles", null);
        Type type = new TypeToken<ArrayList<Audio>>(){}.getType();
        return gson.fromJson(json, type);
    }

    public void storeAudioIndex(int index){
        sharedPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("audioIndex", index);
        editor.apply();
    }

    public int getAudioIndex() {
        sharedPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        return sharedPreferences.getInt("audioIndex", -1); //-1 is the default value
    }

    public void clearCachedAudioPlayList() {
        sharedPreferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }
}
