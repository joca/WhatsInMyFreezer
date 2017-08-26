package com.antergy.whatsinmyfreezer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Fragment for handling categories. The user can add, rename and remove a category.
 */
public class CategoryFragment extends Fragment {
    private final static String DIALOG_MERGE = "dialogMerge";
    private final static String DIALOG_REMOVE = "dialogRemove";
    private final static int REQUEST_ANSWER_MERGE = 0;
    private final static int REQUEST_ANSWER_REMOVE = 1;
    private EditText mAddEditText;
    private Button mAddButton;
    private Spinner mEditSpinner;
    private EditText mEditEditText;
    private Button mEditButton;
    private Spinner mRemoveSpinner;
    private Button mRemoveButton;
    private CategoryManager mCategoryManager;
    private ArrayAdapter<String> mRemoveAdapter;
    private ArrayAdapter<String> mEditAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // Add graphics to the activity
        mAddEditText = (EditText) view.findViewById(R.id.new_category_edittext);
        mAddButton = (Button) view.findViewById(R.id.new_category_button);
        mEditSpinner = (Spinner) view.findViewById(R.id.edit_category_spinner);
        mEditEditText = (EditText) view.findViewById(R.id.edit_category_edittext);
        mEditButton = (Button) view.findViewById(R.id.edit_category_button);
        mRemoveSpinner = (Spinner) view.findViewById(R.id.remove_category_spinner);
        mRemoveButton = (Button) view.findViewById(R.id.remove_category_button);
        mCategoryManager = CategoryManager.get(getActivity());
        // Configure graphics
        setNewEditText();
        setEditEditText();
        setNewButton();
        setRemoveButton();
        setEditButton();
        setRemoveSpinner();
        setEditSpinner();

        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        CategoryActivity activity = (CategoryActivity) getActivity();
        activity.setSupportActionBar(toolbar);

        if (activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            activity.getSupportActionBar().setHomeButtonEnabled(true);
        }
        return view;
    }

    /**
     * Receives request codes from when a user wants to remove an activity. The user had the
     * choice to only remove the category or the category and all its content. If the only the
     * category is removed, the food items that belongs to it will be set to have no category.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ANSWER_MERGE) {
            if (resultCode == Activity.RESULT_OK) {
                String[] names = (String[]) data.getSerializableExtra(
                        DialogConfirmMergeFragment.EXTRA_CONFIRM_MESSAGE);
                String oldName = names[0];
                String newName = names[1];

                mCategoryManager.removeCategory(oldName);

                FoodManager foodManager = FoodManager.get(getActivity());
                foodManager.updateCategory(oldName, newName);

                updateSpinners();

                String message = "<" + oldName + "> " +
                        this.getString(R.string.merged) +
                        " <" + newName + ">";
                Toast toast = Toast.makeText(getActivity().getBaseContext(),
                        message,
                        Toast.LENGTH_LONG);
                toast.show();
            }
        } else if (requestCode == REQUEST_ANSWER_REMOVE) {
            String name = (String) data.getSerializableExtra(
                    DialogRemoveCategoryFragment.EXTRA_REMOVE_CATEGORY);
            if (resultCode == Activity.RESULT_FIRST_USER) {
                mCategoryManager.removeCategory(name);

                FoodManager foodManager = FoodManager.get(getActivity());
                ArrayList<Food> foodList = (ArrayList<Food>) foodManager.getCategoryList(name);
                for (Food food : foodList) {
                    File photoFile = foodManager.getPhotoFile(food.getPhotoFilename());
                    foodManager.deletePhotoFile(photoFile);
                }
                foodManager.removeAllInCategory(name);

                updateSpinners();

                String message = "<"+name+"> "+
                        this.getString(R.string.removed_category_all);
                Toast toast = Toast.makeText(getActivity().getBaseContext(),
                        message,
                        Toast.LENGTH_LONG);
                toast.show();
                updateSpinners();
            } else if (resultCode == Activity.RESULT_FIRST_USER+1) {
                mCategoryManager.removeCategory(name);

                FoodManager foodManager = FoodManager.get(getActivity());
                foodManager.updateCategory(name, CategoryManager.VALUE_NO_CATEGORY);

                updateSpinners();

                String message = "<"+name+"> "+
                        this.getString(R.string.removed_category);
                Toast toast = Toast.makeText(getActivity().getBaseContext(),
                        message,
                        Toast.LENGTH_LONG);
                toast.show();
                updateSpinners();
            }
        }
    }

    /**
     * Sets the new category name field.
     */
    private void setNewEditText() {
        mAddEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean b) {
                String input = mAddEditText.getText().toString();
                input = input.trim();
                mAddEditText.setText(input);
            }
        });
    }

    /**
     * Sets the edit category name field
     */
    private void setEditEditText() {
        mEditEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean b) {
                String input = mEditEditText.getText().toString();
                input = input.trim();
                mEditEditText.setText(input);
            }
        });
    }

    /**
     * Sets the add a new category button. A new category is only created if the user specifies a
     * name and the name has to start with a number or letter. This to make sure the
     * key KEY_NO_CATEGORY in CategoryManager is not overwritten.
     */
    private void setNewButton() {
        mAddButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = mAddEditText.getText().toString();
                name = name.trim();
                mAddEditText.setText(name);
                if (name.isEmpty()) {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(),
                            R.string.category_forgot_name,
                            Toast.LENGTH_LONG);
                    toast.show();
                } else if (name.substring(0,1).matches("_") || name.substring(0,1).matches("\\W")) {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(),
                            R.string.alphanumeric,
                            Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    addCategory(name);
                    mAddEditText.setText("");
                }
            }
        });
    }

    /**
     * Sets the remove category button.
     */
    private void setRemoveButton() {
        mRemoveButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String name = mRemoveSpinner.getSelectedItem().toString();
                if (!name.equalsIgnoreCase(getString(R.string.no_category))) {
                    removeCategory(name);
                    mRemoveSpinner.setSelection(0);
                } else {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(),
                            R.string.category_forgot_category,
                            Toast.LENGTH_LONG);
                    toast.show();
                }
            }
        });
    }

    /**
     * Sets the edit name button. The user must specify and a category. The new name has the same
     * requirements as in the setNewButton() specification.
     */
    private void setEditButton() {
        mEditButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String oldName = mEditSpinner.getSelectedItem().toString();
                String newName = mEditEditText.getText().toString();
                newName = newName.trim();
                mEditEditText.setText(newName);
                if (newName.isEmpty()) {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(),
                            R.string.category_forgot_name,
                            Toast.LENGTH_LONG);
                    toast.show();
                } else if (newName.substring(0,1).matches("_") || newName.substring(0,1).matches("\\W")) {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(),
                            R.string.alphanumeric,
                            Toast.LENGTH_LONG);
                    toast.show();
                } else if (oldName.equalsIgnoreCase(getString(R.string.no_category))) {
                    Toast toast = Toast.makeText(getActivity().getBaseContext(),
                            R.string.category_forgot_category,
                            Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    editCategory(oldName, newName);
                    mEditEditText.setText("");
                    mEditSpinner.setSelection(0);
                }
            }
        });
    }

    /**
     * Removes a chosen category from the category list. The user is prompted with a dialog window
     * to specify if only he category should be removed or also its content.
     * @param name the category to remove.
     */
    private void removeCategory(String name) {
        FragmentManager manager = getActivity().getSupportFragmentManager();
        DialogRemoveCategoryFragment dialog = DialogRemoveCategoryFragment
                .newInstance(name);
        dialog.setTargetFragment(CategoryFragment.this, REQUEST_ANSWER_REMOVE);
        dialog.show(manager, DIALOG_REMOVE);
    }

    /**
     * Adds a new category if the chosen category name does not already exists.
     * @param name the name of the category.
     */
    private void addCategory(String name) {
        boolean added = mCategoryManager.addCategory(name);
        String message;
        if (added) {
            message = "<"+name+"> "+
                    this.getString(R.string.added);
            updateSpinners();
        } else {
            message = "<"+name+"> "+
                    this.getString(R.string.category_exists);
        }
        Toast toast = Toast.makeText(getActivity().getBaseContext(), message, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Edits the name of a category. If the name is already given to another category, the user
     * is prompted with a dialog window and asked if the two categories should be merged or not.
     * @param oldName the current name of the category.
     * @param newName the new name assigned to the category.
     */
    private void editCategory(String oldName, String newName) {
        String message;
        if (oldName.equalsIgnoreCase(newName)) {
            message = this.getString(R.string.same_name);
            Toast toast = Toast.makeText(getActivity().getBaseContext(), message, Toast.LENGTH_LONG);
            toast.show();
        } else {
            int edited = mCategoryManager.changeCategory(oldName, newName);
            if (edited > 0) {// new name assigned to category
                message = "<"+oldName+"> "+
                        this.getString(R.string.edited)+
                        " <"+newName+">";
                Toast toast = Toast.makeText(getActivity().getBaseContext(),
                        message, Toast.LENGTH_LONG);
                toast.show();
                updateSpinners();
            } else {// categories merged
                FragmentManager manager = getActivity().getSupportFragmentManager();
                DialogConfirmMergeFragment dialog = DialogConfirmMergeFragment
                        .newInstance(new String[]{oldName, newName});
                dialog.setTargetFragment(CategoryFragment.this, REQUEST_ANSWER_MERGE);
                dialog.show(manager, DIALOG_MERGE);
            }
        }
    }

    /**
     * Sets the remove categories spinner.
     */
    private void setRemoveSpinner() {
        addRemoveAdapter();
        mRemoveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Sets the edit category name spinner.
     */
    private void setEditSpinner() {
        addEditAdapter();
        mRemoveSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    /**
     * Add an adapter to the remove spinner.
     */
    private void addRemoveAdapter() {
        List<String> spinnerItems = mCategoryManager.getCategories();
        mRemoveAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                spinnerItems
        );
        mRemoveAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRemoveSpinner.setAdapter(mRemoveAdapter);
    }

    /**
     * Adds an adapter to the edit spinner.
     */
    private void addEditAdapter() {
        List<String> spinnerItems = mCategoryManager.getCategories();
        mEditAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item,
                spinnerItems
        );
        mEditAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mEditSpinner.setAdapter(mEditAdapter);
    }

    /**
     * Updates the spinners when a category has been created, renamed or deleted.
     */
    private void updateSpinners() {
        List<String> spinnerItems = mCategoryManager.getCategories();
        mRemoveAdapter.clear();
        mEditAdapter.clear();
        mRemoveAdapter.addAll(spinnerItems);
        mEditAdapter.addAll(spinnerItems);
    }
}
