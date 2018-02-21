package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
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
import com.example.vlada.licenta.Utils.Utils;

import java.util.Date;

import io.realm.Case;
import io.realm.Realm;
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
    Toolbar mToolbar;

    private Realm mRealm;
    private RealmResults<Lift> mResults;
    private RecyclerView mRecyclerView;
    private AdapterLiftRecycler mAdapter;

    private String exercise_name;

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

        exercise_name = getArguments().getString("exercise_name");

        View rootView = inflater.inflate(R.layout.fragment_exercise_lift, container, false);

        mExercise = new Exercise();

        mToolbar = rootView.findViewById(R.id.my_toolbar);
        mToolbar.setTitle(getArguments().getString("exercise_name"));
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);
        mWeightET = rootView.findViewById(R.id.weightET);
        mRepsET = rootView.findViewById(R.id.repsET);
        mAddBT = rootView.findViewById(R.id.addSetButton);
        mRecyclerView = rootView.findViewById(R.id.historyLV);

        this.mRealm = Realm.getDefaultInstance();
        mExercise = mRealm.where(Exercise.class).equalTo("name", exercise_name).findFirst();

        this.mResults = mRealm.where(Lift.class)
                .contains("exercise_name", exercise_name, Case.INSENSITIVE)
                .findAll()
                .sort("setDate", Sort.DESCENDING);


        populateList();
        addButtonClickListener();

        return rootView;
    }

    void addButtonClickListener() {
        mAddBT.setOnClickListener(v -> {
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
            try (Realm r = Realm.getDefaultInstance()) {
                r.executeTransaction(realm -> {
                    realm.insertOrUpdate(lift);
                    mAdapter.notifyDataSetChanged();
                });
                Utils.displayToast(getContext(), "Successfully added");
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
                //Remove swiped item from list and notify the RecyclerView
                final int position = viewHolder.getAdapterPosition();
                try (Realm r = Realm.getDefaultInstance()) {
                    r.executeTransaction(realm -> {
                        mResults.deleteFromRealm(position);
                        mAdapter.notifyDataSetChanged();
                        Utils.displayToast(getContext(), "Successfully deleted");
                    });

                }
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mRealm != null)
            mRealm.close();
    }

    class ItemsListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
            Lift itemTouched = mResults.get(mRecyclerView.getChildAdapterPosition(view));
            Utils.displayToast(getContext(), "clicked");
        }
    }

}
