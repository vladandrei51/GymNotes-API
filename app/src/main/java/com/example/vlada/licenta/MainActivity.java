package com.example.vlada.licenta;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.vlada.licenta.Views.ExerciseListView;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

//        Lift lift = new Lift();
//        Lift lift2 = new Lift();
//
//        LocalDate localDate = LocalDate.of(2018, Month.FEBRUARY, 5);
//        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        LocalDate localDate2 = LocalDate.of(2018, Month.FEBRUARY, 9);
//        Date date2 = Date.from(localDate2.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//
//        lift.setSetDate(date);
//
//        try (Realm r = Realm.getDefaultInstance()) {
//            r.executeTransaction(realm -> {
//                Exercise exercise = realm.where(Exercise.class).equalTo("name", "Side Neck Stretch").findFirst();
//                lift.setExercise(exercise);
//                lift.setWeight(10);
//                lift.setReps(10);
//                realm.insert(lift);
//
//                lift2.setExercise(exercise);
//                lift2.setWeight(10);
//                lift2.setReps(10);
//                lift2.setSetDate(date2);
//                realm.insert(lift2);
//            });
//        }


        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, ExerciseListView.class);
        startActivity(intent);
        finish();
    }
}
