package com.antergy.whatsinmyfreezer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.List;

/**
 * Fragment class for adding new food items.
 */
public class AddFragment extends Fragment {
    private final static String KEY_KEEP_PHOTO = "keepPhoto";
    private final static int REQUEST_PHOTO = 0;
    private EditText mNameEditText;
    private EditText mBrandEditText;
    private EditText mAmountEditText;
    private Spinner mUnitSpinner;
    private NumberPicker mQuantityNumberPicker;
    private Button mAddButton;
    private Spinner mCategorySpinner;
    private Button mClearButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private FoodManager mFoodManager;
    private boolean mIsPhotoTaken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Add graphics to the activity
        mNameEditText = (EditText) view.findViewById(R.id.set_name_edittext);
        mBrandEditText = (EditText) view.findViewById(R.id.set_brand_edittext);
        mAmountEditText = (EditText) view.findViewById(R.id.set_amount_edittext);
        mUnitSpinner = (Spinner) view.findViewById(R.id.set_unit_spinner);
        mQuantityNumberPicker = (NumberPicker) view.findViewById(R.id.set_quantity_number_picker);
        mAddButton = (Button) view.findViewById(R.id.add_product_button);
        mCategorySpinner = (Spinner) view.findViewById(R.id.set_category_spinner);
        mClearButton = (Button) view.findViewById(R.id.clear_product_button);
        mPhotoButton = (ImageButton) view.findViewById(R.id.add_picture_button);
        mPhotoView = (ImageView) view.findViewById(R.id.food_picture);
        mFoodManager = FoodManager.get(getActivity());
        // Configure graphics
        setNameEditText();
        setBrandEditText();
        setUnitSpinner();
        setQuantityNumberPicker();
        setAddButton();
        setCategorySpinner();
        setClearButton();
        setPhotoManager();

        // Handles screen rotation so the photo is not removed.
        if (savedInstanceState != null) {
            if (!savedInstanceState.getBoolean(KEY_KEEP_PHOTO)) {
                mPhotoFile = mFoodManager.getTempPhotoFile();
                mIsPhotoTaken = false;
            } else {
                mPhotoFile = mFoodManager.getSavedTempPhotoFile();
                mIsPhotoTaken = true;
            }
        } else {
            mIsPhotoTaken = false;
        }

        updatePhotoView();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        AddActivity activity = (AddActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null){
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeButtonEnabled(true);
        }

        return view;
    }

    /**
     * Result from camera.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO) {
            if (resultCode == getActivity().RESULT_OK) {
                mIsPhotoTaken = true;
                updatePhotoView();
            }
        }
    }

    /**
     * Enables the user to take a photo of the food item.
     */
    private void setPhotoManager() {
        mPhotoFile = mFoodManager.getTempPhotoFile();
        PackageManager packageManager = getActivity().getPackageManager();
        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null &&
                captureImage.resolveActivity(packageManager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            Uri uri = Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
    }

    /**
     * Updates the imageView displaying a photo. If no photo is taken, an icon is set to the
     * imageView to show where the photo will be.
     */
    private void updatePhotoView() {
        if (mPhotoView == null || !mPhotoFile.exists()) {
            mPhotoView.setImageResource(R.drawable.ic_panorama_black_48dp);
        } else {
            mPhotoView.setImageDrawable(null);
            Bitmap bitmap = PictureUtils.getScaledBitmap(
                    mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
        }
    }

    /**
     * Handles the input of the name field. Removes any whitespaces before and after a name.
     */
    private void setNameEditText() {
        mNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean b) {
                String input = mNameEditText.getText().toString();
                input = input.trim();
                mNameEditText.setText(input);
            }
        });
    }

    /**
     * Handles the input of the brand field. Removes any whitespaces before and after a brand.
     */
    private void setBrandEditText() {
        mBrandEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean b) {
                String input = mBrandEditText.getText().toString();
                input = input.trim();
                mBrandEditText.setText(input);
            }
        });
    }

    /**
     * Sets the add button. A new food item can only be stored if the user specified a name to it.
     */
    private void setAddButton() {
        mAddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = mNameEditText.getText().toString();
                name = name.trim();
                mNameEditText.setText(name);

                String brand = mBrandEditText.getText().toString();
                brand = brand.trim();
                mBrandEditText.setText(brand);

                Editable amount = mAmountEditText.getText();
                String amountWithUnit = "";
                if (amount.length() > 0) {
                    amountWithUnit = amount.toString() +
                            " " +
                            mUnitSpinner.getSelectedItem().toString();
                }

                String category = mCategorySpinner.getSelectedItem().toString();
                if (category.equalsIgnoreCase(getString(R.string.no_category))) {
                    category = CategoryManager.VALUE_NO_CATEGORY;
                }

                int quantity = mQuantityNumberPicker.getValue();

                // Name is required field
                if (name.isEmpty()) {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(),
                            R.string.add_forgot_name,
                            Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    Food food = mFoodManager.addFood(name, quantity,
                            amountWithUnit, brand, category);
                    mFoodManager.renamePhotoFile(food.getPhotoFilename(), mPhotoFile);//rename photo
                    mPhotoFile = mFoodManager.getTempPhotoFile();//create new temp
                    String message = name + " " + getText(R.string.added);
                    Toast toast = Toast.makeText(getActivity().getBaseContext(), message ,Toast.LENGTH_LONG);
                    toast.show();
                    clearFields();
                }
            }
        });
    }

    /**
     * Sets the clear button. It is used to quickly clear all the input fields.
     */
    private void setClearButton() {
        mClearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearFields();
            }
        });
    }

    /**
     * Clears all the fields that the user have changed.
     */
    private void clearFields() {
        mNameEditText.setText("");
        mBrandEditText.setText("");
        mAmountEditText.setText("");
        mCategorySpinner.setSelection(0);
        mUnitSpinner.setSelection(0);
        mQuantityNumberPicker.setValue(1);
        mFoodManager.deleteTempPhotoFile();
        updatePhotoView();
        mIsPhotoTaken = false;
    }

    /**
     * Spinner for choosing a unit.
     */
    private void setUnitSpinner() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getActivity(),
                R.array.units_spinner_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mUnitSpinner.setAdapter(adapter);
        mUnitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {}

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Spinner for choosing a category.
     */
    private void setCategorySpinner() {
        addCategoryAdapter();
        mCategorySpinner.setSelection(0);
        mCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {}
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    /**
     * Adapter for category spinner. The spinner items are dynamically set by the user.
     */
    private void addCategoryAdapter() {
        CategoryManager categoryManager = CategoryManager.get(getActivity());
        List<String> spinnerItems = categoryManager.getCategories();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                spinnerItems
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(categoryAdapter);
    }

    /**
     * Number picker for number of packages (quantity). The user can choose to
     * add 1 up to 100 of same food package at the same time.
     */
    private void setQuantityNumberPicker() {
        mQuantityNumberPicker.setMinValue(1);
        mQuantityNumberPicker.setMaxValue(100);
        mQuantityNumberPicker.setEnabled(true);
    }

    /**
     * Overrides the onStop() method so the temporary photo file is deleted when the user leaves
     * the fragment.
     */
    @Override
    public void onStop() {
        super.onStop();
        mFoodManager.deleteTempPhotoFile();
    }

    /**
     * Is used to save the temporary photo file before onStop() is called. This because the photo
     * should not be deleted if the user rotates the screen.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (mIsPhotoTaken) {
            mFoodManager.saveTempPhotoFile(mPhotoFile);
        }
        savedInstanceState.putBoolean(KEY_KEEP_PHOTO, mIsPhotoTaken);
    }
}
