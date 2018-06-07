package com.example.vlada.licenta.Views.Exercise;

import android.app.Dialog;
import android.content.Intent;
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
import android.widget.TextView;

import com.example.vlada.licenta.Adapter.ExerciseLiftsRecyclerAdapter;
import com.example.vlada.licenta.Base.BaseFragment;
import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.Utils;

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
    TextView mNoLiftsTV;

    String mExerciseName;
    private RealmResults<Lift> mResults;
    private RecyclerView mRecyclerView;
    private ExerciseLiftsRecyclerAdapter mAdapter;

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
        if (getActivity() == null)
            return;

        if (clickedLift != null) {
            getActivity().runOnUiThread(() -> {
                deleteLiftFromRealm(clickedLift);
                updateRVList();
            });
        }
    }

    private void deleteLiftFromRealm(Lift lift) {
        Realm realm;
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            RealmResults<Lift> result = realm1.where(Lift.class)
                    .equalTo("date_ms", lift.getDate_ms())
                    .equalTo("notes", lift.getNotes())
                    .equalTo("reps", lift.getReps())
                    .equalTo("weight", lift.getWeight())
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
        else {
            Intent intent = new Intent(getActivity(), ExerciseListView.class);
            startActivity(intent);
        }

        mExercise = (Exercise) mRealmHelper.getRealmObject(Exercise.class, "name", mExerciseName);

        mAddBT = rootView.findViewById(R.id.fab);
        mAddBT.setOnClickListener(v -> showLiftDialog(false));

        mNoLiftsTV = rootView.findViewById(R.id.no_lifts_registered);
        mNoLiftsTV.setText(R.string.no_lifts_added);

        mRecyclerView = rootView.findViewById(R.id.historyLV);
        this.mResults = mRealmHelper.findAllFilteredSorted(Lift.class, "exercise_name", mExerciseName, "setDate", Sort.DESCENDING);
        populateList();

        if (mAdapter.getItemCount() == 0) {
            mNoLiftsTV.setVisibility(View.VISIBLE);
        }

        return rootView;
    }

    public void showLiftDialog(boolean edit) {
        DialogFragment liftDialog = LiftDialog.newInstance(edit);
        liftDialog.setTargetFragment(this, 0);
        if (getFragmentManager() != null) {
            liftDialog.show(getFragmentManager(), "dialog");
        }
    }


    private void updateLiftFromRealm(Lift lift1, Lift lift2) {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(() -> {

            Realm realm;
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            Lift newLift = realm.where(Lift.class)
                    .equalTo("date_ms", lift1.getDate_ms())
                    .equalTo("notes", lift1.getNotes())
                    .equalTo("reps", lift1.getReps())
                    .equalTo("weight", lift1.getWeight())
                    .findFirst();
            if (newLift != null) {
                newLift.setNotes(lift2.getNotes());
                newLift.setReps(lift2.getReps());
                newLift.setWeight(lift2.getWeight());
                realm.commitTransaction();
                updateRVList();
                if (Utils.is1RM(newLift, mResults)) {
                    Utils.showAlertDialog(getContext(), "Congratulations", "New strength record");
                }
            }
            realm.close();
        });
    }

    public void insertLiftFromDialog(Lift lift) {
        if (getActivity() == null)
            return;

        if (lift != null && mExercise != null) {
            lift.setExercise(mExercise);


            getActivity().runOnUiThread(() -> {
                mRealmHelper.insert(lift);
                updateRVList();
            });

            if (Utils.is1RM(lift, mResults)) {
                Utils.showAlertDialog(getContext(), "Congratulations", "New strength record");
            }

        }

    }

    private void populateList() {
        mAdapter = new ExerciseLiftsRecyclerAdapter(mResults, getContext(), new ItemsListener(), this);
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
        mAdapter = new ExerciseLiftsRecyclerAdapter(mResults, getContext(), new ItemsListener(), this);
        mRecyclerView.setAdapter(mAdapter);
        if (mAdapter.getItemCount() > 0) mNoLiftsTV.setVisibility(View.GONE);
        else mNoLiftsTV.setVisibility(View.VISIBLE);
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
                    Lift lift2Add = new Lift();
                    lift2Add.setNotes(mNotesET.getText().toString().length() > 0 && mNotesCB.isChecked() ? mNotesET.getText().toString() : "");
                    lift2Add.setReps(mRepsET.getText().toString().length() > 0 ? Integer.parseInt(mRepsET.getText().toString()) : 0);
                    lift2Add.setWeight(mWeightET.getText().toString().length() > 0 ? Integer.parseInt(mWeightET.getText().toString()) : 0);
                    lift2Add.setSetDate(new Date());
                    dismissDialog();
                    mExerciseLiftFragment.insertLiftFromDialog(lift2Add);
                } else {
                    Lift newLift = new Lift();
                    newLift.setSetDate(mLift2Edit.getSetDate());
                    newLift.setNotes(mNotesET.getText().toString().length() > 0 && mNotesCB.isChecked() ? mNotesET.getText().toString() : "");
                    newLift.setWeight(mWeightET.getText().length() > 0 ? Integer.parseInt(mWeightET.getText() + "") : 0);
                    newLift.setReps(mRepsET.getText().length() > 0 ? Integer.parseInt(mRepsET.getText() + "") : 0);
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

