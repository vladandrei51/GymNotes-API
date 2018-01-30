package com.example.vlada.licenta.Views;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Net.Client.ExerciseClient;
import com.example.vlada.licenta.Net.DTOs.TokenDTO;
import com.example.vlada.licenta.R;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;

/**
 * Created by andrei-valentin.vlad on 1/30/2018.
 */

public class ExerciseActivity extends AppCompatActivity {
    private Exercise exercise;
    private CompositeDisposable disposables = new CompositeDisposable();
    private ExerciseClient exerciseClient;

    private Realm realm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        this.realm = Realm.getDefaultInstance();
        exerciseClient = new ExerciseClient(this);
    }

    public void InsertExerciseIntoDB(View view) {
        exercise = new Exercise();
        exercise.setName("aaa");

        disposables.add(exerciseClient.addExercise(exercise)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        this::addSuccess,
                        this::addFail
                )
        );

    }
    private void showToast(String message){
        Toast toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    private void addFail(Throwable throwable) {
        showToast("Entry could not be added!");
    }

    private void addSuccess(TokenDTO tokenDTO) {
        showToast("You've added successfully!");
    }
}
