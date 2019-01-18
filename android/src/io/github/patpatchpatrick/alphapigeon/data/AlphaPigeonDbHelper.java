package io.github.patpatchpatrick.alphapigeon.data;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import io.github.patpatchpatrick.alphapigeon.data.AlphaPigeonContract.ScoresEntry;

import io.github.patpatchpatrick.alphapigeon.R;

public class AlphaPigeonDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = AlphaPigeonDbHelper.class.getSimpleName();

    //DB Name and version
    private static final String DATABASE_NAME = "alphapigeon.db";
    //Current version of database is 1
    private static final int DATABASE_VERSION = 1;


    public AlphaPigeonDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create a String that contains the SQL statement to create the goals table
        String SQL_CREATE_SCORES_TABLE = "CREATE TABLE " + ScoresEntry.TABLE_NAME + " ("
                + ScoresEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ScoresEntry.COLUMN_SCORES_NUM_GAMES_PLAYED+ " REAL NOT NULL DEFAULT 0, "
                + ScoresEntry.COLUMN_SCORES_HIGH_SCORES + " REAL NOT NULL DEFAULT 0, "
                + ScoresEntry.COLUMN_SCORES_LAST_SCORE + " REAL NOT NULL DEFAULT 0, "
                + ScoresEntry.COLUMN_SCORES_TOTAL_PLAY_TIME + " REAL NOT NULL DEFAULT 0, "
                + ScoresEntry.COLUMN_SCORES_LAST_PLAY_TIME+ " REAL NOT NULL DEFAULT 0);";


        // Execute the SQL statement
        db.execSQL(SQL_CREATE_SCORES_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
