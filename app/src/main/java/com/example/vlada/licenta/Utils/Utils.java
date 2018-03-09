package com.example.vlada.licenta.Utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.vlada.licenta.Domain.Exercise;
import com.example.vlada.licenta.Domain.Lift;

import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

import io.realm.Realm;

/**
 * Created by andrei-valentin.vlad on 2/9/2018.
 */

public class Utils {
    public static void displayToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    public static void showAlertDialog(Context context, String title, String message) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .show();
    }


    public static double getEstimated1RM(Lift lift) {
        return (double) lift.getWeight() * (36 / (37 - (double) lift.getReps()));
    }

    static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static void addPlaceHolderLifts() {
        Lift lift = new Lift();
        Random rand = new Random();

        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> {
                for (Exercise exercise : realm.where(Exercise.class).findAll()) {
                    lift.setExercise(exercise);
                    for (int i = 0; i < rand.nextInt(5) + 1; i++) {
                        LocalDate localDate = LocalDate.of(rand.nextInt(2018 - 2017 + 1) + 2017, Month.of(rand.nextInt(12 - 1 + 1) + 1), rand.nextInt(28 - 1 + 1) + 1);
                        lift.setSetDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        for (int j = 0; j <= rand.nextInt(7) + 1; j++) {
                            lift.setWeight(rand.nextInt(30));
                            lift.setReps(rand.nextInt(20));
                            realm.insertOrUpdate(lift);
                        }
                    }
                }
            });
        }

    }


}
