package com.clase.riberadeffense.modelos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;

import com.clase.riberadeffense.R;
import com.clase.riberadeffense.database.DatabaseHelper;

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
    private float width;
    private int height;
    private long lastShotTime = 0;

    private int baseWidth;
    private int baseHeight;
    private float baseAspectRatio;
    private static final float SCALE_FACTOR = 1.2f;

    private AnimationDrawable animationDrawable;
    private boolean isAnimating = false;

    public Tower(int x, int y, int range, int level, int damage, int attackSpeed, int id, Context contexto) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.level = level;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.id = id;
        this.contexto = contexto;

        Bitmap baseTowerImage = BitmapFactory.decodeResource(contexto.getResources(), R.drawable.tower0);
        if (baseTowerImage != null) {
            this.baseWidth = baseTowerImage.getWidth();
            this.baseHeight = baseTowerImage.getHeight();
            this.baseAspectRatio = (float) baseWidth / baseHeight;
        } else {
            Log.e("Tower", "La imagen base (tower0) no se pudo cargar.");
        }

        if (level == 1) {
            loadTowerImage(R.drawable.tower11);
        } else if (level == 2 || level == 3) {
            setAnimation(level);
            startAnimation();
        } else {
            loadBaseTowerImage(R.drawable.tower0);
        }
    }

    private void loadBaseTowerImage(int resource) {
        towerImage = BitmapFactory.decodeResource(contexto.getResources(), resource);
        if (towerImage != null) {
            this.width = baseWidth;
            this.height = baseHeight;
        } else {
            Log.e("Tower", "La imagen base (tower0) no se pudo cargar.");
        }
    }

    private void loadTowerImage(int resource) {
        towerImage = BitmapFactory.decodeResource(contexto.getResources(), resource);
        if (towerImage != null) {
            int originalWidth = towerImage.getWidth();
            int originalHeight = towerImage.getHeight();
            float originalAspectRatio = (float) originalWidth / originalHeight;

            if (originalAspectRatio > baseAspectRatio) {
                this.width = (int) (baseWidth * SCALE_FACTOR);
                this.height = (int) (this.width / originalAspectRatio);
            } else {
                this.height = (int) (baseHeight * SCALE_FACTOR);
                this.width = (int) (this.height * originalAspectRatio);
            }

            towerImage = Bitmap.createScaledBitmap(towerImage, (int) this.width, this.height, true);
        } else {
            Log.e("Tower", "La imagen de la torre no se pudo cargar.");
        }
    }

    private void setAnimation(int level) {
        if (level == 2) {
            animationDrawable = (AnimationDrawable) contexto.getResources().getDrawable(R.drawable.animtower2, null);
        } else if (level == 3) {
            animationDrawable = (AnimationDrawable) contexto.getResources().getDrawable(R.drawable.animtower3, null);
        }

        if (animationDrawable == null) {
            Log.e("Tower", "No se pudo cargar la animación para el nivel " + level);
        } else {
            animationDrawable.setOneShot(false);
            isAnimating = true;

            this.width = (int) (baseWidth * SCALE_FACTOR);
            this.height = (int) (baseHeight * SCALE_FACTOR);
            startAnimation();
        }
    }

    public boolean canShoot() {
        long currentTime = System.currentTimeMillis();
        return (currentTime - lastShotTime >= attackSpeed);
    }
    public void shoot() {
        lastShotTime = System.currentTimeMillis();
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

    public void upgradeTower() {
        level++;
        damage += 25;
        attackSpeed = Math.max(10, (int) (attackSpeed * 0.9));
        isUnlocked = true;

        if (level == 1) {
            range += 150;
        } else {
            range += 100;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(contexto);
        dbHelper.saveTower(this);

        float centerX = x;
        float centerY = y;

        if (level == 2 || level == 3) {
            setAnimation(level);
        } else {
            loadTowerImage(R.drawable.tower11);
        }

        x = (int)centerX;
        y = (int)centerY;

        float left = x - width / 2;
        float top = y - height / 2;
        float right = x + width / 2;
        float bottom = y + height / 2;

        if (isAnimating && animationDrawable != null) {
            animationDrawable.setBounds((int) left, (int) top, (int) right, (int) bottom);
        }
    }

    public void draw(Canvas canvas, Paint paint) {
        float left = x - width / 2;
        float top = y - height / 2;
        float right = x + width / 2;
        float bottom = y + height / 2;

        if (isAnimating && animationDrawable != null) {
            animationDrawable.setBounds((int) left, (int) top, (int) right, (int) bottom);

            if (!animationDrawable.isRunning()) {
                animationDrawable.start();
            }

            animationDrawable.draw(canvas);
        } else if (towerImage != null) {
            canvas.drawBitmap(towerImage, left, top, null);
        }
    }

    public boolean isTouched(float touchX, float touchY) {
        float left = x - width / 2;
        float right = x + width / 2;
        float top = y - height / 2;
        float bottom = y + height / 2;

        return touchX >= left && touchX <= right && touchY >= top && touchY <= bottom;
    }

    public void startAnimation() {
        if (animationDrawable != null && !animationDrawable.isRunning()) {
            animationDrawable.start();
        }
    }

    public boolean isEnemyInRange(Enemy enemy) {
        double distance = Math.hypot(enemy.getX() - getX(), enemy.getY() - getY());
        return distance <= getRange();
    }
}