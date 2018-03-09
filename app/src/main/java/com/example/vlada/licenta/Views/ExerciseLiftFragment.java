package com.example.vlada.licenta.Views;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.example.vlada.licenta.Base.BaseFragment;
import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.AdapterLiftRecycler;
import com.example.vlada.licenta.Utils.Utils;

import java.util.Comparator;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by andrei-valentin.vlad on 2/9/2018.
 */

public class ExerciseLiftFragment extends BaseFragment {

    Exercise mExercise;
    FloatingActionButton mAddBT;
    Lift mClickedLift;

    String mExerciseName;
    private RealmResults<Lift> mResults;
    private RecyclerView mRecyclerView;
    private AdapterLiftRecycler mAdapter;

    public ExerciseLiftFragment() {

    }

    public static ExerciseLiftFragment newInstance(String text) {
        ExerciseLiftFragment f = new ExerciseLiftFragment();
        Bundle b = new Bundle();
        b.putString("exercise_name", text);
        f.setArguments(b);
        return f;
    }

    public void deleteLift(Lift clickedLift) {
        if (clickedLift != null) {
            deleteLiftFromRealm(clickedLift);
        }
    }

    private void deleteLiftFromRealm(Lift lift) {
        Realm realm;
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            RealmResults<Lift> result = realm1.where(Lift.class)
                    .equalTo("notes", lift.getNotes())
                    .equalTo("reps", lift.getReps())
                    .equalTo("weight", lift.getWeight())
                    .equalTo("date_ms", lift.getDate_ms())
                    .equalTo("exercise_name", lift.getExercise_name())
                    .findAll();
            result.deleteAllFromRealm();
        });
        realm.close();
    }

    public void editLift(Lift clickedLift) {
        if (clickedLift != null) {
            mClickedLift = clickedLift;
            showLiftDialog(true);
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_exercise_lift, container, false);

        if (getArguments() != null)
            mExerciseName = getArguments().getString("exercise_name");
        mExercise = (Exercise) mRealmHelper.getRealmObject(Exercise.class, "name", mExerciseName);

        mAddBT = rootView.findViewById(R.id.fab);
        mAddBT.setOnClickListener(v -> showLiftDialog(false));

        mRecyclerView = rootView.findViewById(R.id.historyLV);
        this.mResults = mRealmHelper.findAllFilteredSorted(Lift.class, "exercise_name", mExerciseName, "setDate", Sort.DESCENDING);
        populateList();
        return rootView;
    }

    public void showLiftDialog(boolean edit) {
        DialogFragment liftDialog = LiftDialog.newInstance(edit);
        liftDialog.setTargetFragment(this, 0);
        if (getFragmentManager() != null) {
            liftDialog.show(getFragmentManager(), "dialog");
        } else {
            Utils.displayToast(getContext(), "Error");
        }
    }


    private void updateLiftFromRealm(Lift originalLift, Lift newLift) {
        deleteLiftFromRealm(originalLift);
        mRealmHelper.insert(newLift);
        updateRVList();
        Utils.displayToast(getContext(), "Successfully updated");

    }

    public void insertLiftFromDialog(Lift lift) {
        if (lift != null && mExercise != null) {
            float highest1RMPreAdd = mResults
                    .stream()
                    .map(Utils::getEstimated1RM)
                    .max(Comparator.naturalOrder())
                    .orElse(0f);
            lift.setExercise(mExercise);
            mRealmHelper.insert(lift);
            updateRVList();
            Utils.displayToast(getContext(), "Successfully added");

            if (highest1RMPreAdd < Utils.getEstimated1RM(lift)) {
                Utils.showAlertDialog(getContext(), "Congratulations", "New strength record");
            }
        }

    }

    void populateList() {
        mAdapter = new AdapterLiftRecycler(mResults, getContext(), new ItemsListener(), this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mRecyclerView.setAdapter(mAdapter);
    }


    public void updateRVList() {
        mAdapter = new AdapterLiftRecycler(mResults, getContext(), new ItemsListener(), this);
        mRecyclerView.setAdapter(mAdapter);
    }

    public Lift getClickedLift() {
        return mClickedLift;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public static class LiftDialog extends DialogFragment {
        Button mCancelBT;
        Button mConfirmButton;
        EditText mWeightET;
        EditText mRepsET;
        CheckBox mNotesCB;
        EditText mNotesET;
        Lift mLift2Add;
        Lift mLift2Edit;
        boolean mEditDialog;
        ExerciseLiftFragment mExerciseLiftFragment;

        public static LiftDialog newInstance(boolean value) {
            LiftDialog f = new LiftDialog();

            Bundle args = new Bundle();
            args.putBoolean("edit", value);
            f.setArguments(args);
            return f;
        }


        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            assert getActivity() != null;
            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_lift, new LinearLayout(getActivity()), false);
            if (getArguments() != null)
                mEditDialog = getArguments().getBoolean("edit");

            mExerciseLiftFragment = (ExerciseLiftFragment) getTargetFragment();

            mCancelBT = view.findViewById(R.id.cancelDialogBT);
            mConfirmButton = view.findViewById(R.id.confirmDialogButton);
            mConfirmButton.setText(mEditDialog ? "Edit" : "Add");
            mWeightET = view.findViewById(R.id.weight);
            mRepsET = view.findViewById(R.id.reps);
            mNotesCB = view.findViewById(R.id.notesCB);
            mNotesET = view.findViewById(R.id.notes);
            mLift2Add = new Lift();
            mLift2Edit = (mExerciseLiftFragment.getClickedLift());

            Dialog builder = new Dialog(getActivity());
            if (builder.getWindow() != null) {
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E0E0E0")));
                builder.setContentView(view);
            }
            if (mLift2Edit != null && mEditDialog) {
                mNotesET.setText(mLift2Edit.getNotes());
                mRepsET.setText(String.valueOf(mLift2Edit.getReps()));
                mWeightET.setText(String.valueOf(mLift2Edit.getWeight()));
                mNotesCB.setChecked(mLift2Edit.getNotes().length() > 0);
                mNotesET.setVisibility(mLift2Edit.getNotes().length() > 0 ? View.VISIBLE : View.GONE);
            }
            listeners();
            return builder;
        }

        private void listeners() {
            mConfirmButton.setOnClickListener(v -> {
                if (!mEditDialog) {
                    mLift2Add.setNotes(mNotesET.getText().toString().length() > 0 && mNotesCB.isChecked() ? mNotesET.getText().toString() : "");
                    mLift2Add.setReps(mRepsET.getText().toString().length() > 0 ? Integer.parseInt(mRepsET.getText().toString()) : 0);
                    mLift2Add.setWeight(mWeightET.getText().toString().length() > 0 ? Integer.parseInt(mWeightET.getText().toString()) : 0);
                    mLift2Add.setSetDate(new Date());
                    dismissDialog();
                    mExerciseLiftFragment.insertLiftFromDialog(mLift2Add);
                } else {
                    Lift newLift = new Lift();
                    newLift.setSetDate(mLift2Edit.getSetDate());
                    newLift.setNotes(mNotesET.getText().toString().length() > 0 && mNotesCB.isChecked() ? mNotesET.getText().toString() : "");
                    newLift.setWeight(Integer.parseInt(mWeightET.getText() + ""));
                    newLift.setReps(Integer.parseInt(mRepsET.getText() + ""));
                    dismissDialog();
                    mExerciseLiftFragment.updateLiftFromRealm(mLift2Edit, newLift);
                }
            });
            mCancelBT.setOnClickListener(v -> dismissDialog());
            mNotesCB.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    mNotesET.setVisibility(View.VISIBLE);
                } else {
                    mNotesET.setVisibility(View.GONE);
                }

            });

        }

        private void dismissDialog() {
            LiftDialog.this.getDialog().cancel();
        }
    }

    class ItemsListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
//            Lift itemTouched = mResults.get(mRecyclerView.getChildAdapterPosition(view));
//            Utils.displayToast(getContext(), "clicked");
        }
    }


}

