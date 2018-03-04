package com.example.vlada.licenta.Utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.example.vlada.licenta.Domain.Lift;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

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


    public static float getEstimated1RM(Lift lift) {
        return (float) lift.getWeight() * (36 / (37 - (float) lift.getReps()));
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }


}
