package com.antergy.whatsinmyfreezer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.antergy.whatsinmyfreezer.database.FoodDbSchema.FoodTable;

/**
 * Helper class for creating the SQLite database associated with this application.
 */
public class FoodBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "foodBase.db";

    public FoodBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase dB) {
        dB.execSQL("create table " + FoodTable.FOOD + "(" +
                " _id integer primary key autoincrement, " +
                FoodTable.Cols.NAME + ", " +
                FoodTable.Cols.QUANTITY + ", " +
                FoodTable.Cols.AMOUNT + ", " +
                FoodTable.Cols.BRAND + ", " +
                FoodTable.Cols.CATEGORY +
                ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
