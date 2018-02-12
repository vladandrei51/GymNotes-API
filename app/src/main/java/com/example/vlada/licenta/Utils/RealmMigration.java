package com.example.vlada.licenta.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;

/**
 * Created by andrei-valentin.vlad on 2/12/2018.
 */

public class RealmMigration {

    private final static String TAG = RealmMigration.class.getName();

    private Context context;
    private Realm realm;

    public RealmMigration(Context context) {
        this.realm = Realm.getInstance(Realm.getDefaultConfiguration());
        this.context = context;
    }

    public void backup() {

        File exportRealmFile = null;

        File exportRealmPATH = context.getExternalFilesDir(null);
        String exportRealmFileName = "default.realm";

        Log.d(TAG, "Realm DB Path = " + realm.getPath());

        // create a backup file
        exportRealmFile = new File(exportRealmPATH, exportRealmFileName);

        // if backup file already exists, delete it
        exportRealmFile.delete();

        // copy current realm to backup file
        realm.writeCopyTo(exportRealmFile);

        String msg = "File exported to Path: " + context.getExternalFilesDir(null);
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
        Utils.displayToast(context, msg);
        Log.d(TAG, msg);

        realm.close();

    }

    public void restore() {

        //Restore
        File exportRealmPATH = context.getExternalFilesDir(null);
        String FileName = "default.realm";

        String restoreFilePath = context.getExternalFilesDir(null) + "/" + FileName;

        Log.d(TAG, "oldFilePath = " + restoreFilePath);

        copyBundledRealmFile(restoreFilePath, FileName);

        Utils.displayToast(context, "Data restore is done!");
        Log.d(TAG, "Data restore is done");

    }

    private String copyBundledRealmFile(String oldFilePath, String outFileName) {
        try {
            File file = new File(context.getFilesDir(), outFileName);

            Log.d(TAG, "context.getFilesDir() = " + context.getFilesDir().toString());
            FileOutputStream outputStream = new FileOutputStream(file);

            FileInputStream inputStream = new FileInputStream(new File(oldFilePath));

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
            return file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String dbPath() {

        return realm.getPath();
    }
}