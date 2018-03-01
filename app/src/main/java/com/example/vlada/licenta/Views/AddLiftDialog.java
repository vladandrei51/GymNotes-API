package com.example.vlada.licenta.Views;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;

import java.util.Date;

public class AddLiftDialog extends DialogFragment {
    Button mCancelBT;
    Button mAddBT;
    EditText mWeightET;
    EditText mRepsET;
    CheckBox mNotesCB;
    EditText mNotesET;
    Lift mLift;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_add_lift, new LinearLayout(getActivity()), false);

        mCancelBT = view.findViewById(R.id.cancelDialogBT);
        mAddBT = view.findViewById(R.id.addDialogBT);
        mWeightET = view.findViewById(R.id.weight);
        mRepsET = view.findViewById(R.id.reps);
        mNotesCB = view.findViewById(R.id.notesCB);
        mNotesET = view.findViewById(R.id.notes);
        mLift = new Lift();

        Dialog builder = new Dialog(getActivity());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E0E0E0")));
        builder.setContentView(view);
        listeners();
        return builder;
    }

    private void listeners() {
        mAddBT.setOnClickListener(v -> {
            mLift.setNotes(mNotesET.getText().toString().length() > 0 && mNotesCB.isChecked() ? mNotesET.getText().toString() : "");
            mLift.setReps(mRepsET.getText().toString().length() > 0 ? Integer.parseInt(mRepsET.getText().toString()) : 0);
            mLift.setWeight(mWeightET.getText().toString().length() > 0 ? Integer.parseInt(mWeightET.getText().toString()) : 0);
            mLift.setSetDate(new Date());
            dismissDialog();
            ExerciseLiftFragment exerciseLiftFragment = (ExerciseLiftFragment) getTargetFragment();
            if (exerciseLiftFragment != null) {
                exerciseLiftFragment.insertLiftFromDialog(mLift);
            }
        });
        mCancelBT.setOnClickListener(v -> {
            dismissDialog();
        });
        mNotesCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                mNotesET.setVisibility(View.VISIBLE);
            } else {
                mNotesET.setVisibility(View.GONE);
            }

        });

    }

    private void dismissDialog() {
        AddLiftDialog.this.getDialog().cancel();
    }
}
