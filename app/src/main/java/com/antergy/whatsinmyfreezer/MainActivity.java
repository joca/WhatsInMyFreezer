package com.antergy.whatsinmyfreezer;

import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import java.util.ArrayList;
import java.util.List;

/**
 * Main activity, where the application starts. This activity handles the drawer menu that slides
 * from the left side of the screen. From the drawer menu, a user can navigate between different
 * categories, access the activity that handles the categories and return back to the "home" screen.
 */
public class MainActivity extends AppCompatActivity {
    private final static String KEY_FRAGMENT_NAME = "fragmentName";
    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private MenuItem mPreviousMenuItem;
    private CharSequence mCurrentFragmentTitle;
    private boolean mClickedHandleCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.main_drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.main_navigation_menu);

        setSupportActionBar(toolbar);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        setNavigationView();

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.content_frame);

        if (savedInstanceState != null) {
            setTitle(savedInstanceState.getCharSequence(KEY_FRAGMENT_NAME));
        }

        if (fragment == null) {
            fragment = new MainFragment();
            MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.home);
            mCurrentFragmentTitle = menuItem.getTitle();
            setTitle(mCurrentFragmentTitle);
            changeCheckedMenuItem(menuItem);
            fm.beginTransaction()
                    .add(R.id.content_frame, fragment)
                    .commit();
        }
    }

    /**
     * Changes the checked MenuItem to the newly tapped.
     * @param menuItem the MenuItem to check.
     */
    private void changeCheckedMenuItem(MenuItem menuItem) {
        menuItem.setCheckable(true);
        menuItem.setChecked(true);
        if (mPreviousMenuItem != null) {
            mPreviousMenuItem.setChecked(false);
        }
        mPreviousMenuItem = menuItem;
    }

    /**
     * Sets the Navigation View.
     */
    private void setNavigationView() {
        setMenuCategories();
        mNavigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        changeCheckedMenuItem(menuItem);
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }

    /**
     * This method is used to set the drawer layout to have the "all" category checked.
     */
    public void setAllCategoryAsChecked() {
        MenuItem menuItem = mNavigationView.getMenu().findItem(R.id.show_all_category);
        changeCheckedMenuItem(menuItem);
    }

    /**
     * This method is used to check if "handle categories" was the most recent item
     * clicked in the menu.
     * @return true if "handle categories" was the most recent choice, false otherwise.
     */
    public boolean wasCategoryClicked() {
        return mClickedHandleCategory;
    }

    /**
     * Handles the sub menu categories dynamically. This because categories can be created,
     * renamed and removed.
     */
    private void setMenuCategories() {
        Menu menu = mNavigationView.getMenu();
        MenuItem categoryGroupItem = menu.findItem(R.id.group_categories);
        SubMenu subMenu = categoryGroupItem.getSubMenu();

        ArrayList<MenuItem> removeList = new ArrayList<>();
        for (int i = 1; i < subMenu.size(); ++i) {
            removeList.add(subMenu.getItem(i));
        }
        for (MenuItem item : removeList) {
            subMenu.removeItem(item.getItemId());
        }

        CategoryManager categoryManager = CategoryManager.get(MainActivity.this);
        List<String> categories = categoryManager.getCategories();
        for (String category : categories) {
            String categoryName = getString(R.string.no_category);
            if (!category.equalsIgnoreCase(categoryName)) {
                MenuItem item = subMenu.add(category);
                item.setIcon(R.drawable.ic_chevron_right_black_36dp);
            }
        }
    }

    /**
     * Handles the items in the menu and what should happen when they are clicked.
     * @param menuItem the menu item that was clicked.
     */
    private void selectDrawerItem(MenuItem menuItem) {
        int menuItemId = menuItem.getItemId();
        if (menuItemId == R.id.home) {
            mClickedHandleCategory = false;
            Fragment fragment = new MainFragment();
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
            mCurrentFragmentTitle = menuItem.getTitle();
            setTitle(mCurrentFragmentTitle);
            mDrawerLayout.closeDrawers();
        } else if (menuItemId == R.id.handle_category) {
            mClickedHandleCategory = true;
            Intent intent = new Intent(MainActivity.this, CategoryActivity.class);
            startActivity(intent);
            mDrawerLayout.closeDrawers();
        } else {
            mClickedHandleCategory = false;
            String category = menuItem.getTitle().toString();
            Fragment fragment = ContentListFragment.newInstance(category);
            FragmentManager fm = getSupportFragmentManager();
            fm.beginTransaction().replace(R.id.content_frame, fragment).commit();
            mCurrentFragmentTitle = menuItem.getTitle();
            setTitle(mCurrentFragmentTitle);
            mDrawerLayout.closeDrawers();
        }
    }

    /* Some methods for handeling the drawer menu */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        mDrawerToggle.onConfigurationChanged(configuration);
    }
    /*********************************************/


    /**
     * Overrides onSaveInstanceState to be able to know which category that was previously accessed
     * from the menu, if the MainActivity is closed.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putCharSequence(KEY_FRAGMENT_NAME, mCurrentFragmentTitle);
    }

    /**
     * Overrides onResume to make sure the category list in the menu is always up to date.
     */
    @Override
    protected void onResume() {
        super.onResume();
        setMenuCategories();
    }
}