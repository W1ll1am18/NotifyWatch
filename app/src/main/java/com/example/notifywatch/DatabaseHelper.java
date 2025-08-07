package com.example.notifywatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    //https://developer.android.com/training/data-storage/sqlite#java
    public static final int DATABASE_VERSION = 1;
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + Db.Entry.TABLE_NAME + " (" +
                    Db.Entry.COLUMN_ID + " INTEGER PRIMARY KEY," +
                    Db.Entry.COLUMN_TITLE + " TEXT," +
                    Db.Entry.COLUMN_TEXT + " TEXT," +
                    Db.Entry.COLUMN_TIME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + Db.Entry.TABLE_NAME;

    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    //Put
    public void insert() {
        // Gets the data repository in write mode
        SQLiteDatabase db = this.getWritableDatabase();

        String title = "TESTING DB ENTRY";
        String text = "This ia a test to see if this works";
        String time = "Format time here";

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(Db.Entry.COLUMN_TITLE, title);
        values.put(Db.Entry.COLUMN_TEXT, text);
        values.put(Db.Entry.COLUMN_TIME, time);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(Db.Entry.TABLE_NAME, null, values);
    }

    //Query
    public void read() {
        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BaseColumns._ID,
                Db.Entry.COLUMN_TITLE,
                Db.Entry.COLUMN_TEXT,
                Db.Entry.COLUMN_TIME
        };

        // Filter results WHERE "title" = 'My Title'
        //Should change Db.entry.column text to something more dynamic.
        String selection = Db.Entry.COLUMN_TITLE + " = ?";
        String[] selectionArgs = { "My Title" };

        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                Db.Entry.COLUMN_TEXT + " DESC";

        Cursor cursor = db.query(
                Db.Entry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );

        //Display Here???
    }

    //Delete
    public void delete() {
        SQLiteDatabase db = this.getReadableDatabase();
        // Define 'where' part of query.
        String selection = Db.Entry.COLUMN_TITLE + " LIKE ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = { "MyTitle" };
        // Issue SQL statement.
        int deletedRows = db.delete(Db.Entry.TABLE_NAME, selection, selectionArgs);
    }

    //Update
    public void updateEntry() {
        SQLiteDatabase db = this.getWritableDatabase();

        // New value for one column
        String title = "MyNewTitle";
        ContentValues values = new ContentValues();
        values.put(Db.Entry.COLUMN_TITLE, title);

        // Which row to update, based on the title
        String selection = Db.Entry.COLUMN_TITLE + " LIKE ?";
        String[] selectionArgs = { "MyOldTitle" };

        int count = db.update(
                Db.Entry.TABLE_NAME,
                values,
                selection,
                selectionArgs);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
