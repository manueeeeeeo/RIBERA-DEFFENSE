package com.clase.riberadeffense;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Ajustes extends AppCompatActivity {
    private SharedPreferences sharedPreferences = null;
    private Switch musica = null;
    private boolean conMusica = false;
    private MediaPlayer mediaPlayer;
    private Button btnVolver = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Ocultamos la barra de estado y hacemos la pantalla completa
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;

        // Comprobamos las versiones del sdk para que funcione correctamente
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            uiOptions |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        }

        decorView.setSystemUiVisibility(uiOptions);

        setContentView(R.layout.activity_ajustes);

        sharedPreferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
        musica = findViewById(R.id.switchMusica);
        btnVolver = findViewById(R.id.buttonJugar);

        conMusica = sharedPreferences.getBoolean("musica", false);
        musica.setChecked(conMusica);

        mediaPlayer = MediaPlayer.create(this, R.raw.musica_fondo);
        mediaPlayer.setLooping(true);

        if (conMusica) {
            mediaPlayer.start();
        }

        btnVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Ajustes.this, Inicio.class);
                startActivity(i);
                finish();
            }
        });

        musica.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                conMusica = isChecked;
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("musica", conMusica);
                editor.apply();

                if (conMusica) {
                    mediaPlayer.start();
                } else {
                    mediaPlayer.pause();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null && conMusica) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mediaPlayer != null && conMusica) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }
}