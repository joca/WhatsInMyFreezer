package com.antergy.whatsinmyfreezer.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.antergy.whatsinmyfreezer.Food;

import com.antergy.whatsinmyfreezer.database.FoodDbSchema.FoodTable;


/**
 * A wrapper class for the database when retrieving a food item from the database. Makes it more
 * secure to use a wrapper.
 */
public class FoodCursorWrapper extends CursorWrapper {
    public FoodCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Food getFood() {
        String name = getString(getColumnIndex(FoodTable.Cols.NAME));
        int quantity = getInt(getColumnIndex(FoodTable.Cols.QUANTITY));
        String amount = getString(getColumnIndex(FoodTable.Cols.AMOUNT));
        String brand = getString(getColumnIndex(FoodTable.Cols.BRAND));
        int foodId = getInt(getColumnIndex(FoodTable.Cols.FOOD_ID));
        String category = getString(getColumnIndex(FoodTable.Cols.CATEGORY));

        return new Food(name, quantity, amount, brand, foodId, category);
    }
}
