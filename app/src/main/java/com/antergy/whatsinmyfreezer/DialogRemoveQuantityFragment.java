package com.antergy.whatsinmyfreezer;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

/**
 * Dialog fragment used to ask the user how many food items to remove from the freezer.
 */
public class DialogRemoveQuantityFragment extends DialogFragment {
    public final static String EXTRA_REMOVE_QUANTITY_MESSAGE =
            "com.antergy.android.whatsinmyfreezer.removeQuantity";
    private final static String ARG_CONFIRM_REMOVE = "confirmRemove";
    private NumberPicker mNumberPicker;

    public static DialogRemoveQuantityFragment newInstance(int quantity) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONFIRM_REMOVE, quantity);

        DialogRemoveQuantityFragment fragment = new DialogRemoveQuantityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a new dialog fragment with an ok button and a cancel button. It also contains a
     * number picker where the user can specify between 1 to the current quantity of food items
     * to remove.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_new_quantity, null);
        int maxValue = (int) getArguments().getSerializable(ARG_CONFIRM_REMOVE);
        mNumberPicker = (NumberPicker) view.findViewById(R.id.dialog_quantity_number_picker);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(maxValue);
        mNumberPicker.setEnabled(true);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.remove_quantity_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_CANCELED);
                            }
                        })
                .create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }
        int removedQuantity = mNumberPicker.getValue();

        Intent intent = new Intent();
        intent.putExtra(EXTRA_REMOVE_QUANTITY_MESSAGE, removedQuantity);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
