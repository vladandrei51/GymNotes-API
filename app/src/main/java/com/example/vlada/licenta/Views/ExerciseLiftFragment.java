package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.AdapterLiftRecycler;
import com.example.vlada.licenta.Utils.RealmHelper;
import com.example.vlada.licenta.Utils.Utils;

import java.util.Comparator;
import java.util.Date;

import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by andrei-valentin.vlad on 2/9/2018.
 */

public class ExerciseLiftFragment extends Fragment {

    Exercise mExercise;
    EditText mWeightET;
    EditText mRepsET;
    Button mAddBT;

    String mExerciseName;


    private RealmResults<Lift> mResults;
    private RecyclerView mRecyclerView;
    private AdapterLiftRecycler mAdapter;
    private RealmHelper mRealmHelper;

    public ExerciseLiftFragment() {

    }

    public static ExerciseLiftFragment newInstance(String text) {

        ExerciseLiftFragment f = new ExerciseLiftFragment();
        Bundle b = new Bundle();
        b.putString("exercise_name", text);
        f.setArguments(b);

        return f;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_exercise_lift, container, false);

        mExerciseName = getArguments().getString("exercise_name");

        mRealmHelper = new RealmHelper();

        mWeightET = rootView.findViewById(R.id.weightET);
        mRepsET = rootView.findViewById(R.id.repsET);
        mAddBT = rootView.findViewById(R.id.addSetButton);
        mRecyclerView = rootView.findViewById(R.id.historyLV);


        mExercise = (Exercise) mRealmHelper.getRealmObject(Exercise.class, "name", mExerciseName);
        this.mResults = mRealmHelper.findAllFilteredSorted(Lift.class, "exercise_name", mExerciseName, "setDate", Sort.DESCENDING);

        populateList();
        addButtonClickListener();

        return rootView;
    }

    void addButtonClickListener() {
        mAddBT.setOnClickListener(v -> {

            float highest1RMPreAdd = mRealmHelper.findAllFiltered(Lift.class, "exercise_name", mExerciseName)
                    .stream().map(Utils::getEstimated1RM).max(Comparator.naturalOrder()).orElse(null);


            Lift lift = new Lift();
            lift.setNotes(null);
            lift.setReps(0);
            lift.setWeight(0);
            if (mRepsET.getText().toString().length() > 0) {
                lift.setReps(Integer.parseInt(mRepsET.getText().toString()));
            }
            if (mWeightET.getText().toString().length() > 0) {
                lift.setWeight(Integer.parseInt(mWeightET.getText().toString()));
            }
            lift.setSetDate(new Date());

            if (mExercise.getId() > 0) lift.setExercise(mExercise);

            mRealmHelper.insert(lift);
            mAdapter.notifyDataSetChanged();
            Utils.displayToast(getContext(), "Successfully added");

            if (highest1RMPreAdd < Utils.getEstimated1RM(lift)) {
                Utils.showAlertDialog(getContext(), "Congratulations", "New strength record");
            }
        });

    }

    void populateList() {


        mAdapter = new AdapterLiftRecycler(mResults, getContext(), new ItemsListener());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        DividerItemDecoration mDividerItemDecoration = new DividerItemDecoration(
                mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        mRecyclerView.addItemDecoration(mDividerItemDecoration);
        mRecyclerView.setAdapter(mAdapter);
        SwipeToDelete();
    }

    void SwipeToDelete() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                /* Remove swiped item from list and notify the RecyclerView */
                mRealmHelper.deleteAtPosition(mResults, viewHolder.getAdapterPosition());
                mAdapter.notifyDataSetChanged();
                Utils.displayToast(getContext(), "Successfully deleted");
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRealmHelper.closeRealm();
    }

    class ItemsListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
            Lift itemTouched = mResults.get(mRecyclerView.getChildAdapterPosition(view));
            Utils.displayToast(getContext(), "clicked");
        }
    }

}
