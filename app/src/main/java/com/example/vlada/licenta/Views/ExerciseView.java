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

        Realm realm;
        String exercise_name;

        ExerciseViewPageAdapter(FragmentManager fm) {
            super(fm);

        }

        @Override
        public Fragment getItem(int pos) {

            ArrayList<String> cardioTypes = new ArrayList<>();
            cardioTypes.add("Cardio");
            cardioTypes.add("Plyometrics");
            cardioTypes.add("Stretching");

            realm = Realm.getDefaultInstance();

            exercise_name = getIntent().getExtras().getString("exercise_name");

            final ArrayList<Exercise> exercises = new ArrayList<>();
            exercises.add(realm.where(Exercise.class).equalTo("name", exercise_name).findFirst());

            if (exercises.get(0) != null) {
                switch (pos) {
                    case 0:
                        return ExerciseDetailsFragment.newInstance(exercises.get(0).getName());
                    case 1:
                        if (!cardioTypes.contains(exercises.get(0).getType())) {
                            return ExerciseLiftFragment.newInstance(exercises.get(0).getName());
                        }
                        return CardioLiftFragment.newInstance(exercises.get(0).getName());
                    case 2:
                        if (!cardioTypes.contains(exercises.get(0).getType()))
                            return ExerciseChartFragment.newInstance(exercises.get(0).getName());
                        return CardioChartFragment.newInstance(exercises.get(0).getName());

                    default:
                        return ExerciseDetailsFragment.newInstance(exercises.get(0).getName());
                }
            }
            finish();
            realm.close();
            return null;
        }


        @Override
        public int getCount() {
            return 3;
        }
    }

}
