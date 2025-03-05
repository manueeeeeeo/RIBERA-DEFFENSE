package com.clase.riberadeffense;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameEngine extends SurfaceView implements SurfaceHolder.Callback {
    // Creamos todas las variables que vamos a usar en esta clase
    private BucleJuego thread = null;
    private List<Tower> towers = new ArrayList<>();
    private DatabaseHelper databaseHelper = null;
    private Handler handler = null;
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

    private Bitmap fondo = null;
    private Bitmap nube = null;
    private int nubeX = 0;
    private int screenWidth = 0;
    private int screenHeight = 0;

    private int lives = 5;
    private Bitmap[] lifeImages;
    private int currentLifeImageIndex = 0;

    private Handler uiHandler = null;

    private long lastBasicEnemySpawnTime = 0;
    private long bossSpawnTime = 0;

    private int currentFrame = 0;
    private long lastFrameTime = 0;
    private final int FRAME_DURATION = 125;
    private Bitmap[] coinFrames;

    private Bitmap capaBackground = null;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

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

    private volatile boolean resourcesLoaded = false;

    /**
     * @param context
     * Constructor en donde inicializamos el gamenegine
     * con su contexto, cargamos varios datos sobre el juego, además
     * de inicializar la base de datos de sqlite y cargar todos
     * los recursos necesarios para el videojuego*/
    public GameEngine(Context context) {
        super(context);
        getHolder().addCallback(this);
        setFocusable(true);

        uiHandler = new Handler(Looper.getMainLooper());

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

            fondo = BitmapFactory.decodeResource(getResources(), R.drawable.fondojuego);
            nube = BitmapFactory.decodeResource(getResources(), R.drawable.nube);

            if (fondo == null || nube == null) {
                throw new RuntimeException("Error al cargar recursos gráficos.");
            }

            resourcesLoaded = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            if (thread == null || !thread.isAlive()) {
                thread = new BucleJuego(holder, this);
                thread.setRunning(true);
                thread.start();
            }

            towers = databaseHelper.getAllTowers(getContext());

            for (Tower tower : towers) {
                if (tower.getLevel() >= 2) {
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
            capaBackground = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas cacheCanvas = new Canvas(capaBackground);

            cacheCanvas.drawBitmap(fondo, null, new Rect(0, 0, width, height), null);
        }

        screenWidth = width;
        screenHeight = height;
        nubeX = screenWidth;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                if (thread != null) {
                    thread.setRunning(false);
                    thread.join();
                }
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        thread = null;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // En caso de que no se hayan cargado los recursos
        if (!resourcesLoaded) {
            // Retornamos sin hacer nada
            return;
        }

        if (canvas == null) {
            return;
        }

        // Creo un nuevo paint
        Paint paint = new Paint();
        // Establezco el color del dibujo del fondo
        canvas.drawColor(Color.BLACK);

        // En caso de que la capa del background no sea nulo
        if (capaBackground != null) {
            // Dibujamos el fondo
            canvas.drawBitmap(capaBackground, 0, 0, null);
        }

        // Comprobamos que la lista de torres no sea nula
        if (towers != null) {
            // Dibujamos todas las torres que tenemos en la lista
            for (Tower tower : towers) {
                tower.draw(canvas, new Paint());
            }
        } else { // En caso de que no tengamos torres
            // Establecemos el color del texto del paint
            paint.setColor(Color.RED);
            // Dibujamos un texto indicando que no hemos podido cargas las torres
            canvas.drawText("Error al cargar torres", 50, 500, paint);
        }

        // Vamos dibujando todos los enemigos que tenemos en la lista
        for (Enemy enemy : enemies) {
            enemy.draw(canvas, paint);
        }

        // Dibujamos todos los projectiles que tenemos en la lista
        for (Projectile projectile : projectiles) {
            projectile.draw(canvas, paint);
        }

        // Llamo al método para dibujar la ui del juego
        drawUI(canvas, paint);
        // Llamo al método para dibujar la nube del juego
        drawNube(canvas);
    }

    private void drawNube(Canvas canvas) {
        // En caso de que la nube tenga algo
        if (nube != null) {
            // Declaro un nuevo paint
            Paint paint = new Paint();
            // Le establezco que sea semitransparente
            paint.setAlpha(128);
            // Dibujamos la nube
            canvas.drawBitmap(nube, nubeX, 50, paint);
            // Actualizamos la x de la nube de 2 en 2 de posiciones
            nubeX -= 2;
            // En caso de que la x de la nube más el ancho de la nube sea menor que 0
            if (nubeX + nube.getWidth() < 0) {
                // La x de la nube es igual al ancho de la pantalla
                nubeX = screenWidth;
            }
        }
    }

    private void drawUI(Canvas canvas, Paint paint) {
        // Guardo en una variable la cantidad de dinero que tenemos en la base de datos
        int money = databaseHelper.getMoney();
        // Establezco el texto donde vemos el contador de monedas
        String moneyText = ": " + money;
        // Establecemos el tamaño del texto
        paint.setTextSize(50);
        // Establecemos el color del texto
        paint.setColor(Color.WHITE);
        // Establecemos el centrado del texto
        paint.setTextAlign(Paint.Align.LEFT);
        // Dibujamos el cuadro de texto
        canvas.drawText(moneyText, 150, 100, paint);

        // En caso de que los frames del coin no sea nulo
        if (coinFrames != null && coinFrames.length > 0) {
            // Genero un bitmap con el frame de la moneda actua
            Bitmap currentCoinFrame = coinFrames[currentFrame];
            // Dibujamos la moneda actual
            canvas.drawBitmap(currentCoinFrame, 50, 40, null);
        }

        // Establezco el texto donde vamos a ver el número de oleadas
        String waveText = (currentWave) + "/5";
        // Establezco el centrado del texto
        paint.setTextAlign(Paint.Align.RIGHT);
        // Dibujamos el cuadro de texto
        canvas.drawText(waveText, getWidth() - 50, 100, paint);

        // Obtengo en un bitmap el recurso de drawable donde tenemos la calavera
        Bitmap skullImage = BitmapFactory.decodeResource(getResources(), R.drawable.skull);

        // Escalamos la imagen de la calavera
        Bitmap scaledSkullImage = Bitmap.createScaledBitmap(skullImage, 80, 80, true);

        // Establecemos la x de la calavera
        int skullX = getWidth() - 220;
        // Establecemos la y de la calavera
        int skullY = 80 - (80 / 2);

        // Dibujamos la calavera
        canvas.drawBitmap(scaledSkullImage, skullX, skullY, null);

        // En caso de que las imagenes de la vida no sea nula y sea menor al tamaño total de la lista
        if (lifeImages != null && currentLifeImageIndex < lifeImages.length) {
            // Obtengo el frame de la vida que nos toca ahora
            Bitmap lifeImage = lifeImages[currentLifeImageIndex];
            // Escalamos el ancho de la bitmap
            int scaledWidth = lifeImage.getWidth() * 2;
            // Escalamos el alto de la bitmap
            int scaledHeight = lifeImage.getHeight() * 2;
            // Escalamos la imagen
            Bitmap scaledLifeImage = Bitmap.createScaledBitmap(lifeImage, scaledWidth, scaledHeight, true);
            // Establecemos la x de la imagen
            int lifeImageX = 50;
            // Establecemos la y de la imagen
            int lifeImageY = 150;
            // Dibujo la imagen de las vidas
            canvas.drawBitmap(scaledLifeImage, lifeImageX, lifeImageY, null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Dentro de las torres
        for (Tower tower : towers) {
            // Comprobamos si la torre ha sido tocada
            if (tower.isTouched(event.getX(), event.getY())) {
                // Guardo en una variable la longitud del tiempo que hemos esta pulsando
                long currentTime = System.currentTimeMillis();

                // Con un switch establecemos que tipo de acción hemos hecho
                switch (event.getAction()) {
                    // Cuando hacemos el action down
                    case MotionEvent.ACTION_DOWN:
                        // Guardamos en una variable el ultimo tiempo que hicimos la acción
                        lastClickTime = currentTime;
                        return true;

                    // Cuando hacemos el action up
                    case MotionEvent.ACTION_UP:
                        // Comprobamos si el tiempo actual menos el tiempo del ultimo action down es menor que el tiempo de largo click
                        if (currentTime - lastClickTime < LONG_CLICK_THRESHOLD) { // Si es así
                            // Llamamos al método para mostrar las estadisticas de la torre
                            showTowerStats(tower);
                        } else { // Por otro lado
                            // Llamamos la método para mostrar el dialogo de mejora
                            showUpgradeDialog(tower);
                        }
                        return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }

    /**
     * @param tower
     * Método al que le pasamos el objeto de la torre
     * que hemos tocado y mostramos sus estádisticas
     * principales*/
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

    /**
     * @param tower
     * Método en el que le pasamos el objeto
     * de la torre que hemos tocado, comprobamos el nivel que tiene,
     * el nivel al que va a pasar y establecemos un string para mostrar
     * al usuario las posibles mejoras que se van a generar en caso
     * de que el acepte actualizar la torre, además tambien llevamos
     * el control de las monedas y subdialogos internos para
     * mostrar en caso de que no tenga suficiente dinero o ya haya
     * llegado al maximo nivel de torre*/
    private void showUpgradeDialog(Tower tower) {
        int upgradeCost = (tower.getLevel() + 1) * 75;
        String newStats;
        if(tower.getLevel()+1==1){
            newStats = "Nuevo Nivel: " + (tower.getLevel() + 1) + "\n" +
                    "Nuevo Daño: " + (tower.getDamage() + 25) + "\n" +
                    "Nuevo Rango: " + (tower.getRange() + 170);
        }else{
            newStats = "Nuevo Nivel: " + (tower.getLevel() + 1) + "\n" +
                    "Nuevo Daño: " + (tower.getDamage() + 25) + "\n" +
                    "Nuevo Rango: " + (tower.getRange() + 100);
        }

        if(tower.getLevel()+1>3){
            new AlertDialog.Builder(getContext())
                    .setTitle("Limite de Nivel de Torre")
                    .setMessage("Está torre ha llegado a su nivel limite para este juego")
                    .setPositiveButton("Okey!!!", null)
                    .show();
        }else{
            new AlertDialog.Builder(getContext())
                    .setTitle("Mejorar Torre")
                    .setMessage("Costo: " + upgradeCost + " monedas\n\n" +
                            "Mejoras:\n" + newStats)
                    .setPositiveButton("Mejorar", (dialog, which) -> {
                        int money = databaseHelper.getMoney();
                        if (money >= upgradeCost) {
                            databaseHelper.updateMoney(money - upgradeCost);
                            tower.startUpgrade();
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.postDelayed(() -> {
                                tower.upgradeTower();

                                Canvas canvas = getHolder().lockCanvas();
                                if (canvas != null) {
                                    towers = databaseHelper.getAllTowers(getContext());
                                    drawUI(canvas, new Paint());
                                    getHolder().unlockCanvasAndPost(canvas);
                                }
                            }, 1550);
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
    }

    /**
     * Método al que llamamos cuando hemos ganado el
     * juego, utilizando hilos secundatios, inflando
     * la vista y dando funcionalidad a todos sus elementos*/
    public void mostrarGanado(){
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            View dialogoGanado = LayoutInflater.from(getContext()).inflate(R.layout.dialogo_ganado, null);
            AlertDialog.Builder eleccionDialogo = new AlertDialog.Builder(getContext());
            eleccionDialogo.setView(dialogoGanado);

            final AlertDialog dialogo = eleccionDialogo.create();
            dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogo.setCancelable(false);
            dialogo.show();

            Button btnReiniciar = dialogo.findViewById(R.id.buttonContinue);
            Button btnSalir = dialogo.findViewById(R.id.buttonExit);

            btnReiniciar.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    databaseHelper.resetDatabase();
                    reiniciarJuego();
                    dialogo.dismiss();
                }
            });

            btnSalir.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    volverAlInicio();
                }
            });
        });
    }

    /**
     * Método en donde paramos todo lo actual
     * para poder volver al inicio y liberar
     * recursos para que el juego siga yendo rapido
     * y fluido*/
    public void volverAlInicio() {
        if (thread != null) {
            thread.setRunning(false);
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = null;
        }

        if (fondo != null) {
            fondo.recycle();
            fondo = null;
        }
        if (capaBackground != null) {
            capaBackground.recycle();
            capaBackground = null;
        }

        if (towers != null) {
            towers.clear();
            towers = null;
        }
        if (enemies != null) {
            enemies.clear();
            enemies = null;
        }
        if (projectiles != null) {
            projectiles.clear();
            projectiles = null;
        }

        if (executor != null) {
            executor.shutdown();
        }

        Context context = getContext();
        if (context instanceof Activity) {
            Intent intent = new Intent(context, Inicio.class);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
    }

    /**
     * Método con el cerramos el game engine
     * actual y creamos otro, dando la sensación
     * de que el juego se ha reiniciado*/
    private void reiniciarJuego() {
        if (thread != null) {
            thread.setRunning(false);
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            thread = null;
        }

        if (fondo != null) {
            fondo.recycle();
            fondo = null;
        }
        if (capaBackground != null) {
            capaBackground.recycle();
            capaBackground = null;
        }

        if (towers != null) {
            towers.clear();
            towers = null;
        }
        if (enemies != null) {
            enemies.clear();
            enemies = null;
        }
        if (projectiles != null) {
            projectiles.clear();
            projectiles = null;
        }

        if (executor != null) {
            executor.shutdown();
        }

        Context context = getContext();
        if (context instanceof Activity) {
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
    }

    /**
     * Método al que llamamos cuando hemos perdido el
     * juego, utilizando hilos secundatios, inflando
     * la vista y dando funcionalidad a todos sus elementos*/
    private void mostrarPerdido() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            View dialogoPerdido = LayoutInflater.from(getContext()).inflate(R.layout.dialogo_perdido, null);
            AlertDialog.Builder eleccionDialogo = new AlertDialog.Builder(getContext());
            eleccionDialogo.setView(dialogoPerdido);

            final AlertDialog dialogo = eleccionDialogo.create();
            dialogo.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialogo.setCancelable(false);
            dialogo.show();

            Button btnReiniciar = dialogo.findViewById(R.id.buttonContinue);
            Button btnSalir = dialogo.findViewById(R.id.buttonExit);

            btnReiniciar.setOnClickListener(view -> {
                databaseHelper.resetDatabase();
                reiniciarJuego();
                dialogo.dismiss();
            });

            btnSalir.setOnClickListener(view -> volverAlInicio());
        });
    }

    private void updateMoneyInBackground(int reward) {
        // Utilizamos el executor para actualizar las monedas en la base de datos en un hilo por detras para no
        // sobrecargar el hilo principal
        executor.execute(() -> {
            try {
                databaseHelper.updateMoney(databaseHelper.getMoney() + reward);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void update() {
        // Obtenemos el tiempo actual
        long currentTime = System.currentTimeMillis();

        // Si el tiempo actual menos el ultimo tiempo de frame es mayor que la duración de un frame que hemos asignado
        if (currentTime - lastFrameTime >= FRAME_DURATION) {
            // Alternamos con el siguiente frame de la moneda
            currentFrame = (currentFrame + 1) % coinFrames.length;
            // Actualizamos el último frame de tiempo
            lastFrameTime = currentTime;
        }

        // Actualizamos los enemigos sincronizados
        executor.execute(() -> updateEnemies(currentTime));

        // Actualizamos los projectiles sincronizados
        executor.execute(() -> updateProjectiles());

        // Actualizamos lsa torres
        updateTowers();
    }

    private void updateEnemies(long currentTime) {
        // Sincronizamos los enemigos
        synchronized (enemies) {
            // Comprobamos las oleadas
            if (currentWave == 5) { // En caso de que sea la 5 oleada
                if (!isBossWave && spawnedEnemies < ENEMIES_PER_WAVE) {
                    // Aquí no hacemos nada porque no se deben generar nuevos enemigos
                }

                // Si la lista de enemigos esta vacia
                if (enemies.isEmpty()) {
                    // Si el hilo tiene algo
                    if (thread != null) {
                        // Le detenemos
                        thread.setRunning(false);
                    }
                    // Llamamos al método para mostrar el dialogo de ganado
                    uiHandler.post(this::mostrarGanado);
                }
            } else { // En caso de que sea una oleada diferente a la 5
                // Si no es el momento del boss y los enemigos spawneados son menores a los que tengo que spawnear
                if (!isBossWave && spawnedEnemies < ENEMIES_PER_WAVE) {
                    // Compruebo si el tiempo actual menos el ultimo spawn es mayor o igual al intervalo de spawn
                    if (currentTime - lastSpawnTime >= SPAWN_INTERVAL) {
                        // Obtengo un nuevo camino
                        ArrayList<int[]> path = crearCaminoEnemigo();
                        // Genero un nuevo enemigo básico y le agrego a la lista
                        enemies.add(new BasicEnemy(path, 100, 2, 10, upAnimationBasic, downAnimationBasic, leftAnimationBasic));
                        // Actualizo el tiempo del ultimo spawneado
                        lastSpawnTime = currentTime;
                        // Aumento el contador de enemigos spawneados
                        spawnedEnemies++;
                        // Establecezco la variable del tiempo de ultimo enemigo básico spawneado
                        lastBasicEnemySpawnTime = currentTime;
                    }
                }

                // Si no es oleada de boss y el contador de enemigos spawneados es igual a los que hay que spawnear
                // y el tiempo actual menos el tiempo del ultimo spawn de un enemigo basico es mayor o igual
                // al delay de spwan del boss
                if (!isBossWave && spawnedEnemies == ENEMIES_PER_WAVE && currentTime - lastBasicEnemySpawnTime >= BOSS_SPAWN_DELAY) {
                    // Creo un nuevo camino para el enemigo
                    ArrayList<int[]> path = crearCaminoEnemigo();
                    // Creo un nuevo enemigo boss y le agrego a la lista de enemigo
                    enemies.add(new BossEnemy(path, 500, 2, 50, upAnimationBoss, downAnimationBoss, leftAnimationBoss, currentWave));
                    // Establezco que es una oleada de boss
                    isBossWave = true;
                    // Reinicio el contador de spawn de enemigos
                    spawnedEnemies = 0;
                    // Establezco el ultimo tiempo de spawn del boss
                    bossSpawnTime = currentTime;
                    // Aumento en una la oleada
                    currentWave++;

                    // Si la ola actual es igual al total de olas y la lista de enemigos está vacia
                    if (currentWave == TOTAL_WAVES && enemies.isEmpty()) {
                        // Si el hilo tiene algo
                        if (thread != null) {
                            // Detengo el hilo
                            thread.setRunning(false);
                        }
                        // Llamamos al método para mostrar el dialogo de ganado
                        uiHandler.post(this::mostrarGanado);
                    }
                }
            }

            // Si es tiempo del boss y el tiempo actual menos el tiempo de spawn del boss es mayor o igual a 3 segundos
            if (isBossWave && currentTime - bossSpawnTime >= 3000) {
                // Desactivamos la oleada jefe
                isBossWave = false;
                // Y actualizamos el tiempo de ultimo spawneo
                lastSpawnTime = currentTime;
            }

            // Utilizo el iterator para manejar la lista de enemigos
            Iterator<Enemy> enemyIterator = enemies.iterator();
            // Mientras que el iterator pueda seguir avanzando
            while (enemyIterator.hasNext()) {
                // Generamos un enemigo con el iterator next
                Enemy enemy = enemyIterator.next();
                // Actualizo un enemigo
                enemy.update();

                // En caso de que el el punto de camino del enemigo sea mayor o igual al maximo del camino
                if (enemy.getCurrentWaypointIndex() >= enemy.getWaypoints().size()) {
                    // Eliminamos el iterator enemy
                    enemyIterator.remove();

                    // Creo una variable para administrar la recompensa por matar a un enemigo
                    int reward = 0;
                    // Si el enemigo es un enemigo básico
                    if (enemy instanceof BasicEnemy) {
                        // La recompensa es de 6 monedas
                        reward = 6;
                    } else if (enemy instanceof BossEnemy) { // Si es un enemigo boss
                        // La recompensa es de 20 monedas
                        reward = 20;
                    }

                    // Declaro una variabe final con la recompensa por matar al enemigo
                    final int finalReward = reward;
                    // Actualizo el número de monedas en la base de datos en segudo plano
                    updateMoneyInBackground(finalReward);

                    // Mientras que las vidas sean mayores de 0
                    if (lives > 0) {
                        // Decuento una vida
                        lives--;
                        // Actualizo el index de la animación de vidas del juego
                        currentLifeImageIndex = Math.min(currentLifeImageIndex + 1, lifeImages.length - 1);
                        // En caso de que no tengamos vidas
                        if (lives == 0) {
                            // Si el hilo tiene algo
                            if (thread != null) {
                                // Le detenemos
                                thread.setRunning(false);
                            }
                            // Llamamos al método para mostrar el dialogo de perdido
                            uiHandler.post(this::mostrarPerdido);
                        }
                    }
                }
            }
        }
    }


    private void updateProjectiles() {
        // Sincronizamos los projectiles
        synchronized (projectiles) {
            // Utilizamos un iterator para pasar de unos projectiles a otros
            Iterator<Projectile> projectileIterator = projectiles.iterator();
            // Mientras que se pueda seguir pasando al siguente projectil
            while (projectileIterator.hasNext()) {
                // Cremos el projectile cargandole con todos los datos necesarios
                Projectile projectile = projectileIterator.next();
                // Actualizamos el projectil
                projectile.update();

                // En caso de que el projectil no este activo
                if (!projectile.isActive()) {
                    // Le eliminamos de la lista
                    projectileIterator.remove();
                }
            }
        }
    }

    private void updateTowers() {
        // Sincronizamos las torres
        synchronized (towers) {
            // Con un bucle recorremos todas las torres
            for (Tower tower : towers) {
                // En caso de que no pueda disparar continuamos
                if (!tower.canShoot()) continue;

                // Creamos un enemigo nulo
                Enemy target = null;
                // Con un bucle de la lista de enemigos
                for (Enemy enemy : enemies) {
                    // Comprobamos si el enemigo esta en el rango de la torre
                    if (tower.isEnemyInRange(enemy)) {
                        // Guardamos el enemigo en el target de la torre
                        target = enemy;
                        break;
                    }
                }

                // En caso de que el target no sea nulo
                if (target != null) {
                    // Creamos una variable para saber si un projectile ha sido activado inicialziada en false
                    boolean hasActiveProjectile = false;
                    // Con un bucle de la lista de projectiles
                    for (Projectile projectile : projectiles) {
                        // Comprobamos si el target del projectil es igual al target de la torre
                        if (projectile.getTarget() == target) {
                            // Si es así establecemos las variable como true
                            hasActiveProjectile = true;
                            break;
                        }
                    }

                    // En caso de que la variable sea falsa
                    if (!hasActiveProjectile) {
                        // Creamos y agregamos a la lista de projectiles uno nuevo
                        projectiles.add(new Projectile(tower.getX(), tower.getY(), target, tower.getDamage()));
                        // Y llamamos la método de la torre para disparar
                        tower.shoot();
                    }
                }
            }

            // Utilizamos u itreator para iterar sobre los enemigos
            Iterator<Enemy> enemyIterator = enemies.iterator();
            // Mientras que el iterator pueda seguir moviendose al siguiente
            while (enemyIterator.hasNext()) {
                // Creamos un enemigo y le iniciamos con el siguiente enemigo de iterator
                Enemy enemy = enemyIterator.next();
                // Comprobamos si el enemigo no esta vivo
                if (!enemy.isAlive()) {
                    // Eliminamos el enemigo de la lista
                    enemyIterator.remove();
                    // Actualizamos con el execturo las monedas
                    executor.execute(() -> {
                        databaseHelper.updateMoney(databaseHelper.getMoney() + 6);
                    });
                }
            }
        }
    }

    /**
     * @return
     * Método en el que establecemos el camino
     * que van a recorrer los enemigos por la pantalla*/
    private ArrayList<int[]> crearCaminoEnemigo() {
        ArrayList<int[]> path = new ArrayList<>();
        path.add(new int[]{getWidth() - 100, getHeight() / 2});
        path.add(new int[]{getWidth() - 450, getHeight() / 2});
        path.add(new int[]{getWidth() - 450, getHeight() / 2 - 270});
        path.add(new int[]{getWidth() - 730, getHeight() / 2 - 270});
        path.add(new int[]{getWidth() - 730, getHeight() / 2});
        path.add(new int[]{getWidth() - 1330, getHeight() / 2});
        path.add(new int[]{getWidth() - 1330, getHeight() / 2 + 225});
        path.add(new int[]{getWidth() - 1765, getHeight() / 2 + 225});
        path.add(new int[]{getWidth() - 1765, getHeight() / 2});
        path.add(new int[]{getWidth() - 1950, getHeight() / 2});
        path.add(new int[]{100, getHeight() / 2});
        return path;
    }

    private class BucleJuego extends Thread {
        // Creamos todas las variables que vamos a usar en esta clase
        private SurfaceHolder surfaceHolder;
        private GameEngine gameEngine;
        private boolean running;

        private static final int MAX_FPS = 60;

        /**
         * @param gameEngine
         * @param surfaceHolder
         * Constructor en donde cargamos el surfaceHolder
         * y el gameengine con el que administramos
         * el juego*/
        public BucleJuego(SurfaceHolder surfaceHolder, GameEngine gameEngine) {
            super();
            this.surfaceHolder = surfaceHolder;
            this.gameEngine = gameEngine;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        @Override
        public void run() {
            // Declaro variables que me van a ayudar a desarrollar el hilo principal del juego
            long startTime;
            long timeMillis;
            long waitTime;
            int frameCount = 0;
            long totalTime = 0;
            long targetTime = 1000 / MAX_FPS;

            // Inicializamos un canvas en nulo
            Canvas canvas = null;

            // Mientras que el juego este corriendo
            while (running) {
                // Guardo en una variable el tiempode inicio
                startTime = System.currentTimeMillis();

                // Utilizo un try catch para manejar las excepciones
                try {
                    // Obtengo el canvas del surfceHolder
                    canvas = surfaceHolder.lockCanvas();
                    // Compruebo que el canvas no sea nulo
                    if (canvas != null) { // En caso de no ser nulo
                        // Procedemos a sincronizar el surface holder
                        synchronized (surfaceHolder) {
                            // Actualizamos el juego
                            gameEngine.update();
                            // Dibujamos el juego
                            gameEngine.draw(canvas);
                        }
                    }
                } catch (Exception e) { // En caso de que surja alguna excepción
                    // La sacamos por consola
                    e.printStackTrace();
                } finally { // Al final
                    // Si el canvas tiene algo
                    if (canvas != null) {
                        // Utilizamos un try catch para manejar las excepciones
                        try {
                            // Indicamos que hemos terminado de dibujar y se tiene que mostrar en pantalla
                            surfaceHolder.unlockCanvasAndPost(canvas);
                        } catch (Exception e) { // En caso de que surja alguna excepción
                            // La sacamos por la consola
                            e.printStackTrace();
                        }
                    }
                }

                // Obtenemos el tiempo actual menos el tiempo en el que empezamos
                timeMillis = System.currentTimeMillis() - startTime;
                // Obtenemos el tiempo de espera
                waitTime = targetTime - timeMillis;

                // Si el tiempo de espera es mayor que 0
                if (waitTime > 0) {
                    // Utilizamos un try catch para manejar las excepciones
                    try {
                        // Dormimos al hilo el tiempo de espera
                        Thread.sleep(waitTime);
                    } catch (InterruptedException e) { // En caso de surguir alguna excepción
                        // La sacamos por consola
                        e.printStackTrace();
                    }
                }

                // Guardamos en la variable de tiempo total el tiempo actual menos el tiempo en el que empezamos
                totalTime += System.currentTimeMillis() - startTime;
                // Aumentamos en uno el contador de frames
                frameCount++;

                // Si el tiempo total es mayor o igual que 1000
                if (totalTime >= 1000) {
                    // Imprimimos el contador de frames por consola
                    System.out.println("FPS: " + frameCount);
                    // Reiniciamos el contador de frames
                    frameCount = 0;
                    // Reiniciamos el tiempo total
                    totalTime = 0;
                }
            }
        }
    }
}