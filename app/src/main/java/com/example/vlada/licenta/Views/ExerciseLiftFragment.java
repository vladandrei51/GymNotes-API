package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.AdapterItemsRecycler;
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

    Exercise exercise;
    EditText weightET;
    EditText repsET;
    Button addBT;

    private Realm realm;
    private RealmResults<Lift> results;
    private RecyclerView recyclerView;
    private AdapterItemsRecycler adapterItemsRecycler;

    private DividerItemDecoration mDividerItemDecoration;

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

        if (getArguments().getString("exercise_name") == null)
            return null;


        View rootView = inflater.inflate(R.layout.fragment_exercise_lift, container, false);

        exercise = new Exercise();

        weightET = rootView.findViewById(R.id.weightET);
        repsET = rootView.findViewById(R.id.repsET);
        addBT = rootView.findViewById(R.id.addSetButton);
        recyclerView = rootView.findViewById(R.id.historyLV);

        this.realm = Realm.getDefaultInstance();
        exercise = realm.where(Exercise.class).equalTo("name", getArguments().getString("exercise_name")).findFirst();

        populateList();
        addButtonClickListener();

        return rootView;
    }

    void addButtonClickListener() {
        addBT.setOnClickListener(v -> {
            Lift lift = new Lift();
            lift.setNotes(null);

            if (repsET.getText().toString().length() > 0) {
                lift.setReps(Integer.parseInt(repsET.getText().toString()));
            }

            if (weightET.getText().toString().length() > 0) {
                lift.setWeight(Integer.parseInt(weightET.getText().toString()));
            } else if (weightET.getText().toString().length() == 0) {
                lift.setWeight(0);
            }

            lift.setSetDate(new Date());

            if (exercise.getId() > 0) lift.setExercise(exercise); //if the exercise was found

            try (Realm r = Realm.getDefaultInstance()) {
                r.executeTransaction(realm -> {
                    realm.insertOrUpdate(lift);
                    adapterItemsRecycler.notifyDataSetChanged();
                });
                Utils.displayToast(getContext(), "Successfully added");
            }
        });

    }

    void populateList() {

        this.results = realm.where(Lift.class)
                .contains("exercise_name", getArguments().getString("exercise_name"), Case.INSENSITIVE)
                .findAll()
                .sort("setDate", Sort.DESCENDING);

        adapterItemsRecycler = new AdapterItemsRecycler(results, getContext(), new ItemsListener());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));

        mDividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(),
                DividerItemDecoration.VERTICAL
        );
        recyclerView.addItemDecoration(mDividerItemDecoration);

        recyclerView.setAdapter(adapterItemsRecycler);


        ListListeners();

    }

    void ListListeners() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (realm != null)
            realm.close();
    }

    class ItemsListener implements AdapterView.OnClickListener {
        @Override
        public void onClick(View view) {
            Lift itemTouched = results.get(recyclerView.getChildAdapterPosition(view));
            Utils.displayToast(getContext(), itemTouched.getWeight() + "");
        }
    }

}
