package com.example.vlada.licenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.MuscleGroup;
import com.example.vlada.licenta.Net.Client.ExerciseClient;
import com.example.vlada.licenta.Utils.Utils;
import com.example.vlada.licenta.Views.HomeActivity;

import java.util.ArrayList;
import java.util.List;

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

//        Utils.deleteAllLifts();
//        Utils.addPlaceHolderLifts();

        Realm realm = Realm.getDefaultInstance();
        if ((long) realm.where(Exercise.class).findAll().size() < 250)
            populateFromDB();
        realm.close();

        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void populateFromDB() {
        CompositeDisposable disposable = new CompositeDisposable();
        ExerciseClient client = new ExerciseClient(getApplicationContext());

        disposable.add(client.getExercises()
                .subscribeOn(Schedulers.io())
                .subscribe(
                        this::getExercisesSuccess,
                        this::getExercisesError
                )
        );

    }



    private void getExercisesError(Throwable throwable) {
    }

    private void getExercisesSuccess(List<Exercise> exercise) {
        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> {
                for (Exercise current : exercise)
                if (realm.where(Exercise.class).equalTo("name", current.getName()).findFirst() == null)
                    realm.insertOrUpdate(exercise);
            });
        }

    }

}
