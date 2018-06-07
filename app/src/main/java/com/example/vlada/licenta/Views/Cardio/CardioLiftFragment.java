package com.example.vlada.licenta.Views.Cardio;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import com.example.vlada.licenta.Adapter.CardioLiftsRecyclerAdapter;
import com.example.vlada.licenta.Base.BaseFragment;
import com.example.vlada.licenta.Domain.Cardio;
import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.Utils;
import com.example.vlada.licenta.Views.Exercise.ExerciseListView;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

public class CardioLiftFragment extends BaseFragment {

    Exercise mExercise;
    FloatingActionButton mAddBT;
    Cardio mClickedCardio;
    TextView mNoLiftsTV;
    String mExerciseName;
    private RealmResults<Cardio> mResults;
    private RecyclerView mRecyclerView;
    private CardioLiftsRecyclerAdapter mAdapter;

    public CardioLiftFragment() {
    }

    public static CardioLiftFragment newInstance(String text) {
        CardioLiftFragment f = new CardioLiftFragment();
        Bundle b = new Bundle();
        b.putString("exercise_name", text);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_cardio_lift, container, false);

        if (getArguments() != null)
            mExerciseName = getArguments().getString("exercise_name");
        else {
            Intent intent = new Intent(getActivity(), ExerciseListView.class);
            startActivity(intent);
        }

        mExercise = (Exercise) mRealmHelper.getRealmObject(Exercise.class, "name", mExerciseName);

        mNoLiftsTV = rootView.findViewById(R.id.no_lifts_registered_cardio);
        mRecyclerView = rootView.findViewById(R.id.historyLV_cardio);
        mAddBT = rootView.findViewById(R.id.fab_cardio);
        mAddBT.setOnClickListener(v -> showCardioDialog(false));
        mNoLiftsTV.setText(R.string.no_cardio_recorded);


        this.mResults = mRealmHelper.findAllFilteredSorted(Cardio.class, "exercise_name", mExerciseName, "setDate", Sort.DESCENDING);
        populateList();
        if (mAdapter.getItemCount() == 0) {
            mNoLiftsTV.setVisibility(View.VISIBLE);
        }
        return rootView;
    }

    private void populateList() {
        mAdapter = new CardioLiftsRecyclerAdapter(mResults, getContext(), new ItemsListener(), this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void editLift(Cardio clickedLift) {
        if (clickedLift != null) {
            mClickedCardio = clickedLift;
            showCardioDialog(true);
        }
    }

    private void showCardioDialog(boolean edit) {
        DialogFragment cardioDialog = CardioDialog.newInstance(edit);
        cardioDialog.setTargetFragment(this, 0);
        if (getFragmentManager() != null) {
            cardioDialog.show(getFragmentManager(), "dialog");
        }
    }

    public void deleteLift(Cardio clickedLift) {
        if (getActivity() == null)
            return;

        if (clickedLift != null) {
            getActivity().runOnUiThread(() -> {
                deleteLiftFromRealm(clickedLift);
                updateRVList();
            });
        }
    }

    private void updateRVList() {
        mAdapter = new CardioLiftsRecyclerAdapter(mResults, getContext(), new ItemsListener(), this);
        mRecyclerView.setAdapter(mAdapter);
        if (mAdapter.getItemCount() == 0)
            mNoLiftsTV.setVisibility(View.VISIBLE);
        else mNoLiftsTV.setVisibility(View.GONE);
    }

    private void deleteLiftFromRealm(Cardio clickedCardio) {
        Realm realm;
        realm = Realm.getDefaultInstance();
        realm.executeTransaction(realm1 -> {
            RealmResults<Cardio> result = realm1.where(Cardio.class)
                    .equalTo("date_ms", clickedCardio.getDate_ms())
                    .equalTo("notes", clickedCardio.getNotes())
                    .equalTo("time_spent", clickedCardio.getTime_spent())
                    .findAll();
            result.deleteAllFromRealm();
        });
        realm.close();
    }


    private void updateCardioFromRealm(Cardio cardio1, Cardio cardio2) {
        if (getActivity() == null)
            return;
        getActivity().runOnUiThread(() -> {
            Realm realm;
            realm = Realm.getDefaultInstance();
            realm.beginTransaction();
            Cardio newCardio = realm.where(Cardio.class)
                    .equalTo("date_ms", cardio1.getDate_ms())
                    .equalTo("notes", cardio1.getNotes())
                    .equalTo("time_spent", cardio1.getTime_spent())
                    .findFirst();
            if (newCardio != null) {
                newCardio.setNotes(cardio2.getNotes());
                newCardio.setTime_spent(cardio2.getTime_spent());
                realm.commitTransaction();
                updateRVList();
                if (Utils.is1RMCardio(newCardio, mResults)) {
                    Utils.showAlertDialog(getContext(), "Congratulations", "Highest time recorded yet");
                }
            }
            realm.close();
        });

    }

    private void insertCardioFromDialog(Cardio cardio2Add) {
        if (getActivity() == null)
            return;

        if (cardio2Add != null && mExercise != null) {
            cardio2Add.setExercise(mExercise);


            getActivity().runOnUiThread(() -> {
                mRealmHelper.insert(cardio2Add);
                updateRVList();
            });

            if (Utils.is1RMCardio(cardio2Add, mResults)) {
                Utils.showAlertDialog(getContext(), "Congratulations", "Highest time recorded yet");
            }

        }
    }

    private Cardio getClickedCardio() {
        return mClickedCardio;
    }

    public static class CardioDialog extends DialogFragment {
        Button mCancelBT;
        Button mConfirmButton;
        EditText mMinutesET;
        CheckBox mNotesCB;
        EditText mNotesET;
        Cardio mLift2Edit;
        boolean mEditDialog;
        CardioLiftFragment mCardioLiftFragment;

        public static CardioDialog newInstance(boolean value) {
            CardioDialog f = new CardioDialog();

            Bundle args = new Bundle();
            args.putBoolean("edit", value);
            f.setArguments(args);
            return f;
        }


        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            assert getActivity() != null;
            View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_cardio, new LinearLayout(getActivity()), false);
            if (getArguments() != null)
                mEditDialog = getArguments().getBoolean("edit");

            mCardioLiftFragment = (CardioLiftFragment) getTargetFragment();

            mCancelBT = view.findViewById(R.id.cancelDialogBT_cardio);
            mConfirmButton = view.findViewById(R.id.confirmDialogButton_cardio);
            mConfirmButton.setText(mEditDialog ? "Edit" : "Add");
            mMinutesET = view.findViewById(R.id.minutes);
            mNotesCB = view.findViewById(R.id.notesCB_cardio);
            mNotesET = view.findViewById(R.id.notes_cardio);
            mLift2Edit = (mCardioLiftFragment.getClickedCardio());

            Dialog builder = new Dialog(getActivity());
            if (builder.getWindow() != null) {
                builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
                builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#E0E0E0")));
                builder.setContentView(view);
            }
            if (mLift2Edit != null && mEditDialog) {
                mNotesET.setText(mLift2Edit.getNotes());
                mMinutesET.setText(String.valueOf(mLift2Edit.getTime_spent()));
                mNotesCB.setChecked(mLift2Edit.getNotes().length() > 0);
                mNotesET.setVisibility(mLift2Edit.getNotes().length() > 0 ? View.VISIBLE : View.GONE);
            }
            listeners();
            return builder;
        }

        private void listeners() {
            mConfirmButton.setOnClickListener(v -> {
                if (!mEditDialog) {
                    Cardio cardio2Add = new Cardio();
                    cardio2Add.setNotes(mNotesET.getText().toString().length() > 0 && mNotesCB.isChecked() ? mNotesET.getText().toString() : "");
                    cardio2Add.setTime_spent(mMinutesET.getText().toString().length() > 0 ? Integer.parseInt(mMinutesET.getText().toString()) : 0);
                    cardio2Add.setSetDate(new Date());
                    dismissDialog();
                    if (cardio2Add.getTime_spent() > 0)
                        mCardioLiftFragment.insertCardioFromDialog(cardio2Add);
                    else
                        Utils.displayToast(getContext(), "Number of minutes should be higher than 0");
                } else {
                    Cardio newLift = new Cardio();
                    newLift.setSetDate(mLift2Edit.getSetDate());
                    newLift.setNotes(mNotesET.getText().toString().length() > 0 && mNotesCB.isChecked() ? mNotesET.getText().toString() : "");
                    newLift.setTime_spent(mMinutesET.getText().length() > 0 ? Integer.parseInt(mMinutesET.getText() + "") : 0);
                    dismissDialog();
                    mCardioLiftFragment.updateCardioFromRealm(mLift2Edit, newLift);
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
            CardioDialog.this.getDialog().cancel();
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
