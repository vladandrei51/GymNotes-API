package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Net.Client.ExerciseClient;
import com.example.vlada.licenta.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by andrei-valentin.vlad on 1/30/2018.
 */

public class ExerciseActivity extends AppCompatActivity {
    ListView lvItems;
    private CompositeDisposable disposables = new CompositeDisposable();
    private ExerciseClient exerciseClient;
    private List<Exercise> exerciseList;
    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        exerciseList = new ArrayList<>();
        lvItems = findViewById(R.id.lvItems);

        this.realm = Realm.getDefaultInstance();
        exerciseClient = new ExerciseClient(this);
        populateExerciseList();

    }

    private void setupExerciseList() {
        List<String> exerciseName = exerciseList.stream().map(Exercise::getName).collect(Collectors.toList());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.list_view, exerciseName);
        lvItems.setAdapter(adapter);
        lvItems.setOnItemLongClickListener((arg0, arg1, pos, id) -> {

            Optional<Exercise> clickedExercise = exerciseList.stream().
                    filter(e -> e.getName().equals(lvItems.getItemAtPosition(pos))).findFirst();
            if (clickedExercise.isPresent()) {
                showAlert(clickedExercise.get().getName(), clickedExercise.get().toPrettyString());
            }
            return true;
        });

    }



    void populateExerciseList() {
        disposables.add(exerciseClient.getExercises()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::getExercisesSuccess,
                        this::getExercisesError
                )
        );
    }

    private void getExercisesError(Throwable throwable) {
        showAlert(throwable.getMessage(), "Will load cached data if available");
        exerciseList = realm.where(Exercise.class).findAll();
        setupExerciseList();

    }

    private void getExercisesSuccess(List<Exercise> exercises) {
        this.realm.executeTransaction(realm -> realm.where(Exercise.class).findAll().deleteAllFromRealm());
        for (int i = 0; i < exercises.size(); i++) {
            Exercise foundExercise = exercises.get(i);
            this.exerciseList.add(foundExercise);
            this.realm.executeTransaction(realm -> realm.copyToRealmOrUpdate(foundExercise));
        }
        setupExerciseList();
    }


    private void displayToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    void showAlert(String title, String message) {
        new AlertDialog.Builder(ExerciseActivity.this)
                .setTitle(title)
                .setMessage(message)
                .show();
    }
}
