package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.R;
import com.example.vlada.licenta.Views.Base.BaseActivity;

import io.realm.Realm;

/**
 * Created by andrei-valentin.vlad on 2/7/2018.
 */

public class ExerciseView extends BaseActivity {

    TextView exerciseName;
    private Realm realm;
    private Exercise exercise;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        exerciseName = findViewById(R.id.textView);
        this.realm = Realm.getDefaultInstance();
        if (getIntent().getExtras() != null) {
            exercise = realm.where(Exercise.class).equalTo("name", getIntent().getExtras().getString("exercise_name")).findFirst();
            if (exercise != null) {
                populatePage();
            }
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true); //for enabling back button on top of activity
            }
        }

    }

    private void populatePage() {
        setTitle(exercise.getName() + " (" + exercise.getMusclegroup() + ")");
        exerciseName.setText(exercise.getName());
    }

    //    For enabling back button on top of the activity
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
