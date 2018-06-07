package com.example.vlada.licenta.Utils;

import android.content.Context;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.widget.Toast;

import com.example.vlada.licenta.Domain.Cardio;
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

import io.realm.Case;
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

    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String source) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(source);
        }
    }

    public static boolean is1RMCardio(Cardio cardio, RealmResults<Cardio> cardioRealmResults) {
        return cardioRealmResults.stream().map(Cardio::getTime_spent).max(Integer::compare).orElse(-1) == cardio.getTime_spent();
    }

    public static boolean isCardio(Exercise exercise, Realm realm) {
        return realm.where(Exercise.class).contains("name", exercise.getName())
                .and().not().contains("type", "Cardio", Case.INSENSITIVE)
                .and().not().contains("type", "Plyometrics", Case.INSENSITIVE)
                .and().not().contains("type", "Stretching", Case.INSENSITIVE).findAll().size() == 0;

    }

    public static boolean is1RM(Lift lift, RealmResults<Lift> lifts) {
        if (lift.getWeight() > 0 && lift.getReps() == 0)
            return false;
        if (lifts.stream().filter(l -> l.getReps() > 0).map(Lift::getWeight).max(Integer::compare).orElse(null) == 0) { //if there are only body-weight lifts
            return lifts.stream().map(Lift::getReps).max(Integer::compare).orElse(null) == lift.getReps();
        }
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
        return higher + same == lifts.size();
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    public static void addPlaceHolderLifts() {
        Lift lift = new Lift();
        Random rand = new Random();

        try (Realm r = Realm.getDefaultInstance()) {
            r.executeTransaction(realm -> {
                RealmResults<Exercise> exerciseList = realm.where(Exercise.class).contains("musclegroup", "Chest").findAll();
                for (Exercise exercise : exerciseList) {
                    lift.setExercise(exercise);
                    for (int i = 0; i < rand.nextInt(6) + 5; i++) {
                        LocalDate localDate = LocalDate.of(rand.nextInt(1) + 2017, Month.of(rand.nextInt(12) + 1), rand.nextInt(25) + 1);
                        lift.setSetDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
                        for (int j = 0; j <= rand.nextInt(8); j++) {
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
