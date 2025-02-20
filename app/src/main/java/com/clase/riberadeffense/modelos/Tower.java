package com.clase.riberadeffense.modelos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import com.clase.riberadeffense.R;

public class Tower {
    private int id;
    private int x;
    private int y;
    private int range;
    private int cooldown;
    private int cooldownCounter;
    private boolean isUnlocked;
    private Bitmap towerImage;
    private int damage;
    private int attackSpeed;
    private int level;
    private Context contexto;
    private int recursoTower;

    public Tower(int x, int y, int range, int level, int damage, int attackSpeed, int id, Context contexto){
        this.x = x;
        this.y = y;
        this.range = range;
        this.level = level;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.id = id;
        this.contexto = contexto;

        if(level == 1){
            recursoTower = R.drawable.tower11;
        }else if(level == 2){
            recursoTower = R.drawable.animtower2;
        }else if(level == 3){
            recursoTower = R.drawable.animtower3;
        }else{
            recursoTower = R.drawable.tower0;
        }

        this.towerImage = BitmapFactory.decodeResource(contexto.getResources(), recursoTower);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public int getCooldownCounter() {
        return cooldownCounter;
    }

    public void setCooldownCounter(int cooldownCounter) {
        this.cooldownCounter = cooldownCounter;
    }

    public boolean isUnlocked() {
        return isUnlocked;
    }

    public void setUnlocked(boolean unlocked) {
        isUnlocked = unlocked;
    }

    public Bitmap getTowerImage() {
        return towerImage;
    }

    public void setTowerImage(Bitmap towerImage) {
        this.towerImage = towerImage;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public int getAttackSpeed() {
        return attackSpeed;
    }

    public void setAttackSpeed(int attackSpeed) {
        this.attackSpeed = attackSpeed;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void upgradeTower(){
        level++;
        damage += 10;
        attackSpeed = Math.max(10, attackSpeed - 1);
        range += 10;
        isUnlocked = true;
    }

    public void draw(Canvas canvas, Paint paint) {
        if (!isUnlocked) {
            canvas.drawBitmap(towerImage, x - 25, y - 25, null);
        } else {
            canvas.drawBitmap(towerImage, x - 25, y - 25, null);
        }
    }
}