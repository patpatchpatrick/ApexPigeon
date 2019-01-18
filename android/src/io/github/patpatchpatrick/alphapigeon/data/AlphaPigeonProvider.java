package io.github.patpatchpatrick.alphapigeon.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import io.github.patpatchpatrick.alphapigeon.data.AlphaPigeonContract.ScoresEntry;

public class AlphaPigeonProvider extends ContentProvider {

    private static final int SCORE = 100;
    private static final int SCORE_ID = 101;
    private static final int MATCH = 200;
    private static final int MATCH_ID = 201;


    //URI matcher to handle different URIs input into provider
    public static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(AlphaPigeonContract.CONTENT_AUTHORITY, AlphaPigeonContract.PATH_SCORE, SCORE);
        sUriMatcher.addURI(AlphaPigeonContract.CONTENT_AUTHORITY, AlphaPigeonContract.PATH_SCORE + "/#", SCORE_ID);
    }

    //Tag for log messages
    public static final String LOG_TAG = AlphaPigeonProvider.class.getSimpleName();
    private AlphaPigeonDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        //Create new instance of AlphaPigeonDbHelper to access database.
        mDbHelper = new AlphaPigeonDbHelper((getContext()));
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //Cursor to hold results of query
        Cursor cursor;

        //Match the URI
        int match = sUriMatcher.match(uri);
        switch (match) {
            case SCORE:
                //Query the table directly with the given inputs
                cursor = database.query(ScoresEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case SCORE_ID:
                //Query the table for a specific team ID
                selection = ScoresEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(ScoresEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case SCORE:
                return ScoresEntry.CONTENT_LIST_TYPE;
            case SCORE_ID:
                return ScoresEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SCORE:
                return insertScores(uri, contentValues);
            default:
                //Insert is not supported for a specific SCORE ID, will hit default exception
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertScores(Uri uri, ContentValues values) {

        // Checks to determine values are ok before inserting into database
        // Check to ensure name is not null
        float totalNumberOfGames = values.getAsFloat(ScoresEntry.COLUMN_SCORES_NUM_GAMES_PLAYED);
        if (totalNumberOfGames <= 0.0f) {
            throw new IllegalArgumentException("Value greater than 0 required");
        }

        float highScore = values.getAsFloat(ScoresEntry.COLUMN_SCORES_HIGH_SCORES);
        if (highScore <= 0.0f) {
            throw new IllegalArgumentException("Value greater than 0 required");
        }

        float lastScore = values.getAsFloat(ScoresEntry.COLUMN_SCORES_LAST_SCORE);
        if (lastScore <= 0.0f) {
            throw new IllegalArgumentException("Value greater than 0 required");
        }

        float totalPlayTime = values.getAsFloat(ScoresEntry.COLUMN_SCORES_TOTAL_PLAY_TIME);
        if (totalPlayTime <= 0.0f) {
            throw new IllegalArgumentException("Value greater than 0 required");
        }

        float lastPlayTime = values.getAsFloat(ScoresEntry.COLUMN_SCORES_LAST_PLAY_TIME);
        if (lastPlayTime <= 0.0f) {
            throw new IllegalArgumentException("Value greater than 0 required");
        }


        //If data is valid, insert data into SQL database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        long id = db.insert(ScoresEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        //Notify any listeners that the data has changed for the URI
        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);

        int rowsDeleted;

        switch (match) {
            case SCORE:
                rowsDeleted = database.delete(ScoresEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case SCORE_ID:
                selection = ScoresEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                rowsDeleted = database.delete(ScoresEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        //If 1 or more rows were deleted, notify all listeners that data at the given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case SCORE:
                return updateScores(uri, contentValues, selection, selectionArgs);
            case SCORE_ID:
                // For the SCORE_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = ScoresEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateScores(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateScores(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If the ScoresEntry.Name key is present,
        // check that the name value is not null.
        if (values.containsKey(ScoresEntry.COLUMN_SCORES_NUM_GAMES_PLAYED)) {
            float numGamesPlayed = values.getAsFloat(ScoresEntry.COLUMN_SCORES_NUM_GAMES_PLAYED);
            if (numGamesPlayed <= 0.0f) {
                throw new IllegalArgumentException("Value must be greater than 0");
            }
        }

        // If the ScoresEntry.Name key is present,
        // check that the name value is not null.
        if (values.containsKey(ScoresEntry.COLUMN_SCORES_HIGH_SCORES)) {
            float highScore = values.getAsFloat(ScoresEntry.COLUMN_SCORES_HIGH_SCORES);
            if (highScore<= 0.0f) {
                throw new IllegalArgumentException("Value must be greater than 0");
            }
        }

        // If the ScoresEntry.Name key is present,
        // check that the name value is not null.
        if (values.containsKey(ScoresEntry.COLUMN_SCORES_LAST_SCORE)) {
            float lastScore = values.getAsFloat(ScoresEntry.COLUMN_SCORES_LAST_SCORE);
            if (lastScore <= 0.0f) {
                throw new IllegalArgumentException("Value must be greater than 0");
            }
        }

        // If the ScoresEntry.Name key is present,
        // check that the name value is not null.
        if (values.containsKey(ScoresEntry.COLUMN_SCORES_TOTAL_PLAY_TIME)) {
            float totPlayTime = values.getAsFloat(ScoresEntry.COLUMN_SCORES_TOTAL_PLAY_TIME);
            if (totPlayTime <= 0.0f) {
                throw new IllegalArgumentException("Value must be greater than 0");
            }
        }

        // If the ScoresEntry.Name key is present,
        // check that the name value is not null.
        if (values.containsKey(ScoresEntry.COLUMN_SCORES_LAST_PLAY_TIME)) {
            float lastPlayTime = values.getAsFloat(ScoresEntry.COLUMN_SCORES_LAST_PLAY_TIME);
            if (lastPlayTime <= 0.0f) {
                throw new IllegalArgumentException("Value must be greater than 0");
            }
        }

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writeable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ScoresEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Return the number of rows updated
        return rowsUpdated;
    }

}
