package com.example.nouno.locateme.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by nouno on 14/08/2017.
 */

public class LocalsDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Locals.db";
    public LocalsDbHelper (Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE_STRUCTURES = "CREATE TABLE"+LocalsContract.StructuresEntry.TABLE_NAME+" ("+
                LocalsContract.StructuresEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocalsContract.StructuresEntry.COLUMN_NAME_LABEL+" TEXT NOT NULL, "+
                LocalsContract.StructuresEntry.COLUMN_NAME_TAGS+ " TEXT, "+
                LocalsContract.StructuresEntry.COLUMN_NAME_LAT+" DOUBLE, "+
                LocalsContract.StructuresEntry.COLUMN_NAME_LON+" DOUBLE)";
        String SQL_CREATE_TABLE_CLASSROOMS = "CREATE TABLE"+LocalsContract.ClassroomsEntry.TABLE_NAME+" ("+
                LocalsContract.ClassroomsEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocalsContract.ClassroomsEntry.COLUMN_NAME_LABEL+" TEXT NOT NULL, "+
                LocalsContract.ClassroomsEntry.COLUMN_NAME_TAGS+ " TEXT, "+
                LocalsContract.ClassroomsEntry.COLUMN_NAME_LAT+" DOUBLE, "+
                LocalsContract.ClassroomsEntry.COLUMN_NAME_ID_STRUCTURE+ " INTEGER,  FOREIGN KEY("+LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_ID_STRUCTURE+
                ") REFERENCES "+LocalsContract.StructuresEntry.TABLE_NAME+"("+LocalsContract.StructuresEntry._ID+")"+
                LocalsContract.ClassroomsEntry.COLUMN_NAME_LON+" DOUBLE)";
        String SQL_CREATE_TABLE_CENTER_OF_INTERESTS = "CREATE TABLE"+LocalsContract.CenterOfInterestsEntry.TABLE_NAME+" ("+
                LocalsContract.CenterOfInterestsEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_LABEL+" TEXT NOT NULL, "+
                LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_TAGS+ " TEXT, "+
                LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_ID_STRUCTURE+ " INTEGER,  FOREIGN KEY("+LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_ID_STRUCTURE+
                ") REFERENCES "+LocalsContract.StructuresEntry.TABLE_NAME+"("+LocalsContract.StructuresEntry._ID+")"+
                LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_LAT+" DOUBLE, "+
                LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_LON+" DOUBLE)";

        db.execSQL(SQL_CREATE_TABLE_STRUCTURES);
        db.execSQL(SQL_CREATE_TABLE_CLASSROOMS);
        db.execSQL(SQL_CREATE_TABLE_CENTER_OF_INTERESTS);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_TABLE_CLASSROOMS = "DROM TABLE"+LocalsContract.ClassroomsEntry.TABLE_NAME;
        String SQL_DELETE_TABLE_CENTER_OF_INTERESTS = "DROM TABLE"+LocalsContract.CenterOfInterestsEntry.TABLE_NAME;
        String SQL_DELETE_TABLE_STRUCTURE = "DROM TABLE"+LocalsContract.StructuresEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_TABLE_CENTER_OF_INTERESTS);
        db.execSQL(SQL_DELETE_TABLE_CLASSROOMS);
        db.execSQL(SQL_DELETE_TABLE_STRUCTURE);
        onCreate(db);
    }
}
