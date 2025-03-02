package com.clase.riberadeffense;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.clase.riberadeffense.database.DatabaseHelper;

public class Inicio extends AppCompatActivity {
    private ImageView btnJugar = null;
    private ImageView btnAjustes = null;
    private ImageView btnSalir = null;
    private DatabaseHelper bd = null;
    private MediaPlayer mediaPlayer;
    private boolean conMusica;
    private SharedPreferences sharedPreferences;
    private ImageView titulo = null;

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

        setContentView(R.layout.activity_inicio);

        // Obtenemos los elementos de la interfaz
        btnJugar = (ImageView) findViewById(R.id.imageViewPlay);
        btnAjustes = (ImageView) findViewById(R.id.imageViewMenu);
        btnSalir = (ImageView) findViewById(R.id.imageViewExit);
        titulo = (ImageView) findViewById(R.id.imageView);

        ObjectAnimator temblorX = ObjectAnimator.ofFloat(titulo, "translationX", -10f, 10f);
        temblorX.setRepeatCount(ObjectAnimator.INFINITE);
        temblorX.setRepeatMode(ObjectAnimator.REVERSE);
        temblorX.setDuration(100);

        ObjectAnimator parpadeo = ObjectAnimator.ofFloat(titulo, "alpha", 0f, 1f);
        parpadeo.setRepeatCount(ObjectAnimator.INFINITE);
        parpadeo.setRepeatMode(ObjectAnimator.REVERSE);
        parpadeo.setDuration(500);

        AnimatorSet animaciones = new AnimatorSet();
        animaciones.playTogether(temblorX, parpadeo);
        animaciones.start();

        bd = new DatabaseHelper(this);

        // Evento de cuando pulsamos el botón de salir
        btnSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                        view,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f)
                );
                scaleDown.setDuration(100);

                ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                        view,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
                );
                scaleUp.setDuration(100);

                scaleDown.start();
                scaleDown.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        scaleUp.start();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 100);
                    }
                });
            }
        });

        // Evento de cuando pulsamos el botón de ajustes
        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                        view,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f)
                );
                scaleDown.setDuration(100);

                ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                        view,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
                );
                scaleUp.setDuration(100);

                scaleDown.start();
                scaleDown.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        scaleUp.start();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(Inicio.this, Ajustes.class);
                                startActivity(i);
                                finish();
                            }
                        }, 100);
                    }
                });
            }
        });

        btnJugar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bd.resetDatabase();
                ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(
                        view,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f)
                );
                scaleDown.setDuration(100);

                ObjectAnimator scaleUp = ObjectAnimator.ofPropertyValuesHolder(
                        view,
                        PropertyValuesHolder.ofFloat(View.SCALE_X, 1f),
                        PropertyValuesHolder.ofFloat(View.SCALE_Y, 1f)
                );
                scaleUp.setDuration(100);

                scaleDown.start();
                scaleDown.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        scaleUp.start();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent i = new Intent(Inicio.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }
                        }, 100);
                    }
                });
            }
        });

        sharedPreferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
        conMusica = sharedPreferences.getBoolean("musica", false);

        mediaPlayer = MediaPlayer.create(this, R.raw.musica_fondo);
        mediaPlayer.setLooping(true);

        if (conMusica) {
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