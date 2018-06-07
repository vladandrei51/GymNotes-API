package com.example.vlada.licenta.Views;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.Utils;
import com.example.vlada.licenta.Views.Cardio.CardioListView;
import com.example.vlada.licenta.Views.Exercise.ExerciseListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static boolean isFabOpen;
    FloatingActionButton mMainFab;
    FloatingActionButton mLiftingFab;
    FloatingActionButton mCardioFab;
    View bgFabMenu;
    SwipeRefreshLayout mSwipeRefreshLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        mMainFab = findViewById(R.id.main_fab);
        mLiftingFab = findViewById(R.id.lifting_fab);
        mCardioFab = findViewById(R.id.cardio_fab);
        bgFabMenu = findViewById(R.id.bg_fab_menu);

        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            Intent intent = getIntent();
            overridePendingTransition(0, 0);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            finish();
            overridePendingTransition(0, 0);
            startActivity(intent);
        });

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

        Integer averageLevel = 0;
        HashMap<String, Integer> label2level = new HashMap<>();
        label2level.put("Untrained", 1);
        label2level.put("Novice", 2);
        label2level.put("Intermediate", 3);
        label2level.put("Advanced", 4);
        label2level.put("Elite", 5);

        View benchPressView = findViewById(R.id.home_exercise_bench);
        TextView BPExerciseName = benchPressView.findViewById(R.id.home_exercise_name);
        BPExerciseName.setText(R.string.benchpress_strength_exercise);
        TextView BPStrengthLabel = benchPressView.findViewById(R.id.home_exercise_strength);
        BPStrengthLabel.setText(Utils.getStrengthLevel(true, 90, getString(R.string.benchpress_strength_exercise), getApplicationContext()));
        averageLevel += label2level.get(BPStrengthLabel.getText().toString());
        TextView BPStrengthLevel = benchPressView.findViewById(R.id.home_exercise_level);
        BPStrengthLevel.setText(String.valueOf(Utils.get1RMofExercise(getString(R.string.benchpress_strength_exercise)) + " kg"));

        View pullView = findViewById(R.id.home_exercise_dl);
        TextView DLExerciseName = pullView.findViewById(R.id.home_exercise_name);
        DLExerciseName.setText(R.string.pull_strength_exercise);
        TextView DLStrengthLabel = pullView.findViewById(R.id.home_exercise_strength);
        DLStrengthLabel.setText(Utils.getStrengthLevel(true, 90, getString(R.string.pull_strength_exercise), getApplicationContext()));
        averageLevel += label2level.get(DLStrengthLabel.getText().toString());
        TextView DLStrengthLevel = pullView.findViewById(R.id.home_exercise_level);
        DLStrengthLevel.setText(String.valueOf(Utils.get1RMofExercise(getString(R.string.pull_strength_exercise)) + " kg"));


        View shoulderPressView = findViewById(R.id.home_exercise_ohp);
        TextView ohpExerciseName = shoulderPressView.findViewById(R.id.home_exercise_name);
        ohpExerciseName.setText(R.string.ohp_strength_exercise);
        TextView ohpStrengthLabel = shoulderPressView.findViewById(R.id.home_exercise_strength);
        ohpStrengthLabel.setText(Utils.getStrengthLevel(true, 90, getString(R.string.ohp_strength_exercise), getApplicationContext()));
        averageLevel += label2level.get(ohpStrengthLabel.getText().toString());
        TextView ohpStrengthLevel = shoulderPressView.findViewById(R.id.home_exercise_level);
        ohpStrengthLevel.setText(String.valueOf(Utils.get1RMofExercise(getString(R.string.ohp_strength_exercise)) + " kg"));


        View squatView = findViewById(R.id.home_exercise_squat);
        TextView squatExerciseName = squatView.findViewById(R.id.home_exercise_name);
        squatExerciseName.setText(R.string.squat_strength_exercise);
        TextView squatStrengthLabel = squatView.findViewById(R.id.home_exercise_strength);
        squatStrengthLabel.setText(Utils.getStrengthLevel(true, 90, getString(R.string.squat_strength_exercise), getApplicationContext()));
        averageLevel += label2level.get(squatStrengthLabel.getText().toString());
        TextView squatStrengthLevel = squatView.findViewById(R.id.home_exercise_level);
        squatStrengthLevel.setText(String.valueOf(Utils.get1RMofExercise(getString(R.string.squat_strength_exercise)) + " kg"));


        View rowView = findViewById(R.id.home_exercise_row);
        TextView rowExerciseName = rowView.findViewById(R.id.home_exercise_name);
        rowExerciseName.setText(R.string.row_strength_exercise);
        TextView rowStrengthLabel = rowView.findViewById(R.id.home_exercise_strength);
        rowStrengthLabel.setText(Utils.getStrengthLevel(true, 90, getString(R.string.row_strength_exercise), getApplicationContext()));
        averageLevel += label2level.get(rowStrengthLabel.getText().toString());
        TextView rowStrengthLevel = rowView.findViewById(R.id.home_exercise_level);
        rowStrengthLevel.setText(String.valueOf((Utils.get1RMofExercise(getString(R.string.row_strength_exercise)) + " kg")));

        TextView strengthLevel = findViewById(R.id.main_strength_level);
        averageLevel /= 5;
        strengthLevel.setText(Utils.getKeyByValue(label2level, averageLevel));
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
