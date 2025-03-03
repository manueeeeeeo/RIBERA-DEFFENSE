package com.clase.riberadeffense.modelos;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

public class Projectile {
    // Variables necesarias para administrar la clase de projectile
    private int x;
    private int y;
    private Enemy target;
    private int speed = 12;
    private boolean isActive = true;
    private int damage;
    private long creationTime;

    /**
     * @param damage
     * @param startX
     * @param startY
     * @param target
     * Cosntructor de projectiles en donde le damos
     * las variables básicas como las coordenadas en
     * las que empieza el disparo, el enemigo al que ha de seguir
     * y el daño que va ha hacer*/
    public Projectile(int startX, int startY, Enemy target, int damage) {
        this.x = startX;
        this.y = startY;
        this.target = target;
        this.damage = damage;
        this.creationTime = System.currentTimeMillis();
    }

    public void draw(Canvas canvas, Paint paint) {
        // En caso de que el disparo no este activo no hacemos nada
        if (!isActive) return;
        // Establecemos un color amarillo
        paint.setColor(Color.YELLOW);
        // Dibujamos un circulo en x e y de 5 de radio y de color amarillo
        canvas.drawCircle(x, y, 5, paint);
    }

    public void update() {
        // En caso de que el enemigo al que sigue sea nulo o no este activo retornamos sin hacer nada
        if (target == null || !isActive) return;

        // Guardamos en una variable el tiempo que lleva activo ese projecil
        long elapsedTime = System.currentTimeMillis() - creationTime;

        // En caso de que sea superior a 2 segundos
        if (elapsedTime > 2000) {
            // Le desactivamos
            isActive = false;
            // Volvemos
            return;
        }

        // Obtenemos el angulo con referencia a las coordenaas del projectil y del enemigo
        double angle = Math.atan2(target.getY() - y, target.getX() - x);
        // Actualizamos la x teniendo en cuenta el angulo
        x += speed * Math.cos(angle);
        // Actualizamos la y teniendo en cuenta el angulo
        y += speed * Math.sin(angle);

        // Calgulamos la distancia que existe entre el enemigo y el projectil
        double distance = Math.hypot(target.getX() - x, target.getY() - y);

        // Si esa destancia es menor que 20
        if (distance < 20) {
            // Infringimos daño al enemigo
            target.takeDamage(damage);
            // Establecemos que el projectile no esta activo ya
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