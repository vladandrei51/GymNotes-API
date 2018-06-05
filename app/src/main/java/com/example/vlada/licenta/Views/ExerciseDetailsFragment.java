package com.example.vlada.licenta.Views;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.vlada.licenta.Base.BaseFragment;
import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.R;

/**
 * Created by andrei-valentin.vlad on 2/7/2018.
 */

public class ExerciseDetailsFragment extends BaseFragment {

    TextView mExerciseNameTV;
    Exercise mExercise;
    String mExerciseName;
    RatingBar mRatingBar;

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

        mExerciseNameTV = rootView.findViewById(R.id.textView);
        mRatingBar = rootView.findViewById(R.id.ratingBar3);
        if (getArguments() != null)
            mExerciseName = getArguments().getString("exercise_name");
        else {
            Intent intent = new Intent(getActivity(), ExerciseListView.class);
            startActivity(intent);
        }

        mExercise = (Exercise) mRealmHelper.getRealmObject(Exercise.class, "name", mExerciseName);
        if (mExercise != null) {
            populatePage();
        }

        return rootView;
    }

    private void populatePage() {
        mExerciseNameTV.setText(mExercise.getName());
        mRatingBar.setRating(mExercise.getRating() / 2);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
