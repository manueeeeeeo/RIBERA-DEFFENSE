package com.clase.riberadeffense.modelos;

import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;

public abstract class Enemy {
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

    public Enemy(ArrayList<int[]> waypoints, int health, int speed, int score){
        this.waypoints = waypoints;
        this.health = health;
        this.speed = speed;
        this.score = score;
        this.x = waypoints.get(0)[0];
        this.y = waypoints.get(0)[1];
    }

    public void update() {
        if (!isAlive || currentWaypointIndex >= waypoints.size()) return;

        timeOnScreen += System.currentTimeMillis() - lastUpdateTime;
        lastUpdateTime = System.currentTimeMillis();

        speedMultiplier = 1.0f + (timeOnScreen / 10000f);


        int[] targetWaypoint = waypoints.get(currentWaypointIndex);
        int targetX = targetWaypoint[0];
        int targetY = targetWaypoint[1];

        double angle = Math.atan2(targetY - y, targetX - x);
        x += (speed * speedMultiplier) * Math.cos(angle);
        y += (speed * speedMultiplier) * Math.sin(angle);

        double distance = Math.hypot(targetX - x, targetY - y);
        if (distance < 5) {
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

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            isAlive = false;
        }
    }
}