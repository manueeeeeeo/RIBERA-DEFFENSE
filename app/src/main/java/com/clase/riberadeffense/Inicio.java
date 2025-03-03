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
    // Creamos e inicializamos todas las variables de obtejos que vamos a usar en esta clase
    private ImageView btnJugar = null;
    private ImageView btnAjustes = null;
    private ImageView btnSalir = null;
    private DatabaseHelper bd = null;
    private MediaPlayer mediaPlayer = null;
    private boolean conMusica = false;
    private SharedPreferences sharedPreferences = null;
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

        // Creamos un objectanimator para trasladar la imageview en el eje x
        ObjectAnimator temblorX = ObjectAnimator.ofFloat(titulo, "translationX", -10f, 10f);
        // Esteblecemos que sea infinito
        temblorX.setRepeatCount(ObjectAnimator.INFINITE);
        // Establecemos que se ejecute de un lado a otro, es decir, a la inversa cada vez
        temblorX.setRepeatMode(ObjectAnimator.REVERSE);
        // Establecemos la duración de la animación
        temblorX.setDuration(100);

        // Creamos un objectanimator para ejecutar el parpadeo del titulo
        ObjectAnimator parpadeo = ObjectAnimator.ofFloat(titulo, "alpha", 0f, 1f);
        // Esteblecemos que sea infinito
        parpadeo.setRepeatCount(ObjectAnimator.INFINITE);
        // Establecemos que sea a la reversa también
        parpadeo.setRepeatMode(ObjectAnimator.REVERSE);
        // Establecemos la duración de la animación
        parpadeo.setDuration(500);

        // Generamos el animator set para ejecutar las animaciones
        AnimatorSet animaciones = new AnimatorSet();
        // Ejecutamos ambas a la vez
        animaciones.playTogether(temblorX, parpadeo);
        // Iniciamos la ejecucción
        animaciones.start();

        // Inicializamos la base de datos
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
                bd.resetDatabase(); // Reiniciamos la base de datos
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