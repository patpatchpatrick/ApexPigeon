package io.github.patpatchpatrick.alphapigeon.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class AlphaPigeonContract {

    //Contract for DB used to store data for this app

    //URI Information
    public static final String CONTENT_AUTHORITY = "io.github.patpatchpatrick.alphapigeon";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_SCORE = "score";

    private AlphaPigeonContract() {
    }

    public static final class ScoresEntry implements BaseColumns {

        //The MIME type of the {@link #CONTENT_URI} for a list of scores
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCORE;


        //The MIME type of the {@link #CONTENT_URI} for a single score item
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_SCORE;

        //URI for Scores table
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_SCORE);


        //Define table and columns for score data
        public static final String TABLE_NAME = "score";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_SCORES_NUM_GAMES_PLAYED = "scoresNumGames";
        public static final String COLUMN_SCORES_HIGH_SCORES = "scoresHighScores";
        public static final String COLUMN_SCORES_LAST_SCORE = "scoresLastScore";
        public static final String COLUMN_SCORES_TOTAL_PLAY_TIME = "scoresTotalPlayTime";
        public static final String COLUMN_SCORES_LAST_PLAY_TIME = "scoresLastPlayTime";


    }
    
}
