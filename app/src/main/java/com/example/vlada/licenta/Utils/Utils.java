package com.example.vlada.licenta.Utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

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

}
