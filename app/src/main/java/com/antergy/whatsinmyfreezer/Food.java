package com.antergy.whatsinmyfreezer;

/**
 * A class representing a food item that is stored in the freezer.
 * A food item always has a name and an unique id. The food item can also have a brand (producer),
 * an amount (always with a specified unit), a category. The quantity of a food item is always at
 * least 1. If food item with the same name, brand, amount and category is added to the freezer as
 * a food item already stored, the two food items will be stored together as one and the quantity
 * will be updated.
 * A photo connected to the food item can be created. The food item will tie to the photo by
 * setting the name of the photo file in the phone storage, based on the food item id.
 */
public class Food {
    private String mName;
    private int mQuantity;
    private String mAmount;
    private String mBrand;
    private int mId;
    private String mCategory;

    /**
     * Constructor for creating a Food object before its id have been created in the database.
     */
    public Food(String name, int quantity, String amount, String brand, String category) {
        mName = name;
        mQuantity = quantity;
        mAmount = amount;
        mBrand = brand;
        mCategory = category;
    }

    /**
     * Constructor for creating a Food object when the id is known.
     */
    public Food(String name, int quantity, String amount, String brand, int id, String category) {
        mName = name;
        mQuantity = quantity;
        mAmount = amount;
        mBrand = brand;
        mCategory = category;
        mId = id;
    }

    /**
     * Gets the name of the food item.
     */
    public String getName() {
        return mName;
    }

    /**
     * Getts the quantity of the food item.
     */
    public int getQuantity() {
        return mQuantity;
    }

    /**
     * Sets the quantity of the food item.
     */
    public void setQuantity(int quantity) {
        mQuantity = quantity;
    }

    /**
     * Gets the amount of the food item.
     */
    public String getAmount() {
        return mAmount;
    }

    /**
     * Gets the brand of the food item.
     */
    public String getBrand() {
        return mBrand;
    }

    /**
     * Gets the unique id of the food item.
     */
    public int getId() {
        return mId;
    }

    /**
     * Sets the id of the food item.
     */
    public void setId(int id) {
        mId = id;
    }

    /**
     * Gets the category of the food item.
     */
    public String getCategory() {
        return mCategory;
    }

    /**
     * Sets the category of the food item.
     */
    public void setCategory(String category) {
        mCategory = category;
    }

    /**
     * Gets the name of the photo file that can be created for the food item. The String created is
     * based on the id of the food item.
     */
    public String getPhotoFilename() {
        return "IMG_" + Integer.toString(mId) + ".jpg";
    }
}
