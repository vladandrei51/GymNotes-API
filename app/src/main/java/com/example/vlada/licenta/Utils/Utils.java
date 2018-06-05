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
import io.realm.RealmResults;

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
        return (double) lift.getWeight() * Math.pow((double) lift.getReps(), 0.10f);
    }

    public static boolean is1RM(Lift lift, RealmResults<Lift> lifts) {
        int higher = 0;
        int same = 0;
        for (Lift aux : lifts) {
            if (getEstimated1RM(lift) > getEstimated1RM(aux)) {
                higher++;
            }
            if (getEstimated1RM(lift) == getEstimated1RM(aux)) {
                same++;
            }
        }
        return lift.getWeight() > 0 ? higher + same == lifts.size() : lifts.stream().map(Lift::getReps).max(Integer::compare).orElse(0) == lift.getReps();
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
//                realm.deleteAll();
                RealmResults<Exercise> exerciseList = null;
                try {
                    exerciseList = realm.where(Exercise.class).findAllAsync();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                for (Exercise exercise : exerciseList) {
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
