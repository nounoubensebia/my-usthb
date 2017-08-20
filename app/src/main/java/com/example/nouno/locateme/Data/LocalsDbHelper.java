package com.example.nouno.locateme.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.nouno.locateme.Utils.FileUtils;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by nouno on 14/08/2017.
 */

public class LocalsDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "Locals.db";
    Context dbContext;
    public LocalsDbHelper (Context context)
    {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
        this.dbContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_TABLE_STRUCTURES = "CREATE TABLE "+LocalsContract.StructuresEntry.TABLE_NAME+" ("+
                LocalsContract.StructuresEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocalsContract.StructuresEntry.COLUMN_NAME_LABEL+" TEXT NOT NULL, "+
                LocalsContract.StructuresEntry.COLUMN_NAME_TAGS+ " TEXT, "+
                LocalsContract.StructuresEntry.COLUMN_NAME_LAT+" DOUBLE, "+
                LocalsContract.StructuresEntry.COLUMN_NAME_LON+" DOUBLE)";
        String SQL_CREATE_TABLE_CLASSROOMS = "CREATE TABLE "+LocalsContract.ClassroomsEntry.TABLE_NAME+" ("+
                LocalsContract.ClassroomsEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocalsContract.ClassroomsEntry.COLUMN_NAME_LABEL+" TEXT NOT NULL, "+
                LocalsContract.ClassroomsEntry.COLUMN_NAME_TAGS+ " TEXT, "+
                LocalsContract.ClassroomsEntry.COLUMN_NAME_LAT+" DOUBLE, "+
                LocalsContract.ClassroomsEntry.COLUMN_NAME_LON+" DOUBLE, "+
                LocalsContract.ClassroomsEntry.COLUMN_NAME_ID_STRUCTURE+ " INTEGER, FOREIGN KEY ("+LocalsContract.ClassroomsEntry.COLUMN_NAME_ID_STRUCTURE+
                ") REFERENCES "+LocalsContract.StructuresEntry.TABLE_NAME+"("+LocalsContract.StructuresEntry._ID+"))"
                ;
        String SQL_CREATE_TABLE_CENTER_OF_INTERESTS = "CREATE TABLE "+LocalsContract.CenterOfInterestsEntry.TABLE_NAME+" ("+
                LocalsContract.CenterOfInterestsEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_LABEL+" TEXT NOT NULL, "+
                LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_TAGS+ " TEXT, "+
                LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_ID_STRUCTURE+ "INTEGER, "+
                LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_LAT+" DOUBLE, "+
                LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_TYPE + " INTEGER, "+
                LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_LON+" DOUBLE, "+
                LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_ID_STRUCTURE+ " INTEGER, FOREIGN KEY ("+LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_ID_STRUCTURE+
                ") REFERENCES "+LocalsContract.StructuresEntry.TABLE_NAME+"("+LocalsContract.StructuresEntry._ID+"))";



        db.execSQL(SQL_CREATE_TABLE_STRUCTURES);
        db.execSQL(SQL_CREATE_TABLE_CLASSROOMS);
        db.execSQL(SQL_CREATE_TABLE_CENTER_OF_INTERESTS);

        try {
            InputStream inputStream = dbContext.getResources().getAssets().open("LocalsJson.txt");
            String localsJson = FileUtils.readFile(inputStream);
            StructureList structureList = new Gson().fromJson(localsJson,StructureList.class);

            for (Structure structure:structureList.getStructures())
            {
                ContentValues values = new ContentValues();
                values.put(LocalsContract.StructuresEntry._ID,structure.getId());
                values.put(LocalsContract.StructuresEntry.COLUMN_NAME_LABEL,structure.getLabel());
                values.put(LocalsContract.StructuresEntry.COLUMN_NAME_LAT,structure.getCoordinate().getLatitude());
                values.put(LocalsContract.StructuresEntry.COLUMN_NAME_LON,structure.getCoordinate().getLongitude());
                values.put(LocalsContract.StructuresEntry.COLUMN_NAME_TAGS,structure.getTags());
                db.insert(LocalsContract.StructuresEntry.TABLE_NAME,null,values);
            }
            for (CenterOfInterest centerOfInterest:structureList.getCenterOfInterests())
            {
                ContentValues values = new ContentValues();
                values.put(LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_LABEL,centerOfInterest.getLabel());
                values.put(LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_TAGS,centerOfInterest.getTags());
                values.put(LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_ID_STRUCTURE,centerOfInterest.getStructureId());
                values.put(LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_LAT,centerOfInterest.getCoordinate().getLatitude());
                values.put(LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_LON,centerOfInterest.getCoordinate().getLongitude());
                values.put(LocalsContract.CenterOfInterestsEntry.COLUMN_NAME_TYPE,centerOfInterest.getType());
                db.insert(LocalsContract.CenterOfInterestsEntry.TABLE_NAME,null,values);
            }
            for (Classroom classroom:structureList.getClassrooms())
            {
                ContentValues values = new ContentValues();
                values.put(LocalsContract.ClassroomsEntry.COLUMN_NAME_LABEL,classroom.getLabel());
                values.put(LocalsContract.ClassroomsEntry.COLUMN_NAME_TAGS,classroom.getTags());
                values.put(LocalsContract.ClassroomsEntry.COLUMN_NAME_ID_STRUCTURE,classroom.getStructureId());
                values.put(LocalsContract.ClassroomsEntry.COLUMN_NAME_LAT,classroom.getCoordinate().getLatitude());
                values.put(LocalsContract.ClassroomsEntry.COLUMN_NAME_LON,classroom.getCoordinate().getLongitude());
                db.insert(LocalsContract.ClassroomsEntry.TABLE_NAME,null,values);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String SQL_DELETE_TABLE_CLASSROOMS = "DROP TABLE "+LocalsContract.ClassroomsEntry.TABLE_NAME;
        String SQL_DELETE_TABLE_CENTER_OF_INTERESTS = "DROP TABLE "+LocalsContract.CenterOfInterestsEntry.TABLE_NAME;
        String SQL_DELETE_TABLE_STRUCTURE = "DROP TABLE "+LocalsContract.StructuresEntry.TABLE_NAME;
        db.execSQL(SQL_DELETE_TABLE_CENTER_OF_INTERESTS);
        db.execSQL(SQL_DELETE_TABLE_CLASSROOMS);
        db.execSQL(SQL_DELETE_TABLE_STRUCTURE);
        onCreate(db);
    }


}
