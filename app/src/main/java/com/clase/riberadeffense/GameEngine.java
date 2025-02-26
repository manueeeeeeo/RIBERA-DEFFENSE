package com.clase.riberadeffense;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.clase.riberadeffense.modelos.BasicEnemy;
import com.clase.riberadeffense.modelos.BossEnemy;
import com.clase.riberadeffense.modelos.Enemy;
import com.clase.riberadeffense.modelos.Projectile;
import com.clase.riberadeffense.modelos.Tower;
import com.clase.riberadeffense.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameEngine extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;
    private List<Tower> towers;
    private DatabaseHelper databaseHelper;
    private Handler handler;
    private List<Enemy> enemies = new ArrayList<>();
    private List<Projectile> projectiles = new ArrayList<>();
    private long lastClickTime = 0;
    private static final long LONG_CLICK_THRESHOLD = 1000;

    private long lastSpawnTime = 0;
    private static final long SPAWN_INTERVAL = 2000;
    private int enemiesToSpawn = 5;
    private int spawnedEnemies = 0;

    private static final long BOSS_SPAWN_DELAY = 2500;
    private long bossSpawnTimer = 0;
    private boolean isBossWave = false;
    private static final int ENEMIES_PER_WAVE = 15;
    private static final int TOTAL_WAVES = 5;
    private int currentWave = 0;

    private Bitmap fondo;
    private Bitmap nube;
    private int nubeX;
    private Rect rectFondo;
    private int screenWidth;
    private int screenHeight;

    private int lives = 5;
    private Bitmap[] lifeImages;
    private int currentLifeImageIndex = 0;

    private long lastBasicEnemySpawnTime = 0;
    private long bossSpawnTime = 0;

    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private final int FRAME_DURATION = 125;
    private Bitmap[] coinFrames;

    Bitmap[] upAnimationBasic = new Bitmap[]{
            BitmapFactory.decodeResource(getResources(), R.drawable.weaku1),
            BitmapFactory.decodeResource(getResources(), R.drawable.weaku2),
            BitmapFactory.decodeResource(getResources(), R.drawable.weaku3),
            BitmapFactory.decodeResource(getResources(), R.drawable.weaku4),
            BitmapFactory.decodeResource(getResources(), R.drawable.weaku5),
            BitmapFactory.decodeResource(getResources(), R.drawable.weaku6)
    };
    Bitmap[] downAnimationBasic = new Bitmap[]{
            BitmapFactory.decodeResource(getResources(), R.drawable.weakd1),
            BitmapFactory.decodeResource(getResources(), R.drawable.weakd2),
            BitmapFactory.decodeResource(getResources(), R.drawable.weakd3),
            BitmapFactory.decodeResource(getResources(), R.drawable.weakd4),
            BitmapFactory.decodeResource(getResources(), R.drawable.weakd5),
            BitmapFactory.decodeResource(getResources(), R.drawable.weakd6)
    };
    Bitmap[] leftAnimationBasic = new Bitmap[]{
            BitmapFactory.decodeResource(getResources(), R.drawable.weaki1),
            BitmapFactory.decodeResource(getResources(), R.drawable.weaki2),
            BitmapFactory.decodeResource(getResources(), R.drawable.weaki3),
            BitmapFactory.decodeResource(getResources(), R.drawable.weaki4),
            BitmapFactory.decodeResource(getResources(), R.drawable.weaki5),
            BitmapFactory.decodeResource(getResources(), R.drawable.weaki6)
    };

    Bitmap[] upAnimationBoss = new Bitmap[]{
            BitmapFactory.decodeResource(getResources(), R.drawable.strongu1),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongu2),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongu3),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongu4),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongu5),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongu6)
    };
    Bitmap[] downAnimationBoss = new Bitmap[]{
            BitmapFactory.decodeResource(getResources(), R.drawable.strongd1),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongd2),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongd3),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongd4),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongd5),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongd6)
    };
    Bitmap[] leftAnimationBoss = new Bitmap[]{
            BitmapFactory.decodeResource(getResources(), R.drawable.strongi1),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongi2),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongi3),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongi4),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongi5),
            BitmapFactory.decodeResource(getResources(), R.drawable.strongi6)
    };

    public GameEngine(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);

        coinFrames = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.moneda1),
                BitmapFactory.decodeResource(getResources(), R.drawable.moneda2),
                BitmapFactory.decodeResource(getResources(), R.drawable.moneda3),
                BitmapFactory.decodeResource(getResources(), R.drawable.moneda4),
                BitmapFactory.decodeResource(getResources(), R.drawable.moneda5),
                BitmapFactory.decodeResource(getResources(), R.drawable.moneda6)
        };

        lifeImages = new Bitmap[]{
                BitmapFactory.decodeResource(getResources(), R.drawable.v55),
                BitmapFactory.decodeResource(getResources(), R.drawable.v45),
                BitmapFactory.decodeResource(getResources(), R.drawable.v35),
                BitmapFactory.decodeResource(getResources(), R.drawable.v25),
                BitmapFactory.decodeResource(getResources(), R.drawable.v15),
                BitmapFactory.decodeResource(getResources(), R.drawable.v05)
        };
        try {
            databaseHelper = new DatabaseHelper(context);
            handler = new Handler();

            fondo = BitmapFactory.decodeResource(getResources(), R.drawable.fondo1);
            nube = BitmapFactory.decodeResource(getResources(), R.drawable.nube);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            thread = new MainThread(holder, this);
            thread.setRunning(true);
            thread.start();
            towers = databaseHelper.getAllTowers(getContext());

            for (Tower tower : towers) {
                if(tower.getLevel() == 2 && tower.getLevel() == 3){
                    tower.startAnimation();
                }
            }

            lastSpawnTime = System.currentTimeMillis();
            enemiesToSpawn = 5;
            spawnedEnemies = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (fondo != null) {
            rectFondo = new Rect(0, 0, width, height);
            screenWidth = width;
            screenHeight = height;
            nubeX = screenWidth;
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        Paint paint = new Paint();
        canvas.drawColor(Color.BLACK);

        if (fondo != null && rectFondo != null) {
            canvas.drawBitmap(fondo, null, rectFondo, null);
        }

        if (towers != null) {
            for (Tower tower : towers) {
                tower.draw(canvas, new Paint());
            }
        } else {
            paint.setColor(Color.RED);
            canvas.drawText("Error al cargar torres", 50, 500, paint);
        }

        for (Enemy enemy : enemies) {
            enemy.draw(canvas, paint);
        }

        for (Projectile projectile : projectiles) {
            projectile.draw(canvas, paint);
        }

        drawUI(canvas, paint);
        drawNube(canvas);
    }

    private void drawNube(Canvas canvas) {
        if (nube != null) {
            Paint paint = new Paint();
            paint.setAlpha(128);
            canvas.drawBitmap(nube, nubeX, 50, paint);
            nubeX -= 2;
            if (nubeX + nube.getWidth() < 0) {
                nubeX = screenWidth;
            }
        }
    }

    private void drawUI(Canvas canvas, Paint paint) {
        int money = databaseHelper.getMoney();
        String moneyText = ": " + money;
        paint.setTextSize(50);
        paint.setColor(Color.WHITE);
        paint.setTextAlign(Paint.Align.LEFT);
        canvas.drawText(moneyText, 150, 100, paint);

        if (coinFrames != null && coinFrames.length > 0) {
            Bitmap currentCoinFrame = coinFrames[currentFrame];
            canvas.drawBitmap(currentCoinFrame, 50, 40, null);
        }

        String waveText = (currentWave + 1) + "/5";
        paint.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText(waveText, getWidth() - 50, 100, paint);

        Bitmap skullImage = BitmapFactory.decodeResource(getResources(), R.drawable.skull);

        Bitmap scaledSkullImage = Bitmap.createScaledBitmap(skullImage, 80, 80, true);

        int skullX = getWidth() - 220;
        int skullY = 80 - (80 / 2);

        canvas.drawBitmap(scaledSkullImage, skullX, skullY, null);

        if (lifeImages != null && currentLifeImageIndex < lifeImages.length) {
            Bitmap lifeImage = lifeImages[currentLifeImageIndex];
            int scaledWidth = lifeImage.getWidth() * 2;
            int scaledHeight = lifeImage.getHeight() * 2;
            Bitmap scaledLifeImage = Bitmap.createScaledBitmap(lifeImage, scaledWidth, scaledHeight, true);
            int lifeImageX = 50;
            int lifeImageY = 150;
            canvas.drawBitmap(scaledLifeImage, lifeImageX, lifeImageY, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        for (Tower tower : towers) {
            if (tower.isTouched(event.getX(), event.getY())) {
                long currentTime = System.currentTimeMillis();

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastClickTime = currentTime;
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (currentTime - lastClickTime < LONG_CLICK_THRESHOLD) {
                            showTowerStats(tower);
                        } else {
                            showUpgradeDialog(tower);
                        }
                        return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    private void showTowerStats(Tower tower) {
        String stats = "Nivel: " + tower.getLevel() + "\n" +
                "Daño: " + tower.getDamage() + "\n" +
                "Rango: " + tower.getRange();
        new AlertDialog.Builder(getContext())
                .setTitle("Estadísticas de la Torre")
                .setMessage(stats)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void showUpgradeDialog(Tower tower) {
        int upgradeCost = (tower.getLevel() + 1) * 60;
        String newStats;
        if(tower.getLevel()+1==1){
            newStats = "Nuevo Nivel: " + (tower.getLevel() + 1) + "\n" +
                    "Nuevo Daño: " + (tower.getDamage() + 25) + "\n" +
                    "Nuevo Rango: " + (tower.getRange() + 100);
        }else{
            newStats = "Nuevo Nivel: " + (tower.getLevel() + 1) + "\n" +
                    "Nuevo Daño: " + (tower.getDamage() + 25) + "\n" +
                    "Nuevo Rango: " + (tower.getRange() + 55);
        }

        new AlertDialog.Builder(getContext())
                .setTitle("Mejorar Torre")
                .setMessage("Costo: " + upgradeCost + " monedas\n\n" +
                        "Mejoras:\n" + newStats)
                .setPositiveButton("Mejorar", (dialog, which) -> {
                    int money = databaseHelper.getMoney();
                    if (money >= upgradeCost) {
                        databaseHelper.updateMoney(money - upgradeCost);
                        tower.upgradeTower();
                        //databaseHelper.saveTower(tower);
                        Handler uiHandler = new Handler(getContext().getMainLooper());
                        uiHandler.post(() -> {
                            Canvas canvas = getHolder().lockCanvas();
                            if (canvas != null) {
                                towers = databaseHelper.getAllTowers(getContext());
                                drawUI(canvas, new Paint());
                                getHolder().unlockCanvasAndPost(canvas);
                            }
                        });
                    } else {
                        new AlertDialog.Builder(getContext())
                                .setTitle("Error")
                                .setMessage("No tienes suficiente dinero")
                                .setPositiveButton("Cerrar", null)
                                .show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    public void update() {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastFrameTime >= FRAME_DURATION) {
            currentFrame = (currentFrame + 1) % coinFrames.length; // Cambiar al siguiente frame
            lastFrameTime = currentTime;
        }

        if (!isBossWave && spawnedEnemies < ENEMIES_PER_WAVE) {
            if (currentTime - lastSpawnTime >= SPAWN_INTERVAL) {
                ArrayList<int[]> path = crearCaminoEnemigo();
                enemies.add(new BasicEnemy(path, 100, 2, 10, upAnimationBasic, downAnimationBasic, leftAnimationBasic));
                lastSpawnTime = currentTime;
                spawnedEnemies++;
                lastBasicEnemySpawnTime = currentTime;
            }
        }

        if (!isBossWave && spawnedEnemies == ENEMIES_PER_WAVE && currentTime - lastBasicEnemySpawnTime >= BOSS_SPAWN_DELAY) {
            ArrayList<int[]> path = crearCaminoEnemigo();
            enemies.add(new BossEnemy(path, 500, 2, 50, upAnimationBoss, downAnimationBoss, leftAnimationBoss, currentWave));
            isBossWave = true;
            spawnedEnemies = 0;
            bossSpawnTime = currentTime;
            currentWave++;
            if (currentWave > TOTAL_WAVES) {
                showToast("¡Juego acabado!");
            }
        }

        if (isBossWave && currentTime - bossSpawnTime >= 3000) {
            isBossWave = false;
            lastSpawnTime = currentTime;
        }

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update();

            if (enemy.getCurrentWaypointIndex() >= enemy.getWaypoints().size()) {
                enemyIterator.remove();
                showToast("¡Enemigo ha llegado al final!");

                if (lives > 0) {
                    lives--;
                    currentLifeImageIndex = Math.min(currentLifeImageIndex + 1, lifeImages.length - 1);
                    if (lives == 0) {
                        showToast("¡Has perdido!");
                    }
                }
            }
        }

        Iterator<Projectile> projectileIterator = projectiles.iterator();
        while (projectileIterator.hasNext()) {
            Projectile projectile = projectileIterator.next();
            projectile.update();

            if (!projectile.isActive()) {
                projectileIterator.remove();
            }
        }

        for (Tower tower : towers) {
            if (!tower.canShoot()) continue;

            Enemy target = null;

            for (Enemy enemy : enemies) {
                if (tower.isEnemyInRange(enemy)) {
                    target = enemy;
                    break;
                }
            }

            if (target != null) {
                boolean hasActiveProjectile = false;
                for (Projectile projectile : projectiles) {
                    if (projectile.getTarget() == target) {
                        hasActiveProjectile = true;
                        break;
                    }
                }

                if (!hasActiveProjectile) {
                    projectiles.add(new Projectile(tower.getX(), tower.getY(), target, tower.getDamage()));
                    tower.shoot();
                }
            }
        }

        enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            if (!enemy.isAlive()) {
                enemyIterator.remove();
                databaseHelper.updateMoney(databaseHelper.getMoney() + 3);
            }
        }
    }

    private void showToast(final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show());
    }

    private ArrayList<int[]> crearCaminoEnemigo() {
        ArrayList<int[]> path = new ArrayList<>();
        path.add(new int[]{getWidth() - 100, getHeight() / 2});
        path.add(new int[]{getWidth() - 300, getHeight() / 2});
        path.add(new int[]{getWidth() - 300, getHeight() / 2 + 200});
        path.add(new int[]{getWidth() - 600, getHeight() / 2 + 200});
        path.add(new int[]{getWidth() - 600, getHeight() / 2 - 150});
        path.add(new int[]{getWidth() - 900, getHeight() / 2 - 150});
        path.add(new int[]{getWidth() - 900, getHeight() / 2 + 100});
        path.add(new int[]{getWidth() - 1100, getHeight() / 2 + 100});
        path.add(new int[]{getWidth() - 1100, getHeight() / 2 - 100});
        path.add(new int[]{getWidth() - 1400, getHeight() / 2 - 100});
        path.add(new int[]{getWidth() - 1400, getHeight() / 2});
        path.add(new int[]{100, getHeight() / 2});
        return path;
    }

    private class MainThread extends Thread {
        private SurfaceHolder surfaceHolder;
        private GameEngine gameEngine;
        private boolean running;

        public MainThread(SurfaceHolder surfaceHolder, GameEngine gameEngine) {
            super();
            this.surfaceHolder = surfaceHolder;
            this.gameEngine = gameEngine;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            Canvas canvas;
            while (running) {
                canvas = null;
                try {
                    canvas = this.surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        gameEngine.update(); // Actualizar el estado del juego
                        gameEngine.draw(canvas); // Dibujar el estado actual
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        try {
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}