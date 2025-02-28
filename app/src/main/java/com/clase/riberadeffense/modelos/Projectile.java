package com.clase.riberadeffense.modelos;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Projectile {
    private int x;
    private int y;
    private Enemy target;
    private int speed = 12;
    private boolean isActive = true;
    private int damage;

    public Projectile(int startX, int startY, Enemy target, int damage) {
        this.x = startX;
        this.y = startY;
        this.target = target;
        this.damage = damage;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!isActive) return;
        paint.setColor(Color.YELLOW);
        canvas.drawCircle(x, y, 5, paint);
    }

    public void update() {
        if (target == null || !isActive) return;

        double angle = Math.atan2(target.getY() - y, target.getX() - x);
        x += speed * Math.cos(angle);
        y += speed * Math.sin(angle);

        double distance = Math.hypot(target.getX() - x, target.getY() - y);
        if (distance < 20) {
            target.takeDamage(damage);
            isActive = false;
        }
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

    public Enemy getTarget() {
        return target;
    }

    public void setTarget(Enemy target) {
        this.target = target;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }
}