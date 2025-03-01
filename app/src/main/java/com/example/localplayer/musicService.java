package com.example.localplayer;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class musicService extends Service {
    private int currentTrackResId = -1;
    private MediaPlayer mediaPlayer;
    private boolean isPaused = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();

            switch (action) {
                case "ACTION_PLAY":
                    if (mediaPlayer == null) {
                        String trackName = intent.getStringExtra("TRACK_NAME");
                        if (trackName != null) {
                            int newTrackResId = getRawResourceId(trackName);
                            if (newTrackResId != -1) {
                                playMusic(newTrackResId);
                            }
                        }
                    } else if (!mediaPlayer.isPlaying()) {
                        mediaPlayer.start();  // Reanudar si ya está cargado
                    }
                    break;

                case "ACTION_PAUSE":
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    break;
            }
        }
        return START_STICKY;
    }
    private void playMusic(int trackResId) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        mediaPlayer = MediaPlayer.create(this, trackResId);
        mediaPlayer.setLooping(false);
        mediaPlayer.start();
        currentTrackResId = trackResId;

        mediaPlayer.setOnCompletionListener(mp -> stopSelf());  // Stop service when track ends
    }
    private int getRawResourceId(String trackName) {
        int resId = getResources().getIdentifier(trackName, "raw", getPackageName());
        if (resId == 0) {
            Log.e("ResourceError", "Archivo no encontrado en res/raw: " + trackName);
        }
        return resId;
    }
    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        super.onDestroy();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}