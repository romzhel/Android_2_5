package com.example.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    private static DBHelper instance;

    private static final String DATABASE_NAME = "weather.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_WEATHER = "weather";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CITY = "city";
    public static final String COLUMN_TEMPERATURE = "temperature";
    public static final String COLUMN_HUMIDITY = "humidity";


    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static DBHelper getDB(Context context) {
        if (instance == null) {
            instance = new DBHelper(context.getApplicationContext());
        }

        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("sql", "create");
        String tableCreateQuery = "CREATE TABLE " + TABLE_WEATHER + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_CITY + " TEXT,"
                + COLUMN_TEMPERATURE + " INTEGER,"
                + COLUMN_HUMIDITY + " INTEGER)";
        db.execSQL(tableCreateQuery);
        initTable();
    }

    public void initTable() {
        addCity("Москва", 25, 55);
        addCity("Санкт-Петербург", 20, 65);
        addCity("Новосибирск", 15, 40);
        addCity("Сочи", 35, 50);
        addCity("Владивосток", 30, 60);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //first version, no upgrades yet
    }

    public CityWeather getCity(String city) {
        String getQuery = "SELECT * FROM " + TABLE_WEATHER + " WHERE " + COLUMN_CITY + " = ?";
        SQLiteDatabase db = getReadableDatabase();
        String selection = COLUMN_CITY + " = ?";
        String[] selectionArgs = {city};
        Cursor cursor = db.query(TABLE_WEATHER, null, selection, selectionArgs,
                null, null, null);
        CityWeather cityWeather = null;

        try {

            if (cursor.moveToFirst()) {
                cityWeather = new CityWeather(
                        cursor.getLong(0),
                        cursor.getString(1),
                        cursor.getInt(2),
                        cursor.getInt(3)
                );
            }

        } catch (SQLException e) {

        } finally {
            cursor.close();
        }

        return cityWeather;
    }

    public void addCity(String city, int temperature, int humidity) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY, city);
        values.put(COLUMN_TEMPERATURE, temperature);
        values.put(COLUMN_HUMIDITY, humidity);
        db.insert(TABLE_WEATHER, null, values);
    }

    public void editCity(CityWeather city) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_CITY, city.getCity());
        values.put(COLUMN_TEMPERATURE, city.getTemperature());
        values.put(COLUMN_HUMIDITY, city.getHumidity());
        db.update(TABLE_WEATHER, values, COLUMN_ID + "=" + city.getId(), null);
    }
}
