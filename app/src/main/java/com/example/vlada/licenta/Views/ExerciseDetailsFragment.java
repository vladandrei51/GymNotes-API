package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.R;

import io.realm.Realm;

/**
 * Created by andrei-valentin.vlad on 2/7/2018.
 */

public class ExerciseDetailsFragment extends Fragment {

    TextView exerciseNameTV;
    Exercise exercise;
    private Realm realm;

    public ExerciseDetailsFragment() {

    }

    public static ExerciseDetailsFragment newInstance(String text) {

        ExerciseDetailsFragment f = new ExerciseDetailsFragment();
        Bundle b = new Bundle();
        b.putString("exercise_name", text);

        f.setArguments(b);

        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_exercise_details, container, false);

        exerciseNameTV = rootView.findViewById(R.id.textView);

        this.realm = Realm.getDefaultInstance();
        if (getArguments().getString("exercise_name") != null)
            exercise = realm.where(Exercise.class).equalTo("name", getArguments().getString("exercise_name")).findFirst();

        if (exercise != null) {
            populatePage();
        }

        return rootView;
    }

    private void populatePage() {
        exerciseNameTV.setText(exercise.getName());
    }


}
