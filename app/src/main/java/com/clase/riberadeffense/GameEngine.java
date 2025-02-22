package com.clase.riberadeffense;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.clase.riberadeffense.modelos.Tower;
import com.clase.riberadeffense.database.DatabaseHelper;

import java.util.List;

public class GameEngine extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;
    private List<Tower> towers;
    private DatabaseHelper databaseHelper;
    private int wave = 1;
    private Handler handler;
    private long lastClickTime = 0;
    private static final long LONG_CLICK_THRESHOLD = 1000;

    public GameEngine(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);
        try {
            databaseHelper = new DatabaseHelper(context);
            handler = new Handler();
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
                tower.startAnimation();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
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

        if (towers != null) {
            for (Tower tower : towers) {
                tower.draw(canvas, paint);
            }
        } else {
            paint.setColor(Color.RED);
            canvas.drawText("Error al cargar torres", 50, 500, paint);
        }

        drawUI(canvas, paint);
    }

    private void drawUI(Canvas canvas, Paint paint) {
        int money = databaseHelper.getMoney();
        String moneyText = "Monedas: " + money;
        paint.setTextSize(50);
        paint.setColor(Color.WHITE);
        canvas.drawText(moneyText, 50, 100, paint);

        String waveText = "Oleada: " + wave + "/5";
        canvas.drawText(waveText, 50, 170, paint);
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
                "Daño: " + tower.getDamage() + "\n" +
                "Rango: " + tower.getRange();
        new AlertDialog.Builder(getContext())
                .setTitle("Estadísticas de la Torre")
                .setMessage(stats)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private void showUpgradeDialog(Tower tower) {
        int upgradeCost = (tower.getLevel() + 1) * 60;
        String newStats = "Nuevo Nivel: " + (tower.getLevel() + 1) + "\n" +
                "Nuevo Daño: " + (tower.getDamage() + 10) + "\n" +
                "Nuevo Rango: " + (tower.getRange() + 5);

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
                long startTime = System.nanoTime();
                canvas = null;

                try {
                    canvas = surfaceHolder.lockCanvas();
                    synchronized (surfaceHolder) {
                        gameEngine.update();
                        gameEngine.draw(canvas);
                    }
                } finally {
                    if (canvas != null) {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }

                long elapsedTime = System.nanoTime() - startTime;
                long sleepTime = (1000 / 60) - (elapsedTime / 1000000);

                if (sleepTime > 0) {
                    try {
                        sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}