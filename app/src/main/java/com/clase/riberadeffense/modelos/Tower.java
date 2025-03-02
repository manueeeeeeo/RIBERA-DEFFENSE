package com.clase.riberadeffense.modelos;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
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

    private AnimationDrawable animationDrawable;
    private boolean isAnimating = false;

    private static final int STANDARD_WIDTH = 150;
    private static final int STANDARD_HEIGHT = 150;

    private static final int Y_OFFSET = 15;

    private boolean isUpgrading = false;
    private long upgradeStartTime = 0;
    private static final long UPGRADE_DURATION = 1550;
    private Bitmap upgradeImage;

    private float offsetX = 0;
    private float offsetY = 0;

    private Bitmap[] towerAnimationFrames;
    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private static final int FRAME_DURATION = 100;

    private boolean isVisible = true;

    private float previousOffsetY;
    private float offsetYChange;

    public Tower(int x, int y, int range, int level, int damage, int attackSpeed, int id, Context contexto) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.level = level;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.id = id;
        this.contexto = contexto;

        if (level > 0) {
            if (this.id == 1) {
                offsetY = -Y_OFFSET - 10;
            } else if (this.id == 2) {
                offsetY = -Y_OFFSET - 40;
            } else if (this.id == 3) {
                offsetY = -Y_OFFSET - 13;
            } else if (this.id == 4) {
                offsetY = -Y_OFFSET;
            } else if (this.id == 5) {
                offsetY = -Y_OFFSET - 45;
            }
        }

        if (level == 1) {
            loadTowerImage(R.drawable.tower11);
        } else if (level == 2 || level == 3) {
            loadTowerAnimation(level);
        } else {
            loadTowerImage(R.drawable.tower0);
        }

        Bitmap originalUpgrade = BitmapFactory.decodeResource(contexto.getResources(), R.drawable.mejora);
        if (originalUpgrade != null) {
            int upgradeWidth = originalUpgrade.getWidth();
            int upgradeHeight = originalUpgrade.getHeight();
            float scaleRatio = 0.135f;
            upgradeImage = Bitmap.createScaledBitmap(originalUpgrade,
                    (int) (upgradeWidth * scaleRatio),
                    (int) (upgradeHeight * scaleRatio),
                    true);
        }
    }

    public void startUpgrade() {
        isUpgrading = true;
        upgradeStartTime = System.currentTimeMillis();

        previousOffsetY = offsetY;

        if (level + 1 > 0) {
            if (this.id == 1) {
                offsetY = -Y_OFFSET - 10;
            } else if (this.id == 2) {
                offsetY = -Y_OFFSET - 35;
            } else if (this.id == 3) {
                offsetY = -Y_OFFSET - 13;
            } else if (this.id == 4) {
                offsetY = -Y_OFFSET;
            } else if (this.id == 5) {
                offsetY = -Y_OFFSET - 45;
            }
        }

        offsetYChange = offsetY - previousOffsetY;

        if (level + 1 == 1) {
            loadTowerImage(R.drawable.tower11);
        } else if (level + 1 == 2 || level + 1 == 3) {
            loadTowerAnimation(level + 1);
        } else {
            loadTowerImage(R.drawable.tower0);
        }
    }

    private void loadTowerImage(int resource) {
        Bitmap originalImage = BitmapFactory.decodeResource(contexto.getResources(), resource);
        if (originalImage != null) {
            int originalWidth = originalImage.getWidth();
            int originalHeight = originalImage.getHeight();
            float aspectRatio = (float) originalWidth / originalHeight;

            this.width = STANDARD_WIDTH;
            this.height = (int) (STANDARD_WIDTH / aspectRatio);

            towerImage = Bitmap.createScaledBitmap(originalImage, (int) this.width, this.height, true);
        } else {
            Log.e("Tower", "La imagen de la torre no se pudo cargar.");
        }
    }

    private void loadTowerAnimation(int level) {
        if (level == 2) {
            towerAnimationFrames = new Bitmap[] {
                    scaleBitmap(R.drawable.tower21),
                    scaleBitmap(R.drawable.tower22),
                    scaleBitmap(R.drawable.tower23),
                    scaleBitmap(R.drawable.tower24)
            };
        } else if (level == 3) {
            towerAnimationFrames = new Bitmap[] {
                    scaleBitmap(R.drawable.tower31),
                    scaleBitmap(R.drawable.tower32),
                    scaleBitmap(R.drawable.tower33),
                    scaleBitmap(R.drawable.tower34),
                    scaleBitmap(R.drawable.tower35),
                    scaleBitmap(R.drawable.tower36)
            };
        }

        if (towerAnimationFrames != null && towerAnimationFrames.length > 0) {
            this.width = towerAnimationFrames[0].getWidth();
            this.height = towerAnimationFrames[0].getHeight();
        }

        isAnimating = true;
    }

    private Bitmap scaleBitmap(int resource) {
        Bitmap originalBitmap = BitmapFactory.decodeResource(contexto.getResources(), resource);
        if (originalBitmap != null) {
            int originalWidth = originalBitmap.getWidth();
            int originalHeight = originalBitmap.getHeight();
            float aspectRatio = (float) originalWidth / originalHeight;

            int scaledWidth = STANDARD_WIDTH;
            int scaledHeight = (int) (scaledWidth / aspectRatio);

            return Bitmap.createScaledBitmap(originalBitmap, scaledWidth, scaledHeight, true);
        }
        return null;
    }

    public boolean canShoot() {
        if (isUpgrading) {
            return false;
        }

        long currentTime = System.currentTimeMillis();
        return (currentTime - lastShotTime >= attackSpeed);
    }

    public void shoot() {
        lastShotTime = System.currentTimeMillis();
    }

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        this.isVisible = visible;
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

        float previousOffsetY = offsetY;

        if (level > 0) {
            if (this.id == 1) {
                offsetY = -Y_OFFSET - 10;
            } else if (this.id == 2) {
                offsetY = -Y_OFFSET - 40;
            } else if (this.id == 3) {
                offsetY = -Y_OFFSET - 13;
            } else if (this.id == 4) {
                offsetY = -Y_OFFSET;
            } else if (this.id == 5) {
                offsetY = -Y_OFFSET - 45;
            }
        }

        y += (previousOffsetY - offsetY);

        if (level == 1) {
            loadTowerImage(R.drawable.tower11);
        } else if (level == 2 || level == 3) {
            loadTowerAnimation(level);
        } else {
            loadTowerImage(R.drawable.tower0);
        }

        DatabaseHelper dbHelper = new DatabaseHelper(contexto);
        dbHelper.saveTower(this);
    }

    public void draw(Canvas canvas, Paint paint) {
        if (isUpgrading) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - upgradeStartTime < UPGRADE_DURATION) {
                float progress = (float) (currentTime - upgradeStartTime) / UPGRADE_DURATION;

                float adjustedOffsetY = previousOffsetY + (offsetYChange * progress);

                float upgradeLeft = x - (upgradeImage.getWidth() / 2);
                float upgradeTop = y - (upgradeImage.getHeight() / 2);
                canvas.drawBitmap(upgradeImage, upgradeLeft, upgradeTop, null);

                Paint transparentPaint = new Paint();
                transparentPaint.setAlpha(50);

                float left = x + offsetX - width / 2;
                float top = y + adjustedOffsetY - height / 2;

                if (isAnimating && towerAnimationFrames != null) {
                    drawAnimation(canvas, left, top, left + width, top + height, transparentPaint);
                } else if (towerImage != null) {
                    canvas.drawBitmap(towerImage, left, top, transparentPaint);
                }

                return;
            } else {
                isUpgrading = false;
            }
        }

        float left = x + offsetX - width / 2;
        float top = y + offsetY - height / 2;
        float right = x + offsetX + width / 2;
        float bottom = y + offsetY + height / 2;

        if (isAnimating && towerAnimationFrames != null) {
            drawAnimation(canvas, left, top, right, bottom, null);
        } else if (towerImage != null) {
            canvas.drawBitmap(towerImage, left, top, null);
        }
    }

    public boolean isTouched(float touchX, float touchY) {
        float left = x + offsetX - width / 2;
        float right = x + offsetX + width / 2;
        float top = y + offsetY - height / 2;
        float bottom = y + offsetY + height / 2;

        return touchX >= left && touchX <= right && touchY >= top && touchY <= bottom;
    }

    private void drawAnimation(Canvas canvas, float left, float top, float right, float bottom, Paint paint) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastFrameTime >= FRAME_DURATION) {
            currentFrame = (currentFrame + 1) % towerAnimationFrames.length;
            lastFrameTime = currentTime;
        }

        Bitmap currentFrameBitmap = towerAnimationFrames[currentFrame];

        float centerX = (left + right) / 2;
        float centerY = (top + bottom) / 2;

        float frameLeft = centerX - (currentFrameBitmap.getWidth() / 2);
        float frameTop = centerY - (currentFrameBitmap.getHeight() / 2);

        canvas.drawBitmap(currentFrameBitmap, frameLeft, frameTop, paint);
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