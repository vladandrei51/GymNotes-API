package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.example.vlada.licenta.R;

import io.realm.Realm;

/**
 * Created by andrei-valentin.vlad on 2/8/2018.
 */

public class ExerciseView extends FragmentActivity {

    private Realm realm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_view);
        this.realm = Realm.getDefaultInstance();


        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

    }

    private class MyPagerAdapter extends FragmentPagerAdapter {

        public MyPagerAdapter(FragmentManager fm) {
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
