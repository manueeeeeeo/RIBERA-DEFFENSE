package com.clase.riberadeffense.modelos;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.ArrayList;

public class BasicEnemy extends Enemy{
    public BasicEnemy(ArrayList<int[]> waypoints, int health, int speed, int score) {
        super(waypoints, health, speed, score);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (!isAlive()) return;
        paint.setColor(Color.RED);
        canvas.drawCircle(x, y, 15, paint);
    }
}
