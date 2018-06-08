package com.example.vlada.licenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Net.Client.ExerciseClient;
import com.example.vlada.licenta.Views.HomeActivity;

import javax.annotation.Nullable;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
//        Utils.addPlaceHolderLifts();

        insertHomeExercises();

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void insertHomeExercises() {
        CompositeDisposable disposable = new CompositeDisposable();
        ExerciseClient client = new ExerciseClient(getApplicationContext());
        disposable.add(client.getExerciseByName(getString(R.string.benchpress_strength_exercise))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::getExercisesSuccess,
                        this::getExercisesError
                )
        );

        disposable.add(client.getExerciseByName(getString(R.string.pull_strength_exercise))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::getExercisesSuccess,
                        this::getExercisesError
                )
        );

        disposable.add(client.getExerciseByName(getString(R.string.row_strength_exercise))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::getExercisesSuccess,
                        this::getExercisesError
                )
        );

        disposable.add(client.getExerciseByName(getString(R.string.squat_strength_exercise))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::getExercisesSuccess,
                        this::getExercisesError
                )
        );

        disposable.add(client.getExerciseByName(getString(R.string.ohp_strength_exercise))
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::getExercisesSuccess,
                        this::getExercisesError
                )
        );
    }

    private void getExercisesError(Throwable throwable) {
    }

    private void getExercisesSuccess(Exercise exercise) {
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> {
                if (realm.where(Exercise.class).equalTo("name", exercise.getName()).findFirst() == null)
                    realm.insertOrUpdate(exercise);
            });
        }

    }

}
