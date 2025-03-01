package com.example.localplayer;

import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;

public class player extends AppCompatActivity {
    private boolean isPlaying = true;
    private TextView trackAlbum;
    private TextView trackArtist;
    private ImageView trackCover;
    private ImageView trackPlayPauseButton, trackBackButton, trackNextButton, trackCycleButton,trackRandomButton;
    private int trackResourceId;
    private TextView trackTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_player);

        trackTitle = findViewById(R.id.trackTitle);
        trackArtist = findViewById(R.id.trackArtist);
        trackAlbum = findViewById(R.id.trackAlbum);
        trackCover = findViewById(R.id.trackCover);
        trackPlayPauseButton = findViewById(R.id.trackPlayPauseButton);
        trackBackButton = findViewById(R.id.trackBackButton);
        trackNextButton = findViewById(R.id.trackNextButton);
        trackCycleButton= findViewById(R.id.trackCycleButton);
        trackRandomButton = findViewById(R.id.trackRandomButton);

        trackPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePlayPause();
            }
        });
        trackBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackBack();
            }
        });
        trackNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackNext();
            }
        });
        trackCycleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackCycle();
            }
        });
        trackRandomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackRandom();
            }
        });

        try {
            loadTrackData();
        } catch (IOException e) {
            Log.e("LoadTrackError", "Error al cargar los datos del track", e);
        }
    }
    private void trackRandom() {

    }
    private void trackCycle() {

    }
    private void trackNext() {

    }
    private void trackBack() {

    }
    private void loadTrackData() throws IOException {
        String trackName = getIntent().getStringExtra("TRACK_NAME");

        if (trackName == null || trackName.isEmpty()) {
            Log.e("TrackError", "TRACK_NAME es nulo o vacío en el Intent.");
            return;
        }

        Log.d("TrackInfo", "TRACK_NAME recibido: " + trackName);
        trackResourceId = getRawResourceId(trackName);

        if (trackResourceId == 0) {
            Log.e("TrackError", "No se encontró un recurso válido para: " + trackName);
            return;
        }

        Log.d("TrackInfo", "ID del recurso encontrado: " + trackResourceId);

        extractMetadata(trackResourceId);
        loadCoverArt(trackResourceId);
    }
    private int getRawResourceId(String trackName) {
        int resId = getResources().getIdentifier(trackName, "raw", getPackageName());

        if (resId == 0) {
            Log.e("ResourceError", "No se encontró el archivo en res/raw con el nombre: " + trackName);
        }

        return resId;
    }
    private void extractMetadata(int rawResId) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            AssetFileDescriptor afd = getResources().openRawResourceFd(rawResId);
            if (afd == null) {
                Log.e("MetadataError", "AssetFileDescriptor es nulo para rawResId: " + rawResId);
                return;
            }

            retriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            String title = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE);
            String artist = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            String album = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);

            Log.d("Metadata", "Title: " + title + ", Artist: " + artist + ", Album: " + album);

            trackTitle.setText(title != null ? title : "Desconocido");
            trackArtist.setText(artist != null ? artist : "Desconocido");
            trackAlbum.setText(album != null ? album : "Desconocido");
        } catch (Exception e) {
            Log.e("MetadataError", "Error al leer los metadatos", e);
        }
    }
    private void loadCoverArt(int rawResId) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();

        try {
            AssetFileDescriptor afd = getResources().openRawResourceFd(rawResId);
            if (afd == null) {
                Log.e("CoverError", "AssetFileDescriptor es nulo para rawResId: " + rawResId);
                return;
            }

            retriever.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();

            byte[] art = retriever.getEmbeddedPicture();
            if (art != null) {
                Bitmap bitmap = BitmapFactory.decodeByteArray(art, 0, art.length);
                trackCover.setImageBitmap(bitmap);
            } else {
                trackCover.setImageResource(R.drawable.ic_launcher_background);
            }
        } catch (Exception e) {
            Log.e("CoverError", "Error al obtener la portada", e);
        }
    }
    private void togglePlayPause() {
        Intent serviceIntent = new Intent(this, musicService.class);
        if (isPlaying) {
            trackPlayPauseButton.setImageResource(R.drawable.play_icon);
            serviceIntent.setAction("ACTION_PAUSE");
        } else {
            trackPlayPauseButton.setImageResource(R.drawable.pause_icon);
            serviceIntent.setAction("ACTION_PLAY");
        }
        startService(serviceIntent);
        isPlaying = !isPlaying;
    }
}