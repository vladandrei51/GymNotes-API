package com.example.vlada.licenta.Views;

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
import android.widget.AdapterView;

import com.example.vlada.licenta.Base.BaseFragment;
import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.AdapterLiftRecycler;
import com.example.vlada.licenta.Utils.Utils;

import java.util.Comparator;

import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by andrei-valentin.vlad on 2/9/2018.
 */

public class ExerciseLiftFragment extends BaseFragment {

    Exercise mExercise;
    FloatingActionButton mAddBT;

    String mExerciseName;
    DialogFragment mDialog;
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


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_exercise_lift, container, false);

        mExerciseName = getArguments().getString("exercise_name");
        mAddBT = rootView.findViewById(R.id.fab);
        mRecyclerView = rootView.findViewById(R.id.historyLV);

        mExercise = (Exercise) mRealmHelper.getRealmObject(Exercise.class, "name", mExerciseName);
        this.mResults = mRealmHelper.findAllFilteredSorted(Lift.class, "exercise_name", mExerciseName, "setDate", Sort.DESCENDING);
        populateList();
        addButtonClickListener();

        return rootView;
    }

    public void showAddLiftDialog() {
        // Create an instance of the dialog fragment and show it
        mDialog = new AddLiftDialog();
        mDialog.setTargetFragment(this, 0);
        if (getFragmentManager() != null) {
            mDialog.show(getFragmentManager(), "addLiftDialog");
        } else {
            Utils.displayToast(getContext(), "Error");
        }
    }


    void addButtonClickListener() {
        mAddBT.setOnClickListener(v -> {
            showAddLiftDialog();
        });

    }

    public void insertLiftFromDialog(Lift lift) {
        if (lift != null) {
            float highest1RMPreAdd = mResults
                    .stream()
                    .map(Utils::getEstimated1RM)
                    .max(Comparator.naturalOrder())
                    .orElse(0f);
            if (mExercise.getId() > 0) lift.setExercise(mExercise);

            mRealmHelper.insert(lift);
            updateList();
            Utils.displayToast(getContext(), "Successfully added");

            if (highest1RMPreAdd < Utils.getEstimated1RM(lift)) {
                Utils.showAlertDialog(getContext(), "Congratulations", "New strength record");
            }
        }

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
    }


    public void updateList() {
        mAdapter = new AdapterLiftRecycler(mResults, getContext(), new ItemsListener());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    class ItemsListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
//            Lift itemTouched = mResults.get(mRecyclerView.getChildAdapterPosition(view));
//            Utils.displayToast(getContext(), "clicked");
        }
    }

}

