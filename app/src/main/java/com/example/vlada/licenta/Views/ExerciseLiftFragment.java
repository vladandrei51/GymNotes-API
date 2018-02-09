package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.SwipeDismissListViewTouchListener;
import com.example.vlada.licenta.Utils.Utils;

import java.util.Date;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmBaseAdapter;
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
    ListView historyLV;
    private Realm realm;
    private RealmResults<Lift> results;
    private RealmBaseAdapter<Lift> adapter;


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

        weightET = rootView.findViewById(R.id.weightET);
        repsET = rootView.findViewById(R.id.repsET);
        addBT = rootView.findViewById(R.id.addSetButton);
        historyLV = rootView.findViewById(R.id.historyLV);

        this.realm = Realm.getDefaultInstance();
        exercise = realm.where(Exercise.class).equalTo("name", getArguments().getString("exercise_name")).findFirst();

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

            if (exercise != null) lift.setExercise(exercise);

            try (Realm r = Realm.getDefaultInstance()) {
                r.executeTransaction(realm -> {
                    realm.insertOrUpdate(lift);
                });
                Utils.displayToast(getContext(), "Successfully added");
            }
        });
        populateHistoryList();

        return rootView;
    }

    void populateHistoryList() {

        this.results = realm.where(Lift.class)
                .contains("exercise_name", getArguments().getString("exercise_name"), Case.INSENSITIVE)
                .findAll()
                .sort("setDate", Sort.DESCENDING);

        this.adapter = new RealmBaseAdapter<Lift>(results) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                ExerciseLiftFragment.ViewHolder viewHolder;
                if (convertView == null) {
                    convertView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.list_view, parent, false);
                    viewHolder = new ExerciseLiftFragment.ViewHolder();
                    viewHolder.text = convertView.findViewById(R.id.label);
                    convertView.setTag(viewHolder);
                } else {
                    viewHolder = (ExerciseLiftFragment.ViewHolder) convertView.getTag();
                }

                if (adapterData != null) {
                    final Lift item = adapterData.get(position);
                    if (item.getWeight() > 1)
                        viewHolder.text.setText(String.format("%d kgs for %d reps", item.getWeight(), item.getReps()));
                    else if (item.getWeight() == 1)
                        viewHolder.text.setText(String.format("%d kg for %d reps", item.getWeight(), item.getReps()));
                    else if (item.getWeight() == 0) {
                        viewHolder.text.setText(String.format("Bodyweight for %d reps", item.getReps()));
                    }
                }
                return convertView;

            }
        };

        historyLV.setAdapter(adapter);

        historyLV.setOnItemClickListener((adapterView, view, i, l) -> {
            Lift lift = (Lift) historyLV.getItemAtPosition(i);
            Utils.showAlertDialog(getContext(), "", lift.toPrettyString());
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        historyLV,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    try (Realm r = Realm.getDefaultInstance()) {
                                        r.executeTransaction(realm -> {
                                            results.deleteFromRealm(position);
                                            adapter.notifyDataSetChanged();
                                            Utils.displayToast(getContext(), "Successfully deleted");
                                        });
                                    }
                                }


                            }
                        });
        historyLV.setOnTouchListener(touchListener);

    }

    private static class ViewHolder {
        TextView text;
    }

}
