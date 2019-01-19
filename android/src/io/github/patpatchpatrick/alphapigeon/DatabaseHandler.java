package io.github.patpatchpatrick.alphapigeon;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import io.github.patpatchpatrick.alphapigeon.data.AlphaPigeonContract.ScoresEntry;

public class DatabaseHandler {

    // Class to handle calls to the Android SQLite database

    public static void insert(float highScore,  float lastScore,  float totalNumberOfGames){

        ContentValues values = new ContentValues();
        values.put(ScoresEntry.COLUMN_SCORES_HIGH_SCORES, highScore);
        values.put(ScoresEntry.COLUMN_SCORES_LAST_SCORE, lastScore);
        values.put(ScoresEntry.COLUMN_SCORES_NUM_GAMES_PLAYED, totalNumberOfGames);
        Uri uri = AndroidLauncher.contentResolver.insert(ScoresEntry.CONTENT_URI, values);

        Log.d("URIYO", "" + uri);

    }

    public static void query(){
        //Query standings
        String[] scoresProjection = {
                ScoresEntry._ID,
                ScoresEntry.COLUMN_SCORES_HIGH_SCORES,
                ScoresEntry.COLUMN_SCORES_LAST_SCORE,
                ScoresEntry.COLUMN_SCORES_NUM_GAMES_PLAYED,
                ScoresEntry.COLUMN_SCORES_TOTAL_PLAY_TIME,
                ScoresEntry.COLUMN_SCORES_LAST_PLAY_TIME,

        };

        Cursor scoresCursor = AndroidLauncher.contentResolver.query(ScoresEntry.CONTENT_URI, scoresProjection,
                null, null,
                null);

    }


}
