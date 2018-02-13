package com.example.vlada.licenta.Utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;

/**
 * Created by andrei-valentin.vlad on 2/12/2018.
 */

public class RealmMigration {

    private Context context;
    private Realm realm;

    public RealmMigration(Context context) {
        this.realm = Realm.getInstance(Realm.getDefaultConfiguration());
        this.context = context;
    }


    public void backup() {

        File exportRealmFile;

        File exportRealmPATH = context.getExternalFilesDir(null);
        String exportRealmFileName = "default.realm";


        // create a backup file
        exportRealmFile = new File(exportRealmPATH, exportRealmFileName);

        // if backup file already exists, delete it
        exportRealmFile.delete();

        // copy current realm to backup file
        realm.writeCopyTo(exportRealmFile);

        String msg = "File exported to Path: " + context.getExternalFilesDir(null);
        Utils.displayToast(context, msg);

        if (realm != null) realm.close();

    }

    public void restore() {

        realm.close();
        if (Realm.getGlobalInstanceCount(Realm.getDefaultConfiguration()) == 0) {

            //Restore
            File exportRealmPATH = context.getExternalFilesDir(null);
            String FileName = "default.realm";

            String restoreFilePath = context.getExternalFilesDir(null) + "/" + FileName;


            copyBundledRealmFile(restoreFilePath, FileName);

            Utils.displayToast(context, "Successfully restored!");
        } else {
            Utils.displayToast(context, "Global=" + Realm.getGlobalInstanceCount(Realm.getDefaultConfiguration()) + " local=" + Realm.getLocalInstanceCount(Realm.getDefaultConfiguration()));
        }
    }

    private void copyBundledRealmFile(String oldFilePath, String outFileName) {
        try {
            File file = new File(context.getFilesDir(), outFileName);

            FileOutputStream outputStream = new FileOutputStream(file);

            FileInputStream inputStream = new FileInputStream(new File(oldFilePath));

            byte[] buf = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buf)) > 0) {
                outputStream.write(buf, 0, bytesRead);
            }
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String dbPath() {

        return realm.getPath();
    }
}

