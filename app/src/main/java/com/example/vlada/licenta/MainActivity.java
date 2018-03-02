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
//                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
//
//        Lift lift = new Lift();
//        Lift lift2 = new Lift();
//        Lift lift3 = new Lift();
//        Lift lift4 = new Lift();
//        Lift lift5 = new Lift();
//
//        LocalDate localDate = LocalDate.of(2018, Month.FEBRUARY, 5);
//        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        LocalDate localDate2 = LocalDate.of(2018, Month.FEBRUARY, 9);
//        Date date2 = Date.from(localDate2.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//        LocalDate localDate3 = LocalDate.of(2018, Month.FEBRUARY, 12);
//        Date date3 = Date.from(localDate3.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//
//        LocalDate localDate4 = LocalDate.of(2018, Month.FEBRUARY, 27);
//        Date date4 = Date.from(localDate4.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//
//        LocalDate localDate5 = LocalDate.of(2018, Month.MARCH, 1);
//        Date date5 = Date.from(localDate5.atStartOfDay(ZoneId.systemDefault()).toInstant());
//
//
//        lift.setSetDate(date);
//
//        try (Realm r = Realm.getDefaultInstance()) {
//            r.executeTransaction(realm -> {
//                Exercise exercise = realm.where(Exercise.class).equalTo("name", "Chin To Chest Stretch").findFirst();
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
//
//                lift3.setExercise(exercise);
//                lift3.setWeight(14);
//                lift3.setReps(15);
//                lift3.setSetDate(date3);
//                realm.insert(lift3);
//
//                lift4.setExercise(exercise);
//                lift4.setWeight(24);
//                lift4.setReps(12);
//                lift4.setSetDate(date4);
//                realm.insert(lift4);
//
//                lift5.setExercise(exercise);
//                lift5.setWeight(11);
//                lift5.setReps(33);
//                lift5.setSetDate(date5);
//                realm.insert(lift5);
//            });
//        }


//        setContentView(R.layout.activity_main);
        Intent intent = new Intent(MainActivity.this, ExerciseListView.class);
        startActivity(intent);
        finish();
    }
}
