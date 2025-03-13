package com.example.localplayer;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private LinearLayout track01, track02, track03, track04, track05;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        track01 = findViewById(R.id.track01);
        track02 = findViewById(R.id.track02);
        track03 = findViewById(R.id.track03);
        track04 = findViewById(R.id.track04);
        track05 = findViewById(R.id.track05);
        track01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlayer("eyeofthetiger");
            }
        });
        track02.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlayer("illowthesun");
            }
        });
        track03.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlayer("nosurprises");
            }
        });
        track04.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlayer("sona");
            }
        });
        track05.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToPlayer("tilkingdomcome");
            }
        });
    }
    private void goToPlayer(String trackName) {
        //iniciar el servicio de música con la nueva canción
        Intent serviceIntent = new Intent(this, musicService.class);
        stopService(serviceIntent);
        serviceIntent.setAction("ACTION_PLAY");
        serviceIntent.putExtra("TRACK_NAME", trackName);
        startService(serviceIntent);

        //ir a la pantalla del reproductor
        Intent intent = new Intent(this, player.class);
        intent.putExtra("TRACK_NAME", trackName);
        startActivity(intent);
    }
}