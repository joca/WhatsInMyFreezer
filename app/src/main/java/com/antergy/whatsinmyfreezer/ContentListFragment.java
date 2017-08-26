package com.antergy.whatsinmyfreezer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * A fragment for displaying a list of food items. The list content is decided by which category
 * it represents.
 */
public class ContentListFragment extends Fragment {
    private final static String ARG_CATEGORY = "category";
    private RecyclerView mFoodRecyclerView;
    private FoodAdapter mAdapter;
    private String mCategory;

    public static ContentListFragment newInstance(String category) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORY, category);

        ContentListFragment fragment = new ContentListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);

        mFoodRecyclerView = (RecyclerView) view.findViewById(R.id.content_recycler_view);
        mFoodRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (mCategory == null) {
            mCategory = (String) getArguments().getSerializable(ARG_CATEGORY);
        }
        updateUI(mCategory);
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.content_toolbar_menu, menu);
    }

    /**
     * Updates the user interface.
     * @param category
     */
    private void updateUI(String category) {
        if (mAdapter == null) {
            mAdapter = new FoodAdapter(category);
            mFoodRecyclerView.setAdapter(mAdapter);
        } else {
            mAdapter.setFoodList(category);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * Checks if the add new food item button has been pressed in the toolbar.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            goToAdd();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Starts the AddActivity if the user has pressed its corresponding button in the toolbar.
     */
    private void goToAdd() {
        Intent intent = new Intent(getActivity(), AddActivity.class);
        startActivity(intent);
    }

    /**
     * A holder class for the RecyclerView that binds food items to the list and starts
     * FoodItemActivity if the food item is pressed.
     */
    private class FoodHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {
        private Food mFood;
        private TextView mNameTextView;         // Product name
        private TextView mQuantityTextView;     // Number of same product
        private TextView mAmountTextView;       // How uch of product
        private TextView mBrandTextView;        // Manufacturer
        private TextView mCategoryTextView;      // Category

        public FoodHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mNameTextView = (TextView)
                    itemView.findViewById(R.id.name_content_list);
            mQuantityTextView = (TextView)
                    itemView.findViewById(R.id.quantity_content_list);
            mAmountTextView = (TextView)
                    itemView.findViewById(R.id.amount_content_list);
            mBrandTextView = (TextView)
                    itemView.findViewById(R.id.brand_content_list);
            mCategoryTextView = (TextView)
                    itemView.findViewById(R.id.category_content_list);
        }

        public void bindFood(Food food) {
            mFood = food;
            mNameTextView.setText(mFood.getName());

            String quantity = "("+mFood.getQuantity()+")";
            mQuantityTextView.setText(quantity);

            mAmountTextView.setText(mFood.getAmount());

            mBrandTextView.setText(mFood.getBrand());

            if (!mFood.getCategory().equalsIgnoreCase(CategoryManager.VALUE_NO_CATEGORY)) {
                mCategoryTextView.setText(mFood.getCategory());
            }
        }

        @Override
        public void onClick(View view) {
            String[] food = new String[]{
                    mFood.getName(),
                    mFood.getBrand(),
                    mFood.getAmount(),
                    mFood.getCategory(),
                    Integer.toString(mFood.getQuantity()),
                    Integer.toString(mFood.getId())
            };

            Intent intent = FoodItemActivity.newIntent(getActivity(), food);
            startActivity(intent);
        }
    }

    /**
     * Adapter class for the RecyclerView.
     */
    private class FoodAdapter extends RecyclerView.Adapter<FoodHolder> {
        private List<Food> mFood;

        public FoodAdapter(String category) {
            setFoodList(category);
        }

        @Override
        public FoodHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.list_content, parent, false);
            return new FoodHolder(view);
        }

        @Override
        public void onBindViewHolder(FoodHolder holder, int position) {
            Food food = mFood.get(position);
            holder.bindFood(food);
        }

        @Override
        public int getItemCount() {
            return mFood.size();
        }

        public void setFoodList(String category) {
            FoodManager foodManager = FoodManager.get(getActivity());
            List<Food> foodList;

            if (category.equalsIgnoreCase(getString(R.string.drawer_menu_all))) {
                foodList = foodManager.getAll();
            } else {
                foodList = foodManager.getCategoryList(category);
            }

            mFood = foodList;
        }
    }

    /**
     * Overrides onResume() to check if returning from CategoryFragment. If so, changes to the
     * categories could have been made and the user is sent to a ContentListFragment
     * that displays the "list all" category. This category can never be renamed or removed and
     * is "safe" to return to.
     */
    @Override
    public void onResume() {
        super.onResume();
        if (((MainActivity) getActivity()).wasCategoryClicked()) {
            updateUI(getString(R.string.drawer_menu_all));
            ((MainActivity) getActivity()).setAllCategoryAsChecked();
            getActivity().setTitle(R.string.drawer_menu_all);
        } else {
            updateUI(mCategory);
        }
    }
}