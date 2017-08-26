package com.antergy.whatsinmyfreezer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

/**
 * An Activity class for displaying a food item.
 */
public class FoodItemActivity extends AppCompatActivity {
    private final static String EXTRA_FOOD_ITEM_ID =
            "com.antergy.android.whatsinmyfreezer.foodItemActivity";
    private Food mFood;

    public static Intent newIntent(Context packageContext, String[] food) {
        Intent intent = new Intent(packageContext, FoodItemActivity.class);
        intent.putExtra(EXTRA_FOOD_ITEM_ID, food);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_container);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.fragment_container);

        String[] food = (String[]) getIntent().getSerializableExtra(EXTRA_FOOD_ITEM_ID);

        if (fragment == null) {
            fragment = FoodItemFragment.newInstance(food);
            fm.beginTransaction().add(R.id.fragment_container, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
