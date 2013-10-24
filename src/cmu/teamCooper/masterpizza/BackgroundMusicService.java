package cmu.teamCooper.masterpizza;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

public class BackgroundMusicService extends Service {
    MediaPlayer player;
    
    public IBinder onBind(Intent arg0) {

        return null;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();


        player = MediaPlayer.create(this, R.raw.background);
        player.setLooping(true); // Set looping
        
        int maxVolume = 50;
        int currVolume = 50;
        float log1=(float)(Math.log(maxVolume-currVolume)/Math.log(maxVolume));
        player.setVolume(0,1-log1);

    }
    public int onStartCommand(Intent intent, int flags, int startId) {


        player.start();

        return 1;
    }

    public void onStart(Intent intent, int startId) {
        // TODO

    }
    public IBinder onUnBind(Intent arg0) {
        // TODO Auto-generated method stub

        return null;
    }

//    public void onStop() {
//
//    }
//    public void onPause() {
//
//    }
    @Override
    public void onDestroy() {

        player.stop();
        player.release();
    }

    @Override
    public void onLowMemory() {

    }
}