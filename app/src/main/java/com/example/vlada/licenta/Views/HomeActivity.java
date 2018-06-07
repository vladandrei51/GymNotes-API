package com.example.vlada.licenta.Views;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.Utils;
import com.example.vlada.licenta.Views.Cardio.CardioListView;
import com.example.vlada.licenta.Views.Exercise.ExerciseListView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static boolean isFabOpen;
    FloatingActionButton mMainFab;
    FloatingActionButton mLiftingFab;
    FloatingActionButton mCardioFab;
    View bgFabMenu;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        mMainFab = findViewById(R.id.main_fab);
        mLiftingFab = findViewById(R.id.lifting_fab);
        mCardioFab = findViewById(R.id.cardio_fab);
        bgFabMenu = findViewById(R.id.bg_fab_menu);
        populateStrengthLayout();
        mMainFab.setOnClickListener(v -> {
            if (!isFabOpen) {
                ShowFabMenu();
            } else
                closeFabMenu();
        });

        mLiftingFab.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ExerciseListView.class);
            startActivity(intent);
            closeFabMenu();

        });

        mCardioFab.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, CardioListView.class);
            startActivity(intent);

            closeFabMenu();
        });

        bgFabMenu.setOnClickListener(view -> {
            closeFabMenu();
        });

        super.onCreate(savedInstanceState);
    }

    private void populateStrengthLayout() {
        View benchPressView = findViewById(R.id.home_exercise_bench);
        TextView BPExerciseName = benchPressView.findViewById(R.id.home_exercise_name);
        BPExerciseName.setText(R.string.benchpress_strength_exercise);
        TextView BPStrengthLevel = benchPressView.findViewById(R.id.home_exercise_strength);
        BPStrengthLevel.setText(Utils.getStrengthLevel(true, 90, getString(R.string.benchpress_strength_exercise), getApplicationContext()));

        View pullView = findViewById(R.id.home_exercise_dl);
        TextView DLExerciseName = pullView.findViewById(R.id.home_exercise_name);
        DLExerciseName.setText(R.string.pull_strength_exercise);
        TextView DLStrengthLevel = pullView.findViewById(R.id.home_exercise_strength);
        DLStrengthLevel.setText(Utils.getStrengthLevel(true, 90, getString(R.string.pull_strength_exercise), getApplicationContext()));

        View shoulderPressView = findViewById(R.id.home_exercise_ohp);
        TextView ohpExerciseName = shoulderPressView.findViewById(R.id.home_exercise_name);
        ohpExerciseName.setText(R.string.ohp_strength_exercise);
        TextView ohpStrengthLevel = shoulderPressView.findViewById(R.id.home_exercise_strength);
        ohpStrengthLevel.setText(Utils.getStrengthLevel(true, 90, getString(R.string.ohp_strength_exercise), getApplicationContext()));

        View squatView = findViewById(R.id.home_exercise_squat);
        TextView squatExerciseName = squatView.findViewById(R.id.home_exercise_name);
        squatExerciseName.setText(R.string.squat_strength_exercise);
        TextView squatStrengthLevel = squatView.findViewById(R.id.home_exercise_strength);
        squatStrengthLevel.setText(Utils.getStrengthLevel(true, 90, getString(R.string.squat_strength_exercise), getApplicationContext()));

        View rowView = findViewById(R.id.home_exercise_row);
        TextView rowExerciseName = rowView.findViewById(R.id.home_exercise_name);
        rowExerciseName.setText(R.string.row_strength_exercise);
        TextView rowStrengthLevel = rowView.findViewById(R.id.home_exercise_strength);
        rowStrengthLevel.setText(Utils.getStrengthLevel(true, 90, getString(R.string.row_strength_exercise), getApplicationContext()));


    }

    private void ShowFabMenu() {
        isFabOpen = true;
        mLiftingFab.setVisibility(View.VISIBLE);
        mCardioFab.setVisibility(View.VISIBLE);
        bgFabMenu.setVisibility(View.VISIBLE);

        mMainFab.animate().rotation(360f);
        bgFabMenu.animate().alpha(1f);
        mLiftingFab.animate()
                .translationY(-(mMainFab.getTranslationY() + mMainFab.getHeight() * 1.1f))
                .rotation(0f);
        mCardioFab.animate()
                .translationY(-(mMainFab.getTranslationY() + mMainFab.getHeight() * 2))
                .rotation(0f);
    }

    private void closeFabMenu() {
        isFabOpen = false;

        mMainFab.animate().rotation(0f);
        bgFabMenu.animate().alpha(0f);
        mCardioFab.animate()
                .translationY(0f)
                .rotation(90f);
        mLiftingFab.animate()
                .translationY(0f)
                .rotation(90f).setListener(new FabAnimatorListener(bgFabMenu, mLiftingFab, mCardioFab));
    }


    private class FabAnimatorListener implements Animator.AnimatorListener {
        List<View> mViewsToHide;

        FabAnimatorListener(View bgFabMenu, FloatingActionButton mLiftingFab, FloatingActionButton mCardioFab) {
            mViewsToHide = new ArrayList<>();
            mViewsToHide.clear();
            mViewsToHide.add(bgFabMenu);
            mViewsToHide.add(mLiftingFab);
            mViewsToHide.add(mCardioFab);
        }

        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            if (!isFabOpen) {
                for (View object : mViewsToHide) {
                    object.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    }
}
