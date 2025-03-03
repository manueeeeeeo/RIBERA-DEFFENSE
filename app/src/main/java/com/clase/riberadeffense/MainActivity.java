package com.clase.riberadeffense;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    // Creamos e inicializamos todas las variables de obtejos que vamos a usar en esta clase
    private MediaPlayer mediaPlayer = null;
    private boolean conMusica = false;
    private SharedPreferences sharedPreferences = null;

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
        setContentView(new GameEngine(this));

        // Inicializamos las preferencias
        sharedPreferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
        // Guardamos en una variable el resultado obtenido de preferencias, si no existe por defecto es falso
        conMusica = sharedPreferences.getBoolean("musica", false);

        // Inicializamos correctamente el mediaPlayer y establecemos el recurso que vamos a utilizar
        mediaPlayer = MediaPlayer.create(this, R.raw.musica_fondo);
        // Establecemos que la canción se repita en loop
        mediaPlayer.setLooping(true);

        // Comprobamos si la variable de jugar con música es verdadera
        if (conMusica) { // Si así es
            // Iniciamos el reproductor de música
            mediaPlayer.start();
        }
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