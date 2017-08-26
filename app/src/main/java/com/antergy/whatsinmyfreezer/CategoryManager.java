package com.antergy.whatsinmyfreezer;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is used for managing the categories. A category is saved as a shared preference
 * key:value pair.
 */
public class CategoryManager {
    private static CategoryManager sCategoryManager = null;
    private SharedPreferences mSharedPreferences;
    private static final String CATEGORY_PREFERENCES = "_CATEGORY_PREFERENCES";
    private static final String NOT_FOUND = "_RETURN_NOT FOUND";
    public static final String KEY_NO_CATEGORY = "_KEY_NO_CATEGORY";
    public static final String VALUE_NO_CATEGORY = "_NO_CATEGORY";
    private Context mContext;

    public static CategoryManager get(Context context) {
        if (sCategoryManager == null) {
            sCategoryManager = new CategoryManager(context);
        }
        return sCategoryManager;
    }

    private CategoryManager(Context context) {
        mContext = context.getApplicationContext();
        mSharedPreferences = mContext.getSharedPreferences(
                CATEGORY_PREFERENCES,
                mContext.MODE_PRIVATE);
    }

    /**
     * Stores a new category to if it does not already exists.
     * @param name the name of the category.
     * @return true if the category was stored and did not already exist, false otherwise.
     */
    public boolean addCategory(String name) {
        String findName = mSharedPreferences.getString(name, NOT_FOUND);
        if (findName.equalsIgnoreCase(NOT_FOUND)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.putString(name, name);
            editor.commit();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Changes the name of a category. If the new name already exists for another category,
     * the name change is aborted.
     * @param newName the new name to assign the category.
     * @param oldName the current name of the category.
     * @return 1 of the category name was changed, 0 otherwise.
     */
    public int changeCategory(String oldName, String newName) {
        String findName = mSharedPreferences.getString(newName, NOT_FOUND);
        if (findName.equalsIgnoreCase(NOT_FOUND)) {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.remove(oldName);
            editor.putString(newName, newName);
            editor.commit();
            // update database with new category name
            FoodManager foodManager = FoodManager.get(mContext);
            foodManager.updateCategory(oldName, newName);
            return 1;
        } else {
            return 0;
        }
    }

    /**
     * Removes a category if it exists.
     * @param name the name of the category to remove.
     * @return true if the category existed and was removed, false otherwise.
     */
    public boolean removeCategory(String name) {
        String findName = mSharedPreferences.getString(name, NOT_FOUND);
        if (findName.equalsIgnoreCase(NOT_FOUND)) {
            return false;
        } else {
            SharedPreferences.Editor editor = mSharedPreferences.edit();
            editor.remove(name);
            editor.commit();
            return true;
        }
    }

    /**
     * Retieves all the categories. A non-category is added as the head of the list and is used
     * to be the default shown item of a category spinner.
     * @return a list containing all the categories that has been created by the user.
     */
    public List<String> getCategories() {
        List<String> list = new ArrayList<>();
        list.add(mContext.getString(R.string.no_category));
        Map<String, ?> map = mSharedPreferences.getAll();
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            if (!defaultKey(entry.getKey())) {
                list.add(entry.getValue().toString());
            }
        }
        return list;
    }

    /**
     * A check to se if key is the KEY_NO_CATEGORY key.
     * @param key the key to check.
     * @return true if the key was the KEY_NO_CATEGORY key, false otherwise.
     */
    private boolean defaultKey(String key) {
        return key.equalsIgnoreCase(KEY_NO_CATEGORY);
    }
}
