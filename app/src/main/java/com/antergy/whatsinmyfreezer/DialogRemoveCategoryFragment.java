package com.antergy.whatsinmyfreezer;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Dialog fragment that is used to ask the user if a category should be removed and the food items
 * belonging to it have the category removed. Or if the category should be removed and all the
 * food items belonging to it will be removed as well.
 */
public class DialogRemoveCategoryFragment extends DialogFragment {
    public final static String EXTRA_REMOVE_CATEGORY =
            "com.antergy.android.whatsinmyfreezer.removeCategory";
    private final static String ARG_CONFIRM_REMOVE = "confirmRemove";

    public static DialogRemoveCategoryFragment newInstance(String name) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONFIRM_REMOVE, name);

        DialogRemoveCategoryFragment fragment = new DialogRemoveCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a dialog button with a "remove only category" button, a "remove category and content"
     * button and a cancel button.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String name = (String) getArguments().getSerializable(ARG_CONFIRM_REMOVE);
        final String title = getString(R.string.confirm_remove_title) + " <" + name + ">";
        return new AlertDialog.Builder(getActivity())
                .setTitle(title)
                .setMessage(R.string.confirm_remove_message)
                .setNeutralButton(R.string.button_category,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_FIRST_USER, name);
                            }
                        })
                .setPositiveButton(R.string.button_only_category,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_FIRST_USER+1, name);
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_CANCELED, name);
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode, String answer) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_REMOVE_CATEGORY, answer);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
