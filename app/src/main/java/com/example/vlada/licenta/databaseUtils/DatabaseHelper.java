package com.example.vlada.licenta.databaseUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {


    public DatabaseHelper(Context context) {
        super(context, DatabaseContent.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.beginTransaction();
        try{
            sqLiteDatabase.execSQL(DatabaseContent.CREATE_EXERCISE_TABLE);
            sqLiteDatabase.execSQL(DatabaseContent.CREATE_SETS_TABLE);
            sqLiteDatabase.setTransactionSuccessful();
        }
        finally{
            sqLiteDatabase.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}