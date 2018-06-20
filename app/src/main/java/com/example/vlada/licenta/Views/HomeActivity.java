package com.example.vlada.licenta.Views;

import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Utils.Utils;
import com.example.vlada.licenta.Views.Calendar.CalendarActivity;
import com.example.vlada.licenta.Views.Cardio.CardioListView;
import com.example.vlada.licenta.Views.Exercise.ExerciseListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class HomeActivity extends AppCompatActivity {
    private final static String PREF_BODYWEIGHT_KEY = "bodyweight_key";
    private final static String PREF_GENDER_KEY = "list_gender";
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
        setTitle(getString(R.string.app_name));
        mSwipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this::refreshActivity);

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

        bgFabMenu.setOnClickListener(view -> closeFabMenu());

        super.onCreate(savedInstanceState);
    }

    private void refreshActivity() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);

    }

    private void populateStrengthLayout() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int bw = prefs.getInt(PREF_BODYWEIGHT_KEY, 70);
        boolean isMale = Objects.equals(prefs.getString(PREF_GENDER_KEY, "Male"), "Male");

        Integer averageLevel;
        TextView bwTV = findViewById(R.id.bodyweight_tv);
        bwTV.setText(bw + " kg");

        ArrayList<Integer> exercise2level = new ArrayList<>();

        HashMap<Integer, String> compound2usedMuscles = new HashMap<>();
        compound2usedMuscles.put(0, "Chest");
        compound2usedMuscles.put(1, "Lower Back");
        compound2usedMuscles.put(2, "Shoulders");
        compound2usedMuscles.put(3, "Legs");
        compound2usedMuscles.put(4, "Middle Back");


        HashMap<String, Integer> label2level = new HashMap<>();
        label2level.put(getApplicationContext().getString(R.string.exercise_not_performed_yet), 0);
        label2level.put("Untrained", 1);
        label2level.put("Novice", 2);
        label2level.put("Intermediate", 3);
        label2level.put("Advanced", 4);
        label2level.put("Elite", 5);

        View benchPressView = findViewById(R.id.home_exercise_bench);
        TextView BPExerciseName = benchPressView.findViewById(R.id.home_exercise_name);
        BPExerciseName.setText(R.string.benchpress_strength_exercise);
        TextView BPStrengthLabel = benchPressView.findViewById(R.id.home_exercise_strength);
        BPStrengthLabel.setText(Utils.getStrengthLevel(isMale, bw, getString(R.string.benchpress_strength_exercise), getApplicationContext()));
        exercise2level.add(label2level.get(BPStrengthLabel.getText().toString()));
        TextView BPStrengthLevel = benchPressView.findViewById(R.id.home_exercise_level);
        BPStrengthLevel.setText(String.valueOf(Utils.get1RMofExercise(getString(R.string.benchpress_strength_exercise)) + " kg"));
        benchPressView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ExerciseView.class);
            intent.putExtra("exercise_name", getString(R.string.benchpress_strength_exercise));
            startActivity(intent);
        });


        View pullView = findViewById(R.id.home_exercise_dl);
        TextView DLExerciseName = pullView.findViewById(R.id.home_exercise_name);
        DLExerciseName.setText(R.string.pull_strength_exercise);
        TextView DLStrengthLabel = pullView.findViewById(R.id.home_exercise_strength);
        DLStrengthLabel.setText(Utils.getStrengthLevel(isMale, bw, getString(R.string.pull_strength_exercise), getApplicationContext()));
        exercise2level.add(label2level.get(DLStrengthLabel.getText().toString()));
        TextView DLStrengthLevel = pullView.findViewById(R.id.home_exercise_level);
        DLStrengthLevel.setText(String.valueOf(Utils.get1RMofExercise(getString(R.string.pull_strength_exercise)) + " kg"));
        pullView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ExerciseView.class);
            intent.putExtra("exercise_name", getString(R.string.pull_strength_exercise));
            startActivity(intent);
        });


        View shoulderPressView = findViewById(R.id.home_exercise_ohp);
        TextView ohpExerciseName = shoulderPressView.findViewById(R.id.home_exercise_name);
        ohpExerciseName.setText(R.string.ohp_strength_exercise);
        TextView ohpStrengthLabel = shoulderPressView.findViewById(R.id.home_exercise_strength);
        ohpStrengthLabel.setText(Utils.getStrengthLevel(isMale, bw, getString(R.string.ohp_strength_exercise), getApplicationContext()));
        exercise2level.add(label2level.get(ohpStrengthLabel.getText().toString()));
        TextView ohpStrengthLevel = shoulderPressView.findViewById(R.id.home_exercise_level);
        ohpStrengthLevel.setText(String.valueOf(Utils.get1RMofExercise(getString(R.string.ohp_strength_exercise)) + " kg"));
        shoulderPressView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ExerciseView.class);
            intent.putExtra("exercise_name", getString(R.string.ohp_strength_exercise));
            startActivity(intent);
        });


        View squatView = findViewById(R.id.home_exercise_squat);
        TextView squatExerciseName = squatView.findViewById(R.id.home_exercise_name);
        squatExerciseName.setText(R.string.squat_strength_exercise);
        TextView squatStrengthLabel = squatView.findViewById(R.id.home_exercise_strength);
        squatStrengthLabel.setText(Utils.getStrengthLevel(isMale, bw, getString(R.string.squat_strength_exercise), getApplicationContext()));
        exercise2level.add(label2level.get(squatStrengthLabel.getText().toString()));
        TextView squatStrengthLevel = squatView.findViewById(R.id.home_exercise_level);
        squatStrengthLevel.setText(String.valueOf(Utils.get1RMofExercise(getString(R.string.squat_strength_exercise)) + " kg"));
        squatView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ExerciseView.class);
            intent.putExtra("exercise_name", getString(R.string.squat_strength_exercise));
            startActivity(intent);
        });


        View rowView = findViewById(R.id.home_exercise_row);
        TextView rowExerciseName = rowView.findViewById(R.id.home_exercise_name);
        rowExerciseName.setText(R.string.row_strength_exercise);
        TextView rowStrengthLabel = rowView.findViewById(R.id.home_exercise_strength);
        rowStrengthLabel.setText(Utils.getStrengthLevel(isMale, bw, getString(R.string.row_strength_exercise), getApplicationContext()));
        exercise2level.add(label2level.get(rowStrengthLabel.getText().toString()));
        TextView rowStrengthLevel = rowView.findViewById(R.id.home_exercise_level);
        rowStrengthLevel.setText(String.valueOf((Utils.get1RMofExercise(getString(R.string.row_strength_exercise)) + " kg")));
        rowView.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ExerciseView.class);
            intent.putExtra("exercise_name", getString(R.string.row_strength_exercise));
            startActivity(intent);
        });


        TextView strongPoints = findViewById(R.id.strong_points);
        TextView weakPoints = findViewById(R.id.weak_points);
        strongPoints.setText("");
        weakPoints.setText("");
        int minimum_level = exercise2level.stream().mapToInt(a -> a).min().orElse(0);
        int maximum_level = exercise2level.stream().mapToInt(a -> a).max().orElse(0);

        if (exercise2level.contains(0)) {
            strongPoints.setText(R.string.not_enough_data);
            weakPoints.setText(R.string.not_enough_data);
        } else if (minimum_level == maximum_level) {
            strongPoints.setText(R.string.symmetrical_strength);
            weakPoints.setText(R.string.symmetrical_strength);
        } else {
            for (int i = 0; i < exercise2level.size(); i++) {
                if (exercise2level.get(i) == minimum_level) {
                    weakPoints.append(compound2usedMuscles.get(i) + ", ");
                } else if (exercise2level.get(i) == maximum_level) {
                    strongPoints.append(compound2usedMuscles.get(i) + ", ");
                }
            }
            weakPoints.setText(weakPoints.getText().toString().substring(0, weakPoints.getText().toString().length() - 2));
            strongPoints.setText(strongPoints.getText().toString().substring(0, strongPoints.getText().toString().length() - 2));

        }

        TextView strengthLevel = findViewById(R.id.main_strength_level);
        averageLevel = exercise2level.stream().mapToInt(a -> a).sum() / 5;
        strengthLevel.setText(exercise2level.contains(0) ? "Not all exercises are performed" : Utils.getKeyByValue(label2level, averageLevel));
    }

    @Override
    public void onBackPressed() {
        invalidateOptionsMenu();
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            super.onBackPressed();
            //additional code
        } else {
            getFragmentManager().popBackStack();
            setTitle(getString(R.string.app_name));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settings_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings_menu:
                getFragmentManager().beginTransaction()
                        .replace(android.R.id.content, new SettingsFragment())
                        .addToBackStack("SettingsFragment")
                        .commit();
                break;
            case R.id.calendar_menu:
                Intent intent = new Intent(HomeActivity.this, CalendarActivity.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
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
//
//    public void onDataPass(int bodyweight, boolean isMale) {
//        this.settings_bodyweight = bodyweight;
//        this.settings_isMale = isMale;
//    }


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
