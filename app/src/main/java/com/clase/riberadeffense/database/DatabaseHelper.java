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
    private static final String DATABASE_NAME = "tower_defense.db";
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

    private Context contexto = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
        initializeTowersIfNotExists(db);
        this.contexto = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tabla de dinero
        String CREATE_MONEY_TABLE = "CREATE TABLE " + TABLE_MONEY + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY,"
                + COLUMN_AMOUNT + " INTEGER" + ")";
        db.execSQL(CREATE_MONEY_TABLE);

        // Crear tabla de torres
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
        values.put(COLUMN_AMOUNT, 500); // Dinero inicial
        db.insert(TABLE_MONEY, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MONEY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TOWERS);
        onCreate(db);
    }

    public void initializeTowersIfNotExists(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_TOWERS, new String[]{COLUMN_TOWER_ID}, null, null, null, null, null);
        if (cursor != null && cursor.getCount() == 0) {

            int screenWidth = 0;
            int screenHeight = 0;

            if (contexto != null) {
                screenWidth = contexto.getResources().getDisplayMetrics().widthPixels;
                screenHeight = contexto.getResources().getDisplayMetrics().heightPixels;
            }

            // No hay torres en la base de datos, insertar las torres iniciales
            ContentValues values = new ContentValues();
            values.put(COLUMN_TOWER_ID, 1);
            values.put(COLUMN_TOWER_LEVEL, 0);
            values.put(COLUMN_TOWER_DANO, 0);
            values.put(COLUMN_TOWER_RANGO, 0);
            values.put(COLUMN_TOWER_RAPIDEZ, 0);
            values.put(COLUMN_TOWER_X, screenWidth - 572);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 - 360);
            db.insert(TABLE_TOWERS, null, values);

            values.clear();
            values.put(COLUMN_TOWER_ID, 2);
            values.put(COLUMN_TOWER_LEVEL, 0);
            values.put(COLUMN_TOWER_DANO, 0);
            values.put(COLUMN_TOWER_RANGO, 0);
            values.put(COLUMN_TOWER_RAPIDEZ, 0);
            values.put(COLUMN_TOWER_X, screenWidth - 765);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 + 180);
            db.insert(TABLE_TOWERS, null, values);

            values.clear();
            values.put(COLUMN_TOWER_ID, 3);
            values.put(COLUMN_TOWER_LEVEL, 0);
            values.put(COLUMN_TOWER_DANO, 0);
            values.put(COLUMN_TOWER_RANGO, 0);
            values.put(COLUMN_TOWER_RAPIDEZ, 0);
            values.put(COLUMN_TOWER_X, screenWidth - 1320);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 - 125);
            db.insert(TABLE_TOWERS, null, values);

            values.clear();
            values.put(COLUMN_TOWER_ID, 4);
            values.put(COLUMN_TOWER_LEVEL, 0);
            values.put(COLUMN_TOWER_DANO, 0);
            values.put(COLUMN_TOWER_RANGO, 0);
            values.put(COLUMN_TOWER_RAPIDEZ, 0);
            values.put(COLUMN_TOWER_X, screenWidth - 1560);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 + 130);
            db.insert(TABLE_TOWERS, null, values);

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
        if (cursor != null) cursor.close();
    }

    public List<Tower> getAllTowers(Context context) {
        List<Tower> towers = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TOWERS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_ID));
                int level = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_LEVEL));
                int damage = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_DANO));
                int range = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_RANGO));
                int attackSpeed = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_RAPIDEZ));
                int x = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_X));
                int y = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_Y));
                boolean isUnlocked = level > 0;

                Tower tower = new Tower(x, y, range, level, damage, attackSpeed, id, context);
                tower.setUnlocked(isUnlocked);
                towers.add(tower);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return towers;
    }

    public void saveTower(Tower tower) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        int screenWidth = 0;
        int screenHeight = 0;

        if (contexto != null) {
            screenWidth = contexto.getResources().getDisplayMetrics().widthPixels;
            screenHeight = contexto.getResources().getDisplayMetrics().heightPixels;
        }
        values.put(COLUMN_TOWER_ID, tower.getId());
        values.put(COLUMN_TOWER_LEVEL, tower.getLevel());
        values.put(COLUMN_TOWER_DANO, tower.getDamage());
        values.put(COLUMN_TOWER_RANGO, tower.getRange());
        values.put(COLUMN_TOWER_RAPIDEZ, tower.getAttackSpeed());
        if(tower.getId()==1){
            values.put(COLUMN_TOWER_X, screenWidth - 572);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 - 360);
        }else if(tower.getId()==2){
            values.put(COLUMN_TOWER_X, screenWidth - 765);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 + 180);
        }else if(tower.getId()==3){
            values.put(COLUMN_TOWER_X, screenWidth - 1320);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 - 125);
        }else if(tower.getId()==4){
            values.put(COLUMN_TOWER_X, screenWidth - 1560);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 + 130);
        }else if(tower.getId()==5){
            values.put(COLUMN_TOWER_X, screenWidth - 1990);
            values.put(COLUMN_TOWER_Y, screenHeight / 2 + 180);
        }

        db.insertWithOnConflict(TABLE_TOWERS, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public int getMoney() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MONEY, new String[]{COLUMN_AMOUNT}, COLUMN_ID + "=?", new String[]{"1"}, null, null, null);
        int money = 0;
        if (cursor.moveToFirst()) {
            money = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_AMOUNT));
        }
        cursor.close();
        return money;
    }

    public void updateMoney(int amount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_AMOUNT, amount);
        db.update(TABLE_MONEY, values, COLUMN_ID + "=?", new String[]{"1"});
    }

    public int getTowerLevel(int towerId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TOWERS, new String[]{COLUMN_TOWER_LEVEL}, COLUMN_TOWER_ID + "=?", new String[]{String.valueOf(towerId)}, null, null, null);
        int level = 0;
        if (cursor.moveToFirst()) {
            level = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TOWER_LEVEL));
        }
        cursor.close();
        return level;
    }

    public void updateTowerLevel(int towerId, int level, int damage, int attackSpeed, int range) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TOWER_LEVEL, level);
        values.put(COLUMN_TOWER_DANO, damage);
        values.put(COLUMN_TOWER_RANGO, range);
        values.put(COLUMN_TOWER_RAPIDEZ, attackSpeed);
        db.update(TABLE_TOWERS, values, COLUMN_TOWER_ID + "=?", new String[]{String.valueOf(towerId)});
    }

    public void resetDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(TABLE_TOWERS, null, null);
        db.delete(TABLE_MONEY, null, null);

        ContentValues moneyValues = new ContentValues();
        moneyValues.put(COLUMN_ID, 1);
        moneyValues.put(COLUMN_AMOUNT, 500);
        db.insert(TABLE_MONEY, null, moneyValues);

        initializeTowersIfNotExists(db);
    }
}