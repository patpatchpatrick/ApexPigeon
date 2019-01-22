package io.github.patpatchpatrick.alphapigeon;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;

import io.github.patpatchpatrick.alphapigeon.data.AlphaPigeonContract.ScoresEntry;

public class DatabaseHandler {

    // Class to handle calls to the Android SQLite database

    public static void insert(Context context, float highScore, float lastScore){

        // Update the high currentScore
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String highScorePreference = context.getResources().getString(R.string.high_score_pref);
        sharedPreferences.edit().putFloat(highScorePreference,  highScore).commit();

        //Update the number of games to increase by 1
        String numOfGamesPref = context.getResources().getString(R.string.number_of_games_pref);
        float numOfGames = sharedPreferences.getFloat(numOfGamesPref, 0);
        numOfGames = numOfGames + 1;
        sharedPreferences.edit().putFloat(numOfGamesPref, numOfGames).commit();

        ContentValues values = new ContentValues();
        values.put(ScoresEntry.COLUMN_SCORES_HIGH_SCORES, highScore);
        values.put(ScoresEntry.COLUMN_SCORES_LAST_SCORE, lastScore);
        values.put(ScoresEntry.COLUMN_SCORES_NUM_GAMES_PLAYED, numOfGames);
        Uri uri = AndroidLauncher.contentResolver.insert(ScoresEntry.CONTENT_URI, values);

        Log.d("URIYO", "" + uri);


    }

    public static float getHighScore(Context context){

        //Return current high currentScore
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String highScorePreference = context.getResources().getString(R.string.high_score_pref);
        return sharedPreferences.getFloat(highScorePreference, 0);

    }

    public static float getTotalNumberOfGames(Context context){
        //Return current total number of games played
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String numOfGamesPreference = context.getResources().getString(R.string.number_of_games_pref);
        return sharedPreferences.getFloat(numOfGamesPreference, 0);
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
