package com.antergy.whatsinmyfreezer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;

/**
 * A fragment class for displaying a food item. The user is given the ability to change
 * the quantity of the food item in the fragment.
 */
public class FoodItemFragment extends Fragment {
    private final static String ARG_FOOD = "food";
    private final static String DIALOG_REMOVE = "dialogRemove";
    private final static String DIALOG_ADD = "dialogAdd";
    private final static int REQUEST_ANSWER_REMOVE = 0;
    private final static int REQUEST_ANSWER_ADD = 1;
    private String[] mFoodInfo;
    private TextView mNameTextView;
    private TextView mBrandTextView;
    private TextView mAmountTextView;
    private TextView mCategoryTextView;
    private TextView mQuantityTextView;
    private Button mRemoveButton;
    private Button mAddButton;
    private String mName;
    private String mBrand;
    private String mAmount;
    private String mCategory;
    private int mQuantity;
    private int mFoodId;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private FoodManager mFoodManager;
    private int mNewQuantity;
    private Food mFood;

    /**
     * Creates a new FoodItemFragment that will have a String array passed along with it.
     * The array is representing a Food object with the following structure:
     * food[0] => name
     * food[1] => brand
     * food[2] => amount
     * food[3] => category
     * food[4] => quantity
     * food[5] => id
     * If changes are made to the String array, setFoodVariables needs to be refactored.
     * @param food string array that contains the information of a Food object.
     * @return a new FoodItemFragment with Food data.
     */
    public static FoodItemFragment newInstance(String[] food) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FOOD, food);

        FoodItemFragment fragment = new FoodItemFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Method that sets the food variables. If any change to the String array passed along in
     * newInstance is made, only this method has to be refactored. It also retrieves the
     * represented food object.
     */
    private void setFoodVariables() {
        mName = mFoodInfo[0];
        mBrand = mFoodInfo[1];
        mAmount = mFoodInfo[2];
        mCategory = mFoodInfo[3];
        mQuantity = Integer.parseInt(mFoodInfo[4]);
        mNewQuantity = mQuantity;
        mFoodId = Integer.parseInt(mFoodInfo[5]);
        mFood = getFood();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_food_item, container, false);

        // Add graphics to the fragment
        mNameTextView = (TextView) view.findViewById(R.id.food_name_textview);
        mBrandTextView = (TextView) view.findViewById(R.id.food_brand_textview);
        mAmountTextView = (TextView) view.findViewById(R.id.food_amount_textview);
        mCategoryTextView = (TextView) view.findViewById(R.id.food_category_textview);
        mQuantityTextView = (TextView) view.findViewById(R.id.food_quantity_textview);
        mRemoveButton = (Button) view.findViewById(R.id.food_remove_button);
        mAddButton = (Button) view.findViewById(R.id.food_add_button);
        mPhotoView = (ImageView) view.findViewById(R.id.food_fragment_picture);

        mFoodManager = FoodManager.get(getActivity());

        // Configure graphics
        if (mFoodInfo == null) {
            mFoodInfo = (String[]) getArguments().getSerializable(ARG_FOOD);
        }

        setFoodVariables();
        setTextViews();
        setPhotoView();
        setRemoveButton();
        setAddButton();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        FoodItemActivity activity = (FoodItemActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeButtonEnabled(true);
        }
        return view;
    }

    /**
     * If requests have been made to the add or remove dialogs, the results are handled here.
     * The quantity of the food item is increased or reduced accordingly by calling
     * updateQuantity();
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ANSWER_ADD) {
            int quantity = (int) data.getSerializableExtra(
                    DialogAddQuantityFragment.EXTRA_ADD_QUANTITY_MESSAGE);
            if (resultCode == Activity.RESULT_OK) {
                updateQuantity(mNewQuantity + quantity);
            }
        } else if (requestCode == REQUEST_ANSWER_REMOVE) {
            int quantity = (int) data.getSerializableExtra(
                    DialogRemoveQuantityFragment.EXTRA_REMOVE_QUANTITY_MESSAGE);
            if (resultCode == Activity.RESULT_OK) {
                updateQuantity(mNewQuantity - quantity);
            }
        }
    }

    /**
     * Overrides onPause and checks new quantity. If quantity is zero, the food item is deleted
     * from the database and the photo is deleted from the internal storage. If not, the
     * food item is updated in the database with its new quantity.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mNewQuantity == 0) {
            mFoodManager.deleteFood(mFoodId);
            mFoodManager.deletePhotoFile(mPhotoFile);
        } else {
            mFood.setQuantity(mNewQuantity);
            mFoodManager.updateFood(mFoodId, mFood);
        }
    }

    /**
     * Sets the photo view to display the photo that belongs to the food item, if it
     * owns one.
     */
    private void setPhotoView() {
        mPhotoFile = mFoodManager.getPhotoFile(mFood.getPhotoFilename());

        if (!mPhotoFile.exists()) {
            mPhotoView.setImageResource(R.drawable.ic_panorama_black_48dp);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mPhotoView.getLayoutParams();
            params.width = 300;
            params.height = 300;
            mPhotoView.setLayoutParams(params);

            mPhotoView.getLayoutParams().width = 300;
            mPhotoView.getLayoutParams().height = 300;
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    /**
     * Returns the food item the fragment is displaying.
     * @return a Food object representing the food item of the fragment.
     */
    private Food getFood() {
        return new Food(mName, mQuantity, mAmount, mBrand, mFoodId, mCategory);
    }

    /**
     * Sets all the text views to display the food item information.
     */
    private void setTextViews() {
        mNameTextView.setText(mName);
        mBrandTextView.setText(mBrand);
        mAmountTextView.setText(mAmount);
        if (mCategory.equalsIgnoreCase(CategoryManager.VALUE_NO_CATEGORY)) {
            mCategoryTextView.setText("");
        } else {
            mCategoryTextView.setText(mCategory);
        }
        updateQuantity(mNewQuantity);
    }

    /**
     * Updates the quantity in the quantity text field and saves the new quantity.
     * @param quantity
     */
    private void updateQuantity(int quantity) {
        String s = Integer.toString(quantity);
        mQuantityTextView.setText(s);
        mNewQuantity = quantity;
    }

    /**
     * Sets the remove button. If the quantity is > 1, a dialog window is displayed, where the
     * user is asked to set how many to remove. If the quantity is == 0, the quantity is set to
     * zero with no dialog prompt.
     */
    private void setRemoveButton() {
        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (mNewQuantity ==  1) {
                    updateQuantity(mNewQuantity - 1);
                } else if (mNewQuantity > 1) {
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    DialogRemoveQuantityFragment dialog = DialogRemoveQuantityFragment
                            .newInstance(mNewQuantity);
                    dialog.setTargetFragment(FoodItemFragment.this, REQUEST_ANSWER_REMOVE);
                    dialog.show(manager, DIALOG_REMOVE);
                }
            }
        });
    }

    /**
     * Sets the add button. A dialog window is displayed, where the user is asked to set how many
     * to add.
     */
    private void setAddButton() {
        mAddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager manager = getActivity().getSupportFragmentManager();
                DialogAddQuantityFragment dialog = DialogAddQuantityFragment
                        .newInstance(mNewQuantity);
                dialog.setTargetFragment(FoodItemFragment.this, REQUEST_ANSWER_ADD);
                dialog.show(manager, DIALOG_ADD);
            }
        });
    }
}
