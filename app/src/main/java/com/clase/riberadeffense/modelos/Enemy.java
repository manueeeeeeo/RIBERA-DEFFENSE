package com.clase.riberadeffense.modelos;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

public abstract class Enemy {
    // Variables básicas de la clase enemigo
    public ArrayList<int[]> waypoints;
    public int x;
    public int y;
    public int health;
    public int speed;
    public int score;
    public int currentWaypointIndex = 0;
    public boolean isAlive = true;
    private float speedMultiplier = 1.0f;
    private long timeOnScreen = 0;
    private long lastUpdateTime = System.currentTimeMillis();

    /**
     * @param health
     * @param score
     * @param speed
     * @param waypoints
     * Constructor básico de un enemigo en donde le pasamos la
     * ruta que va a tomar el enemigo, la vida base, la velocidad
     * base y el puntaje*/
    public Enemy(ArrayList<int[]> waypoints, int health, int speed, int score){
        this.waypoints = waypoints;
        this.health = health;
        this.speed = speed;
        this.score = score;
        this.x = waypoints.get(0)[0];
        this.y = waypoints.get(0)[1];
    }

    public void update() {
        // En caso de que el enemigo no este vivo o se haya pasado del final de la ruta volvemos sin hacer nada
        if (!isAlive || currentWaypointIndex >= waypoints.size()) return;

        // En la siguiente variable guardamos el tiempo que lleva el enemigo en la pantalla
        timeOnScreen += System.currentTimeMillis() - lastUpdateTime;
        // Guardamos el ultimo tiempo en el que se actualizo el enemigo
        lastUpdateTime = System.currentTimeMillis();

        // En la variable guardamos el multiplicador, más el tiempo que lleva en pantalla entre 10.000
        speedMultiplier = 1.0f + (timeOnScreen / 10000f);


        int[] targetWaypoint = waypoints.get(currentWaypointIndex);
        // Guardamos en una variable la posición 0 del array de los puntos a recorrer
        int targetX = targetWaypoint[0];
        // Guardamos en una variable la posición 1 del array de los puntos a recorrer
        int targetY = targetWaypoint[1];

        // En la siguiente variable guardo el angulo que existe entre la x y la y del enemigo
        double angle = Math.atan2(targetY - y, targetX - x);
        // Procedo a actualizar la posición de x teniendo en cuenta el multiplicaor de velocidad
        x += (speed * speedMultiplier) * Math.cos(angle);
        // Procedo a actualizar la posición de y teniendo en cuenta el multiplicaor de velocidad
        y += (speed * speedMultiplier) * Math.sin(angle);

        // Obtengo en una variable de tipo double la distancia entre las variable anteriores y las x e y del enemigo
        double distance = Math.hypot(targetX - x, targetY - y);
        // Si la distancia anterior es menor a 5
        if (distance < 5) {
            // Confirmamos que el enemigo ha pasado por ese waypoint
            currentWaypointIndex++;
        }
    }

    public abstract void draw(Canvas canvas, Paint paint);

    public ArrayList<int[]> getWaypoints() {
        return waypoints;
    }

    public void setWaypoints(ArrayList<int[]> waypoints) {
        this.waypoints = waypoints;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getCurrentWaypointIndex() {
        return currentWaypointIndex;
    }

    public void setCurrentWaypointIndex(int currentWaypointIndex) {
        this.currentWaypointIndex = currentWaypointIndex;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    /**
     * @param damage
     * Método en el que hacemos que el enemigo
     * sufra daño, en caso de que la vida
     * sea menor o igual a 0, establecemos que el
     * enemigo no esta vivo*/
    public void takeDamage(int damage) {
        // Restamos el daño hecho a la vida que tiene
        health -= damage;
        // Comrpobamos si sigue vivo o no el enemigo
        if (health <= 0) { // En caso de que no
            // Establecemos la variable como falsa
            isAlive = false;
        }
    }
}