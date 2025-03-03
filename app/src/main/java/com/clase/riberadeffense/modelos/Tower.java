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
    // Declaramos todas las variables que vamos a utilizar en el desarrollo de las clase tower
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

    /**
     * @param damage
     * @param id
     * @param y
     * @param x
     * @param attackSpeed
     * @param contexto
     * @param level
     * @param range
     * Constructor de la clase tower en donde incializo cada torre, además
     * de cargar la imagen de mejora de la misma y calcular el offset de la misma
     * basandome en su nivel y en su id de torre*/
    public Tower(int x, int y, int range, int level, int damage, int attackSpeed, int id, Context contexto) {
        this.x = x;
        this.y = y;
        this.range = range;
        this.level = level;
        this.damage = damage;
        this.attackSpeed = attackSpeed;
        this.id = id;
        this.contexto = contexto;

        // Comprobamos si el nivel es mayor que 0, de serlo así ajustamos el offset de y de la torres basandonos
        // en sus ids
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

        // Comprobamos si el nivel que tenemos coincide con un nivel que tiene o no animaciones
        // para cargar un recurso estático o una animación
        if (level == 1) {
            loadTowerImage(R.drawable.tower11);
        } else if (level == 2 || level == 3) {
            loadTowerAnimation(level);
        } else {
            loadTowerImage(R.drawable.tower0);
        }

        // Cargamos en una variable el recurso de la imagen de mejora
        Bitmap originalUpgrade = BitmapFactory.decodeResource(contexto.getResources(), R.drawable.mejora);
        // Comprobamos que no sea nulo
        if (originalUpgrade != null) {
            /// Obtenemos en una variable el width de la imagen
            int upgradeWidth = originalUpgrade.getWidth();
            // Obtenemos en una variable el height de la imagen
            int upgradeHeight = originalUpgrade.getHeight();
            // Establecemos el retio por el que vamos a reducir la imagen
            float scaleRatio = 0.135f;
            // Guardamos la imagen escalada tanto en ancha como en alta
            upgradeImage = Bitmap.createScaledBitmap(originalUpgrade,
                    (int) (upgradeWidth * scaleRatio),
                    (int) (upgradeHeight * scaleRatio),
                    true);
        }
    }

    public void startUpgrade() {
        // Establecemos la variable booleana como true
        isUpgrading = true;
        // Guardamos en el tiempo de empiece de mejora el tiempo actual
        upgradeStartTime = System.currentTimeMillis();

        // Guardamos el offset de y que teniamos
        previousOffsetY = offsetY;

        // Ajustamos el nuevo offset de y de la torre basandonos en su nivel + 1
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

        // Cargamos el offset normal menos la diferentcia
        offsetYChange = offsetY - previousOffsetY;

        // Comprobamos si el nivel al que vamos a pasar coincide con un nivel que tiene o no animaciones
        // para cargar un recurso estático o una animación
        if (level + 1 == 1) {
            loadTowerImage(R.drawable.tower11);
        } else if (level + 1 == 2 || level + 1 == 3) {
            loadTowerAnimation(level + 1);
        } else {
            loadTowerImage(R.drawable.tower0);
        }
    }

    /**
     * @param resource
     * Método en el que le pasamos el recurso de la torre
     * esto sera para las torres de nivel 0 y nivel 1 y escalamos
     * los bitmaps manteniendo sus proporcione para que todos
     * los recursos tengan las mismas medidas*/
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

    /**
     * @param level
     * Método en el que le pasamos el nivel de la torre
     * y dependiendo de si es uno u es otro cargamos una animaciones
     * u otras posteriormente nos disponemos en el primer frame
     *  establecemos como que se esta ejecutando la animación */
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

    /**
     * @return
     * @param resource
     * Método en el que le pasamos recursos de drawable
     * y lo que hacemos es escalarlos manteniendo
     * sus proporciones y devolvemos el bitmap*/
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
        // Comprobamos si la torre se esta mejorando
        if (isUpgrading) { // Si es así
            // La torre no puede disparar
            return false;
        }

        // Obtenemos el tiempo actual y lo guardamos en una variable
        long currentTime = System.currentTimeMillis();
        // Retornamos si el tiempo actual, menos el ultimo tiro es mayor o igual tiempo de ataque
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
        // Actualizamos el nivel
        level++;
        // Aumentamos el daño
        damage += 25;
        // Reducimos el tiempo de espera entre ataques
        attackSpeed = Math.max(10, (int) (attackSpeed * 0.9));
        // Marcamos la torre como desbloqueada
        isUnlocked = true;
        // Comprobamos a que nivel pasamos
        if (level == 1) { // Si pasamo a nivel 1
            // Aumentamos lo siguiente
            range += 170;
        } else { // Y si tenemos otro nivel
            // Aumentamos solo en 100 el rango
            range += 100;
        }

        // Guardamos el offset de y anterior
        float previousOffsetY = offsetY;

        // Comprobamos si el nivel es mayor que 0 y ajustamos basandonos en el id de la torre
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

        // Actualizamos la posición de la y
        y += (previousOffsetY - offsetY);

        // Evaluamos si la torre es de nivel 0, 1, 2 o 3 y cargamos de una forma u otra la misma
        if (level == 1) {
            loadTowerImage(R.drawable.tower11);
        } else if (level == 2 || level == 3) {
            loadTowerAnimation(level);
        } else {
            loadTowerImage(R.drawable.tower0);
        }

        // Inicializamos la base de datos
        DatabaseHelper dbHelper = new DatabaseHelper(contexto);
        // Guardamos la torre
        dbHelper.saveTower(this);
    }

    public void draw(Canvas canvas, Paint paint) {
        // En caso de que la torre se este actualizando
        if (isUpgrading) {
            // Obtenemos el tiempo actual y lo guardamos en una variable
            long currentTime = System.currentTimeMillis();
            // Comprobamos si sigue siendo este menos cuando empezamos menor que el tiempo de duración de la mejora
            if (currentTime - upgradeStartTime < UPGRADE_DURATION) { // Si es así
                // Utilizamos una variable para calcular el progreso de la mejora
                float progress = (float) (currentTime - upgradeStartTime) / UPGRADE_DURATION;

                // Procedemos a ajustar el offset de la y
                float adjustedOffsetY = previousOffsetY + (offsetYChange * progress);

                // Actualizamos la x de la torre y lo guardamos en una variable
                float upgradeLeft = x - (upgradeImage.getWidth() / 2);
                // Actualizamos la y de la torre y lo guardamos en una variable
                float upgradeTop = y - (upgradeImage.getHeight() / 2);
                // Pintamos con canvas la imagen del muñequito mientras se mejora la torre
                canvas.drawBitmap(upgradeImage, upgradeLeft, upgradeTop, null);

                // Generemos un paint para pintar la torre mientras se carga
                Paint transparentPaint = new Paint();
                // Le establecemos que sea casi transparente
                transparentPaint.setAlpha(30);

                // Cargamos la variable left
                float left = x + offsetX - width / 2;
                // Ajustamos la y
                float top = y + adjustedOffsetY - height / 2;

                // Comprobamos si la torre tiene una animacion ejecutandose
                if (isAnimating && towerAnimationFrames != null) { // De ser así
                    // La pintamos
                    drawAnimation(canvas, left, top, left + width, top + height, transparentPaint);
                } else if (towerImage != null) { // Sino y el recurso de la imagen tiene algo
                    // La pintamos
                    canvas.drawBitmap(towerImage, left, top, transparentPaint);
                }

                // Retornamos
                return;
            } else { // Si no es así
                // Establecemos la variable como falsa
                isUpgrading = false;
            }
        }

        // Una vez terminada toda la mejora obtenemos todas las posiciones de la torre
        float left = x + offsetX - width / 2;
        float top = y + offsetY - height / 2;
        float right = x + offsetX + width / 2;
        float bottom = y + offsetY + height / 2;

        // Comprobamos si tiene una animacion ejecutando
        if (isAnimating && towerAnimationFrames != null) { // De ser así
            // La pintamos
            drawAnimation(canvas, left, top, right, bottom, null);
        } else if (towerImage != null) { // De no serlo y el recurso mencionado no sea nulo
            // La pintamos
            canvas.drawBitmap(towerImage, left, top, null);
        }
    }

    /**
     * @param touchX
     * @param touchY
     * Método en el que le pasamos las coordenadas de donde
     * ha tocado el usuario y comprobamos si la torre ha sido
     * tocada o no*/
    public boolean isTouched(float touchX, float touchY) {
        float left = x + offsetX - width / 2;
        float right = x + offsetX + width / 2;
        float top = y + offsetY - height / 2;
        float bottom = y + offsetY + height / 2;

        return touchX >= left && touchX <= right && touchY >= top && touchY <= bottom;
    }

    /**
     * @param bottom
     * @param canvas
     * @param left
     * @param paint
     * @param right
     * @param top
     * Método en el que le pasamos los valores donde tiene que dibujar
     * el paint y el canvas para dibujar la animacion*/
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

    /**
     * @param enemy
     * Método en el que comprobamos
     * gracias a la distancia de un enemigo y las coordenadas
     * de la torre si ese enemigo esta en el rango de la torre o
     * no*/
    public boolean isEnemyInRange(Enemy enemy) {
        double distance = Math.hypot(enemy.getX() - getX(), enemy.getY() - getY());
        return distance <= getRange();
    }
}