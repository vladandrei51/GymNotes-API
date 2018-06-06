package com.example.vlada.licenta.Views;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.vlada.licenta.Adapter.ImagePagerAdapter;
import com.example.vlada.licenta.Base.BaseFragment;
import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.Lift;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.Utils;

import me.relex.circleindicator.CircleIndicator;

/**
 * Created by andrei-valentin.vlad on 2/7/2018.
 */

public class ExerciseDetailsFragment extends BaseFragment {

    private static ViewPager mPager;
    Exercise mExercise;
    TextView mTextViewDescription;
    TextView mTextView1RM;
    String mExerciseName;
    RatingBar mRatingBar;
    CircleIndicator indicator;

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

        mTextView1RM = rootView.findViewById(R.id.exercise_1RM);
        mTextViewDescription = rootView.findViewById(R.id.exercise_details);
        mPager = rootView.findViewById(R.id.image_pager);
        mRatingBar = rootView.findViewById(R.id.ratingBar3);
        indicator = rootView.findViewById(R.id.indicator);

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
        String s = "<b>Main Muscle: </b>" + mExercise.getMusclegroup() + "<br>" + "<b>Exercise Type: </b>" + mExercise.getType() + "<br>" + "<br>" + mExercise.getDescription();
        mTextViewDescription.setText(Utils.fromHtml(s));
        mRatingBar.setRating(mExercise.getRating() / 2);
        if (mRatingBar.getRating() == 0) {
            mRatingBar.setVisibility(View.INVISIBLE);
        }
        LayerDrawable stars = (LayerDrawable) mRatingBar.getProgressDrawable();
        double max_rm = mRealmHelper.findAllFiltered(Lift.class, "exercise_name", mExerciseName).stream().map(Utils::getEstimated1RM).max(Double::compare).orElse(0.0);
        if (max_rm != 0) {
            mTextView1RM.append(String.valueOf((int) max_rm) + " kg");
        } else if (mRealmHelper.findAllFiltered(Lift.class, "exercise_name", mExerciseName).size() == 0) {
            mTextView1RM.setText("Exercise not performed yet");
        } else {
            mTextView1RM.setText("Most amounts of repetitions using bodyweight: " + mRealmHelper.findAllFiltered(Lift.class, "exercise_name", mExerciseName).stream().map(Lift::getReps).max(Integer::compare).orElse(0));

        }
        stars.getDrawable(2).setColorFilter(Color.rgb(48, 63, 159), PorterDuff.Mode.SRC_ATOP);
        stars.getDrawable(0).setColorFilter(Color.rgb(158, 158, 158), PorterDuff.Mode.SRC_ATOP);

        mPager.setAdapter(new ImagePagerAdapter(getContext(), mExercise));
        indicator.setViewPager(mPager);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
