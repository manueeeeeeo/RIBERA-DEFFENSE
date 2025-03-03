package com.clase.riberadeffense.modelos;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

public class BasicEnemy extends Enemy{
    // Variables para manejar la clase de basic enemy extendida de enemy
    private String currentDirection = "left";
    private Bitmap[] upAnimation, downAnimation, leftAnimation;
    private int animationFrame = 0;
    private long lastFrameTime = 0;
    private static final long FRAME_DURATION = 200;

    /**
     * @param waypoints
     * @param upAnimation
     * @param leftAnimation
     * @param speed
     * @param score
     * @param downAnimation
     * @param health
     * Constructor del enemigo basico en donde le damos todas las
     * caracteristicas como vida, velocidad, además de los arrays de
     * bitmaps para las animaciones*/
    public BasicEnemy(ArrayList<int[]> waypoints, int health, int speed, int score, Bitmap[] upAnimation, Bitmap[] downAnimation, Bitmap[] leftAnimation) {
        super(waypoints, health, speed, score);
        this.upAnimation = upAnimation;
        this.downAnimation = downAnimation;
        this.leftAnimation = leftAnimation;
    }

    @Override
    public void update() {
        super.update();

        if (currentWaypointIndex < waypoints.size()) {
            int[] targetWaypoint = waypoints.get(currentWaypointIndex);
            // Guardamos en una variable la posición 0 del array de los puntos a recorrer
            int targetX = targetWaypoint[0];
            // Guardamos en una variable la posición 1 del array de los puntos a recorrer
            int targetY = targetWaypoint[1];

            // Utilizamos el siguiente metodo para cargar las coordenadas del enemigo y comprobar y establecer una animación u otra
            if (Math.abs(targetX - x) > Math.abs(targetY - y)) {
                currentDirection = (targetX > x) ? "right" : "left";
            } else {
                currentDirection = (targetY > y) ? "down" : "up";
            }
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        // Si el enemy no está vivo volvemos sin pintar nada
        if (!isAlive()) return;

        // Obtengo en una variable el tiempo en milisegundos
        long currentTime = System.currentTimeMillis();
        // Compruebo si esa variable menos el tiempo del ultimo frame es mayor que la duración del frame establecido
        if (currentTime - lastFrameTime > FRAME_DURATION) { // Si es así
            // Cambiamos la animaciones a la siguiente imagen ciclando en la lista
            animationFrame = (animationFrame + 1) % upAnimation.length;
            // Actualizamos el ultimo tiempo de frame
            lastFrameTime = currentTime;
        }

        // Creamos una variable en donde vamos a ir cargando cada uno de los bitmaps de las animaciones
        Bitmap currentFrame = null;
        // Utilizamos un switch para ir cambiando la direccion y cargar un array de bitmap u otro
        switch (currentDirection) {
            case "up":
                currentFrame = upAnimation[animationFrame];
                break;
            case "down":
                currentFrame = downAnimation[animationFrame];
                break;
            case "left":
                currentFrame = leftAnimation[animationFrame];
                break;
        }

        // En caso de que el currentFrame sea distinto de nulo lo dibujamos
        if (currentFrame != null) {
            canvas.drawBitmap(currentFrame, x - currentFrame.getWidth() / 2, y - currentFrame.getHeight() / 2, paint);
        }
    }
}
