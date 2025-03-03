package com.clase.riberadeffense.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.clase.riberadeffense.modelos.Tower;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    // Declaro el nombre de la base de datos
    private static final String DATABASE_NAME = "tower_defense.db";
    // Declaro la versión de la base de datos
    private static final int DATABASE_VERSION = 1;

    // Tabla de dinero
    private static final String TABLE_MONEY = "money";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_AMOUNT = "amount";

    // Tabla de torres
    private static final String TABLE_TOWERS = "towers";
    private static final String COLUMN_TOWER_ID = "tower_id";
    private static final String COLUMN_TOWER_LEVEL = "tower_level";
    private static final String COLUMN_TOWER_DANO = "tower_dano";
    private static final String COLUMN_TOWER_RANGO = "tower_rango";
    private static final String COLUMN_TOWER_RAPIDEZ = "tower_rapidez";
    private static final String COLUMN_TOWER_X = "tower_x";
    private static final String COLUMN_TOWER_Y = "tower_y";

    // Contexto para tener acceso a otros datos
    private Context contexto = null;

    /**
     * @param context
     * Constructor de la clase de la base de datos en donde
     * inicializamos la base de datos y creamos todas las
     * torres en caso de que no existan*/
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        initializeTowersIfNotExists(db);
        this.contexto = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla del dinero
        String CREATE_MONEY_TABLE = "CREATE TABLE " + TABLE_MONEY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_AMOUNT + " INTEGER" + ")";
        db.execSQL(CREATE_MONEY_TABLE);

        // Crear la tabla de torres
        String CREATE_TOWERS_TABLE = "CREATE TABLE " + TABLE_TOWERS + "("
                + COLUMN_TOWER_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_TOWER_LEVEL + " INTEGER, "
                + COLUMN_TOWER_DANO + " INTEGER, "
                + COLUMN_TOWER_RANGO + " INTEGER, "
                + COLUMN_TOWER_RAPIDEZ + " INTEGER, "
                + COLUMN_TOWER_X + " INTEGER, "
                + COLUMN_TOWER_Y + " INTEGER)";
        db.execSQL(CREATE_TOWERS_TABLE);

        // Insertar dinero inicial
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, 1);
        values.put(COLUMN_AMOUNT, 280);
        db.insert(TABLE_MONEY, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONEY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOWERS);
        onCreate(db);
    }

    public void initializeTowersIfNotExists(SQLiteDatabase db) {
        // Utilizo un cursor para comprobar si existen o no las torres
        Cursor cursor = db.query(TABLE_TOWERS, new String[]{COLUMN_TOWER_ID}, null, null, null, null, null);
        // En caso de que el cursor tenga cosas
        if (cursor != null && cursor.getCount() == 0) {

            int screenWidth = 0;
            int screenHeight = 0;

            // En caso de que el contexto no sea nulo
            if (contexto != null) {
                // Obteneos el witdh
                screenWidth = contexto.getResources().getDisplayMetrics().widthPixels;
                // Obtenemos el height
                screenHeight = contexto.getResources().getDisplayMetrics().heightPixels;
            }

            // Genero la torre con id 1
            ContentValues values = new ContentValues();
            values.put(COLUMN_TOWER_ID, 1);
            values.put(COLUMN_TOWER_LEVEL, 0);
            values.put(COLUMN_TOWER_DANO, 0);
            values.put(COLUMN_TOWER_RANGO, 0);
            values.put(COLUMN_TOWER_RAPIDEZ, 0);
            values.put(COLUMN_TOWER_X, screenWidth - 572);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 - 360);
            db.insert(TABLE_TOWERS, null, values);

            // Genero la torre con id 2
            values.clear();
            values.put(COLUMN_TOWER_ID, 2);
            values.put(COLUMN_TOWER_LEVEL, 0);
            values.put(COLUMN_TOWER_DANO, 0);
            values.put(COLUMN_TOWER_RANGO, 0);
            values.put(COLUMN_TOWER_RAPIDEZ, 0);
            values.put(COLUMN_TOWER_X, screenWidth - 765);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 + 180);
            db.insert(TABLE_TOWERS, null, values);

            // Genero la torre con id 3
            values.clear();
            values.put(COLUMN_TOWER_ID, 3);
            values.put(COLUMN_TOWER_LEVEL, 0);
            values.put(COLUMN_TOWER_DANO, 0);
            values.put(COLUMN_TOWER_RANGO, 0);
            values.put(COLUMN_TOWER_RAPIDEZ, 0);
            values.put(COLUMN_TOWER_X, screenWidth - 1320);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 - 125);
            db.insert(TABLE_TOWERS, null, values);

            // Genero la torre con id 4
            values.clear();
            values.put(COLUMN_TOWER_ID, 4);
            values.put(COLUMN_TOWER_LEVEL, 0);
            values.put(COLUMN_TOWER_DANO, 0);
            values.put(COLUMN_TOWER_RANGO, 0);
            values.put(COLUMN_TOWER_RAPIDEZ, 0);
            values.put(COLUMN_TOWER_X, screenWidth - 1560);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 + 130);
            db.insert(TABLE_TOWERS, null, values);

            // Genero la torre con id 5
            values.clear();
            values.put(COLUMN_TOWER_ID, 5);
            values.put(COLUMN_TOWER_LEVEL, 0);
            values.put(COLUMN_TOWER_DANO, 0);
            values.put(COLUMN_TOWER_RANGO, 0);
            values.put(COLUMN_TOWER_RAPIDEZ, 0);
            values.put(COLUMN_TOWER_X, screenWidth - 1990);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 + 180);
            db.insert(TABLE_TOWERS, null, values);
        }
        // Si el cursor tiene algo, le cerramos
        if (cursor != null) cursor.close();
    }

    public List<Tower> getAllTowers(Context context) {
        // Creo e inicializo un a lista de torres
        List<Tower> towers = new ArrayList<>();
        // Inicializo la base de datos para poder sobreescribirla
        SQLiteDatabase db = this.getReadableDatabase();
        // Genero un cursor al que le voy a utilziar para obtener todos losd atos de todas las torres
        Cursor cursor = db.query(TABLE_TOWERS, null, null, null, null, null, null);

        // Compruebo si el cursor puede moverse a la siguiente fila
        if (cursor.moveToFirst()) { // De ser así
            // Utilizo un do while
            do {
                // Obtengo todos los datos de la torre
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_ID));
                int level = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_LEVEL));
                int damage = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_DANO));
                int range = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_RANGO));
                int attackSpeed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_RAPIDEZ));
                int x = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_X));
                int y = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_Y));
                boolean isUnlocked = level > 0;

                // Genero una nueva torre con todos sus datos
                Tower tower = new Tower(x, y, range, level, damage, attackSpeed, id, context);
                // Establezco si la torre esta bloqueada o no
                tower.setUnlocked(isUnlocked);
                // Agrego la torre a la lista de torres
                towers.add(tower);
            } while (cursor.moveToNext()); // While mientras pueda seguir moviendose el cursor
        }
        // Cierro el cursor
        cursor.close();
        // Dvuelvo la lista de torres
        return towers;
    }

    public void saveTower(Tower tower) {
        // Inicializo la base de datos para poder sobreescribirla
        SQLiteDatabase db = this.getWritableDatabase();
        // Genero un content values para poder actualizar la torre
        ContentValues values = new ContentValues();

        // Establezco todos los valores significativos de la torre
        values.put(COLUMN_TOWER_LEVEL, tower.getLevel());
        values.put(COLUMN_TOWER_DANO, tower.getDamage());
        values.put(COLUMN_TOWER_RANGO, tower.getRange());
        values.put(COLUMN_TOWER_RAPIDEZ, tower.getAttackSpeed());

        // Abro un cursor para hacer una serie de comprobaciones en la torre con ese id que ya existe en la bd
        Cursor cursor = db.query(TABLE_TOWERS, new String[]{COLUMN_TOWER_ID},
                COLUMN_TOWER_ID + "=?", new String[]{String.valueOf(tower.getId())},
                null, null, null);

        // Compruebo si el cursor puede moverse al primer registro
        if (cursor.moveToFirst()) { // De ser así
            // Porcedemos a actualziar la torre con ese id de torre
            db.update(TABLE_TOWERS, values, COLUMN_TOWER_ID + "=?", new String[]{String.valueOf(tower.getId())});
        } else {
            // La torre no existe, insertar una nueva
            values.put(COLUMN_TOWER_ID, tower.getId());
            values.put(COLUMN_TOWER_X, tower.getX());
            values.put(COLUMN_TOWER_Y, tower.getY());
            db.insert(TABLE_TOWERS, null, values);
        }

        // Cierro el cursor
        cursor.close();
    }


    public int getMoney() {
        // Inicializo la base de datos para poder sobreescribirla
        SQLiteDatabase db = this.getReadableDatabase();
        // Creo un cursor para poder hacer un consulta y obtener el dinero que tenemos en la base de datos
        Cursor cursor = db.query(TABLE_MONEY, new String[]{COLUMN_AMOUNT}, COLUMN_ID + "=?", new String[]{"1"}, null, null, null);
        // Inicializo una variable de dinero a 0
        int money = 0;
        // Compruebo si el cursor puede moverse a primer registro
        if (cursor.moveToFirst()) { // Si es así
            // Cargo en la variable antes creada el valor de las monedas que tengamos en la base de datos
            money = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
        }
        // Cierro el cursor
        cursor.close();
        // Devuelvo la cantidad de dinero
        return money;
    }

    public void updateMoney(int amount) {
        // Inicializo la base de datos para poder sobreescribirla
        SQLiteDatabase db = this.getWritableDatabase();
        // Genero un content values para poder actualizar el dinero
        ContentValues values = new ContentValues();
        // Establezco el valor que le pasamos como parametros
        values.put(COLUMN_AMOUNT, amount);
        // Actualizamos la fila
        db.update(TABLE_MONEY, values, COLUMN_ID + "=?", new String[]{"1"});
    }

    public void resetDatabase() {
        // Inicializo la base de datos para poder sobreescribirla
        SQLiteDatabase db = this.getWritableDatabase();

        // Elimino la tabla de las torres
        db.delete(TABLE_TOWERS, null, null);
        // Elimino la tabla del dinero
        db.delete(TABLE_MONEY, null, null);

        // Genero un nuevo content values
        ContentValues moneyValues = new ContentValues();
        // Donde guardo el id de la columna del dinero
        moneyValues.put(COLUMN_ID, 1);
        // Y el valor de las monedas de esa fila
        moneyValues.put(COLUMN_AMOUNT, 280);
        // Inserto y creo la nueva tabla
        db.insert(TABLE_MONEY, null, moneyValues);

        // Procedemos a inicializar todas las columnas
        initializeTowersIfNotExists(db);
    }
}