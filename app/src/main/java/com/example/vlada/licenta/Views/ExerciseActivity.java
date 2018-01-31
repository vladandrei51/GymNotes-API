package com.example.vlada.licenta.Views;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Net.Client.ExerciseClient;
import com.example.vlada.licenta.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by andrei-valentin.vlad on 1/30/2018.
 */

public class ExerciseActivity extends AppCompatActivity {
    private CompositeDisposable disposables = new CompositeDisposable();
    private ExerciseClient exerciseClient;
    private List<Exercise> exerciseList;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);
        exerciseList = new ArrayList<>();

        this.realm = Realm.getDefaultInstance();
        exerciseClient = new ExerciseClient(this);
        populateExerciseList();

    }

    private void setupExerciseList() {
        ListView lvItems = findViewById(R.id.lvItems);
        ArrayAdapter<Exercise> adapter = new ArrayAdapter<>(this, R.layout.list_view, exerciseList);
        lvItems.setAdapter(adapter);
        displayToast(exerciseList.size() + " elements");
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
        exerciseList = realm.where(Exercise.class).findAll();
        setupExerciseList();

    }

    private void getExercisesSuccess(List<Exercise> exercises) {
        this.realm.executeTransaction(realm -> realm.where(Exercise.class).findAll().deleteAllFromRealm());
        for (int i = 0; i < exercises.size(); i++) {
            Exercise foundExercise = exercises.get(i);
            this.exerciseList.add(foundExercise);
            this.realm.executeTransactionAsync(realm -> realm.copyToRealmOrUpdate(foundExercise));
        }
        displayToast("Done");
        setupExerciseList();
    }


    private void displayToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

//    private void addFail(Throwable throwable) {
//        displayToast(throwable.getMessage());
//    }
//
//    private void addSuccess(TokenDTO tokenDTO) {
//        displayToast("You've added successfully!");
//    }
}
