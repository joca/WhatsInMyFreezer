package com.antergy.whatsinmyfreezer.database;

/**
 * Schema for the database. Makes it more secure to use the static fields when making requests
 * to the database.
 */
public class FoodDbSchema {
    public static final class FoodTable {
        public static final String FOOD = "food";

        public static final class Cols {
            public static final String NAME = "name";
            public static final String QUANTITY = "quantity";
            public static final String AMOUNT = "amount";
            public static final String BRAND = "brand";
            public static final String FOOD_ID = "_id";
            public static final String CATEGORY = "category";
        }
    }
}
