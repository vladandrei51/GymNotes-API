package com.example.vlada.licenta.Utils;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by andrei-valentin.vlad on 2/12/2018.
 */

public class RealmBackup {

    private Context context;
    private Realm realm;
    private RealmConfiguration realmConfiguration;

    public RealmBackup(Context context, RealmConfiguration realmConfiguration) {
        this.realm = Realm.getInstance(realmConfiguration);
        this.context = context;
        this.realmConfiguration = realmConfiguration;
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

        while (!realm.isClosed())
            realm.close();
        if (Realm.getGlobalInstanceCount(realmConfiguration) == 0) {
            //Restore
            String FileName = "default.realm";
            String restoreFilePath = context.getExternalFilesDir(null) + "/" + FileName;
            copyBundledRealmFile(restoreFilePath, FileName);
            Utils.displayToast(context, "Successfully restored!");
        } else {
            Utils.displayToast(context, "Global instances = " + Realm.getGlobalInstanceCount(realmConfiguration) +
                    "\nLocal instances = " + Realm.getLocalInstanceCount(realmConfiguration));
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

}

