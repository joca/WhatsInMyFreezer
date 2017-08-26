package com.antergy.whatsinmyfreezer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.antergy.whatsinmyfreezer.database.FoodBaseHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.antergy.whatsinmyfreezer.database.FoodCursorWrapper;
import com.antergy.whatsinmyfreezer.database.FoodDbSchema.FoodTable;

/**
 * A class that manages all food items stored in the freezer. It connects to the SQLite database
 * where all created food items will be stored. The food managed also manages the photos that
 * belongs to the food items.
 */
public class FoodManager {
    private static FoodManager sFoodManager = null;
    private final static String TEMP_PHOTO_FILE = "IMG_TEMP.jpg";
    private final static String TEMP_SAVED_PHOTO_FILE = "IMG_SAVED_TEMP.jpg";
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static FoodManager get(Context context) {
        if (sFoodManager == null) {
            sFoodManager = new FoodManager(context);
        }
        return sFoodManager;
    }

    private FoodManager(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new FoodBaseHelper(mContext).getWritableDatabase();
    }

    /**
     * Adds a new food item to the database. If a same food item already exists in
     * the database, its quantity will be updated by adding the quantity of the added food item.
     * @param name      name of the food item.
     * @param quantity  number of food items to store.
     * @param amount    how much one food item contains (different units).
     * @param brand     producer of the food item, if any.
     * @param category  the category of the food, if any.
     */
    public Food addFood(String name, int quantity, String amount, String brand, String category) {
        Food food = getFood(name, amount, brand, category);
        if (food == null) {
            food = new Food(name, quantity, amount, brand, category);
            ContentValues values = getContentValues(food);
            mDatabase.insert(FoodTable.FOOD, null, values);
            int id = getFoodId(food);
            food = getFood(id);
            return food;
        } else {
            int newQuantity = food.getQuantity()+quantity;
            food.setQuantity(newQuantity);
            updateFood(food.getId(), food);
            return food;
        }
    }

    /**
     * Gets the id of a specific food item. If a food item is created that has not yet been added
     * to the database, it does not yet have a unique id. When added to the database, it is
     * assigned id and it can then be retrieved.
     * @param food the food item to retrieve an id for.
     * @return the id of the food item if it exists in the database, -1 otherwise.
     */
    private int getFoodId(Food food) {
        FoodCursorWrapper cursor = queryFood(
                FoodTable.Cols.NAME + " = ?" + " and " +
                        FoodTable.Cols.AMOUNT + " = ? " + " and " +
                        FoodTable.Cols.BRAND + " = ? " + " and " +
                        FoodTable.Cols.CATEGORY + " = ? ",
                new String[] {
                        food.getName(),
                        food.getAmount(),
                        food.getBrand(),
                        food.getCategory()}
        );

        try {
            if (cursor.getCount() == 0) {
                return -1;
            }
            cursor.moveToFirst();
            return cursor.getFood().getId();
        } finally {
            cursor.close();;
        }
    }

    /**
     * Returns a list of food items based on which category it belongs to.
     * @param category the category of food items to return.
     * @return a list containing food items.
     */
    public List<Food> getCategoryList(String category) {
        List<Food> foodList = new ArrayList<>();

        FoodCursorWrapper cursor = queryFood(
                FoodTable.Cols.CATEGORY + " = ? ",
                new String[] {category}
        );

        try {
            while (cursor.moveToNext()) {
                foodList.add(cursor.getFood());
            }
        } finally {
            cursor.close();;
        }
        return foodList;
    }

    /**
     * Returns a list of all food items in the database.
     * @return a list containing all food items.
     */
    public List<Food> getAll() {
        List<Food> foodList = new ArrayList<>();

        FoodCursorWrapper cursor = queryFood(null, null);

        try {
            while (cursor.moveToNext()) {
                foodList.add(cursor.getFood());
            }
        } finally {
            cursor.close();
        }
        return foodList;
    }

    /**
     * Returns a Food object based on its unique id.
     * @param id the id of the food item.
     * @return a food item if the id is valid, null otherwise.
     */
    public Food getFood(int id) {
        FoodCursorWrapper cursor = queryFood(
                FoodTable.Cols.FOOD_ID + " = ?",
                new String[] {Integer.toString(id)}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getFood();
        } finally {
            cursor.close();;
        }
    }

    /**
     * Updates a food item in the database.
     * @param id the id of the food item to update.
     * @param updatedFood the new food item to replace the old one with.
     */
    public void updateFood(int id, Food updatedFood) {
        String foodId = Integer.toString(id);
        ContentValues values = getContentValues(updatedFood);

        mDatabase.update(FoodTable.FOOD,
                values,
                FoodTable.Cols.FOOD_ID + " = ?",
                new String[] {foodId}
        );
    }

    /**
     * Returns a food object, if it exists in the database. There can only be one food item
     * in the storage with the same three parameter values.
     * @param name      the name of the food item.
     * @param amount    the amount of the food item.
     * @param brand     the brand of the food item.
     * @param category  the category of the food item.
     * @return the corresponding Food object if it exists, null otherwise.
     */
    public Food getFood(String name, String amount, String brand, String category) {
        FoodCursorWrapper cursor = queryFood(
                FoodTable.Cols.NAME + " = ?" + " and " +
                FoodTable.Cols.AMOUNT + " = ? " + " and " +
                FoodTable.Cols.BRAND + " = ? " + " and " +
                FoodTable.Cols.CATEGORY + " = ? ",
                new String[] {name, amount, brand, category}
        );

        try {
            if (cursor.getCount() == 0) {
                return null;
            }
            cursor.moveToFirst();
            return cursor.getFood();
        } finally {
            cursor.close();;
        }
    }

    /**
     * Removes all food items in the database that belongs to a specific category.
     * @param category the category of the food items to delete.
     */
    public void removeAllInCategory(String category) {
        mDatabase.delete(
                FoodTable.FOOD,
                FoodTable.Cols.CATEGORY + " = ?",
                new String[] {category}
        );
    }

    /**
     * Deletes a food item from the database.
     * @param id the id of the food item to delete.
     */
    public void deleteFood(int id) {
        mDatabase.delete(
                FoodTable.FOOD,
                FoodTable.Cols.FOOD_ID + " = ?",
                new String[] {Integer.toString(id)}
        );
    }

    /**
     * Updates the category column of the food items that had it set to its previous name and sets
     * it to its new name.
     * @param oldCategory the old category name.
     * @param newCategory the new category name.
     */
    public void updateCategory(String oldCategory, String newCategory) {
        ContentValues values = new ContentValues();
        values.put(FoodTable.Cols.CATEGORY, newCategory);

        mDatabase.update(FoodTable.FOOD,
                values,
                FoodTable.Cols.CATEGORY+ " = ?",
                new String[] {oldCategory}
        );
    }

    /**
     * A cursor wrapper for making queries to the database.
     * @param whereClause which columns that are of concern.
     * @param whereArgs which data in each column to match.
     */
    private FoodCursorWrapper queryFood(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                FoodTable.FOOD,
                null, // Null selects all columns
                whereClause,
                whereArgs,
                null, // group by
                null, // having
                null //order by
        );

        return new FoodCursorWrapper(cursor);
    }

    /**
     * Content values for inserting food items into the database.
     */
    private static ContentValues getContentValues(Food food) {
        ContentValues values = new ContentValues();
        values.put(FoodTable.Cols.NAME, food.getName());
        values.put(FoodTable.Cols.QUANTITY, food.getQuantity());
        values.put(FoodTable.Cols.AMOUNT, food.getAmount());
        values.put(FoodTable.Cols.BRAND, food.getBrand());
        values.put(FoodTable.Cols.CATEGORY, food.getCategory());

        return values;
    }

    /**
     * Renames the name of a photo file in the internal storage.
     * @param name the name to rename the file to.
     * @param photoFile the file to be renamed.
     */
    public void renamePhotoFile(String name, File photoFile) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return;
        }
        if (photoFile == null) {
            return;
        }

        File newPhotoFile = new File(externalFilesDir, name);

        if (photoFile.exists()) {
            photoFile.renameTo(newPhotoFile);
        }
    }

    /**
     * Deletes the TEMP_PHOTO_FILE file.
     */
    public void deleteTempPhotoFile() {
        File file = getTempPhotoFile();
        deletePhotoFile(file);
    }

    /**
     * Deletes a specific photo file.
     * @param file the photo to delete.
     */
    public void deletePhotoFile(File file) {
        file.delete();
    }

    /**
     * Retrieves a specific photo file.
     * @param name the name of the photo file to retrieve.
     * @return the photo file requested.
     */
    public File getPhotoFile(String name) {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, name);
    }

    /**
     * Retrieves the TEMP_PHOTO_FILE file.
     * @return the temporary photo file.
     */
    public File getTempPhotoFile() {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        return new File(externalFilesDir, TEMP_PHOTO_FILE);
    }

    /**
     * If an activity needs to save the TEMP_PHOTO_FILE during a screen rotation, this method is
     * used. It is saved to another temporary file called TEMP_SAVED_PHOTO_FILE.
     * TEMP_SAVED_PHOTO_FILE is removed when the method getSavedTempPhotoFile() is called.
     * @param tempFile the temporary photo file.
     */
    public void saveTempPhotoFile(File tempFile) {
        renamePhotoFile(TEMP_SAVED_PHOTO_FILE, tempFile);
        deleteTempPhotoFile();
    }

    /**
     * Method to use after saveTempPhotoFile() have been called. It sets the TEMP_PHOTO_FILE to
     * store the photo in TEMP_SAVED_PHOTO_FILE and deletes TEMP_SAVED_PHOTO_FILE.
     * @return the TEMP_PHOTO_FILE containing a photo.
     */
    public File getSavedTempPhotoFile() {
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null) {
            return null;
        }

        File savedTempPhotoFile = new File(externalFilesDir, TEMP_SAVED_PHOTO_FILE);

        deleteTempPhotoFile();
        File tempPhotoFile = getTempPhotoFile();

        if (savedTempPhotoFile.exists()) {
            savedTempPhotoFile.renameTo(tempPhotoFile);
        } else {
            return null;
        }

        File fileToRemove = getPhotoFile(TEMP_SAVED_PHOTO_FILE);
        deletePhotoFile(fileToRemove);

        return new File(externalFilesDir, TEMP_PHOTO_FILE);
    }
}
