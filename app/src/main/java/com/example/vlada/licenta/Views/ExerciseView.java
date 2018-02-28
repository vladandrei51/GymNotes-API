package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;

import com.example.vlada.licenta.R;

/**
 * Created by andrei-valentin.vlad on 2/8/2018.
 */

public class ExerciseView extends FragmentActivity {

    Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_view);
        mToolbar = findViewById(R.id.my_toolbar);
        if (getIntent().getExtras() != null)
            mToolbar.setTitle(getIntent().getExtras().getString("exercise_name"));
        mToolbar.setTitleTextColor(android.graphics.Color.WHITE);


        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {


            if (getIntent().getExtras() != null) {
                switch (pos) {
                    case 0:
                        return ExerciseDetailsFragment.newInstance(getIntent().getExtras().getString("exercise_name"));
                    case 1:
                        return ExerciseLiftFragment.newInstance(getIntent().getExtras().getString("exercise_name"));
                    case 2:
                        return ExerciseChartFragment.newInstance(getIntent().getExtras().getString("exercise_name"));
                    default:
                        return ExerciseDetailsFragment.newInstance(getIntent().getExtras().getString("exercise_name"));
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
