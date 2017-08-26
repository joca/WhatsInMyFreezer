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
 * Dialog fragment used to ask the user how many food items to add to the freezer.
 */
public class DialogAddQuantityFragment extends DialogFragment {
    public final static String EXTRA_ADD_QUANTITY_MESSAGE =
            "com.antergy.android.whatsinmyfreezer.addQuantity";
    private final static String ARG_CONFIRM_ADD = "confirmAdd";
    private NumberPicker mNumberPicker;

    public static DialogAddQuantityFragment newInstance(int quantity) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONFIRM_ADD, quantity);

        DialogAddQuantityFragment fragment = new DialogAddQuantityFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a new dialog fragment with an ok button and a cancel button. It also contains a
     * number picker where the user can specify between 1 to 100 food items to add.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_new_quantity, null);
        mNumberPicker = (NumberPicker) view.findViewById(R.id.dialog_quantity_number_picker);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(100);
        mNumberPicker.setEnabled(true);

        return new AlertDialog.Builder(getActivity())
                .setView(view)
                .setTitle(R.string.add_quantity_title)
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
        int addedQuantity = mNumberPicker.getValue();

        Intent intent = new Intent();
        intent.putExtra(EXTRA_ADD_QUANTITY_MESSAGE, addedQuantity);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
