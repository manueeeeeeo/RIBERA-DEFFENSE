package com.clase.riberadeffense.modelos;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

public class BasicEnemy extends Enemy{
    private String currentDirection = "left";
    private Bitmap[] upAnimation, downAnimation, leftAnimation;
    private int animationFrame = 0;
    private long lastFrameTime = 0;
    private static final long FRAME_DURATION = 200;

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
            int targetX = targetWaypoint[0];
            int targetY = targetWaypoint[1];

            if (Math.abs(targetX - x) > Math.abs(targetY - y)) {
                currentDirection = (targetX > x) ? "right" : "left";
            } else {
                currentDirection = (targetY > y) ? "down" : "up";
            }
        }
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (!isAlive()) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime > FRAME_DURATION) {
            animationFrame = (animationFrame + 1) % upAnimation.length;
            lastFrameTime = currentTime;
        }

        Bitmap currentFrame = null;
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

        if (currentFrame != null) {
            canvas.drawBitmap(currentFrame, x - currentFrame.getWidth() / 2, y - currentFrame.getHeight() / 2, paint);
        }
    }
}
