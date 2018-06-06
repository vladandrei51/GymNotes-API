package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Views.Cardio.CardioChartFragment;
import com.example.vlada.licenta.Views.Cardio.CardioLiftFragment;
import com.example.vlada.licenta.Views.Exercise.ExerciseChartFragment;
import com.example.vlada.licenta.Views.Exercise.ExerciseDetailsFragment;
import com.example.vlada.licenta.Views.Exercise.ExerciseLiftFragment;

import java.util.ArrayList;

import io.realm.Realm;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by andrei-valentin.vlad on 2/8/2018.
 */

public class ExerciseView extends FragmentActivity {

    Toolbar mToolbar;
    CircleIndicator mIndicator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_view);

        mToolbar = findViewById(R.id.my_toolbar);
        if (getIntent().getExtras() != null)
            mToolbar.setTitle(getIntent().getExtras().getString("exercise_name"));
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);

        ViewPager pager = findViewById(R.id.pager);
        mIndicator = findViewById(R.id.main_indicator);
        pager.setAdapter(new ExerciseViewPageAdapter(getSupportFragmentManager()));
        mIndicator.setViewPager(pager);
    }

    private class ExerciseViewPageAdapter extends FragmentPagerAdapter {

        ExerciseViewPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {

            ArrayList<String> cardioTypes = new ArrayList<>();
            cardioTypes.add("Cardio");
            cardioTypes.add("Plyometrics");
            cardioTypes.add("Stretching");

            final ArrayList<Exercise> exercise = new ArrayList<>();
            try (Realm r = Realm.getDefaultInstance()) {
                r.executeTransaction(realm -> {
                    exercise.add(realm.where(Exercise.class).contains("name", getIntent().getExtras().getString("exercise_name")).findFirst());
                });
            }
            if (exercise.get(0) != null) {
                switch (pos) {
                    case 0:
                        return ExerciseDetailsFragment.newInstance(exercise.get(0).getName());
                    case 1:
                        if (!cardioTypes.contains(exercise.get(0).getType())) {
                            return ExerciseLiftFragment.newInstance(exercise.get(0).getName());
                        }
                        return CardioLiftFragment.newInstance(exercise.get(0).getName());
                    case 2:
                        if (!cardioTypes.contains(exercise.get(0).getType()))
                            return ExerciseChartFragment.newInstance(exercise.get(0).getName());
                        return CardioChartFragment.newInstance(exercise.get(0).getName());

                    default:
                        return ExerciseDetailsFragment.newInstance(exercise.get(0).getName());
                }
            }
            finish();
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }
    }

}
