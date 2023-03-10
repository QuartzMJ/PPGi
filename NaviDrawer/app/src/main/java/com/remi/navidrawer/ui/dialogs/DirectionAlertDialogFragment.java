package com.remi.navidrawer.ui.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.remi.navidrawer.R;

public class DirectionAlertDialogFragment extends DialogFragment {
    private int mDirection = 1;
    private int DIRECTION_PORTRAIT = 1;
    private int DIRECTION_LANDSCAPE = 2;

    public interface NoticeDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int result);

        public void onDialogNegativeClick(DialogFragment dialog);
    }

    private NoticeDialogListener mNoticeDialogListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.dialog_title)
                .setSingleChoiceItems(R.array.direction, 0, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            mDirection = DIRECTION_PORTRAIT;
                        } else {
                            mDirection = DIRECTION_LANDSCAPE;
                        }
                    }
                });
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                mNoticeDialogListener.onDialogPositiveClick(DirectionAlertDialogFragment.this, mDirection);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
                mNoticeDialogListener.onDialogNegativeClick(DirectionAlertDialogFragment.this);
            }

        });

        AlertDialog dialog = builder.create();
        return dialog;
    }


    public int getDirection() {
        return mDirection;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mNoticeDialogListener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }
}

