package com.antergy.whatsinmyfreezer;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

/**
 * Dialog fragment used to confirm if two categories should be merged.
 */
public class DialogConfirmMergeFragment extends DialogFragment {
    public final static String EXTRA_CONFIRM_MESSAGE =
            "com.antergy.android.whatsinmyfreezer.merge";
    private final static String ARG_CONFIRM_MERGE = "confirmMessage";

    public static DialogConfirmMergeFragment newInstance(String[] names) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CONFIRM_MERGE, names);

        DialogConfirmMergeFragment fragment = new DialogConfirmMergeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Creates a new dialog fragment with an ok button and a cancel button.
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final String[] names = (String[]) getArguments().getSerializable(ARG_CONFIRM_MERGE);
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.confirm_merge_title)
                .setMessage(R.string.confirm_merge_message)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK, names);
                            }

                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_CANCELED, names);
                            }
                })
                .create();
    }

    private void sendResult(int resultCode, String[] answer) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_CONFIRM_MESSAGE, answer);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }
}
