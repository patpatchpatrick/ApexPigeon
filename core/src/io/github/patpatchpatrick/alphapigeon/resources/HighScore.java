package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;

import java.text.DecimalFormat;
import java.text.NumberFormat;


public class HighScore {

    public static float currentScore;
    public static float currentHighScore = 0;
    private String scoreString;

    /**
     * NON-HTML FONTS
     * HTML doesn't support FreeTypeFontGenerator so must use bitmap instead
     * These comments are in place in case it is ever decided to use freetype font generator for mobile/desktop applications
    private BitmapFont font12;
    FreeTypeFontGenerator generator;**/

    //HTML Fonts
    private BitmapFont font;
    private Boolean pigeonHasNotCrashed = true;

    public HighScore() {

        // set default currentScore and create and set up the font used for the high currentScore display
        currentScore = 0;
        scoreString = "Distance: 0";

        /**
         * Iniitalize NON-HTML Fonts
         * * HTML doesn't support FreeTypeFontGenerator so must use bitmap instead
         *  These comments are in place in case it is ever decided to use freetype font generator for mobile/desktop applications
        generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/univers.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 14;
        parameter.minFilter = Texture.TextureFilter.Linear;
        parameter.magFilter = Texture.TextureFilter.Linear;
        font12 = generator.generateFont(parameter);
        font12.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font12.getData().setScale(0.1f);
        font12.setUseIntegerPositions(false);
         **/

        //Initialize HTML FONTS
        font = new BitmapFont(Gdx.files.internal("fonts/arial-15.fnt"),
                Gdx.files.internal("fonts/arial-15.png"), false);
        font.getData().setScale(0.1f);
        font.setUseIntegerPositions(false);
    }


    public void update(float deltaTime) {
        // increase currentScore
        // the currentScore is equal to the distance (meters) that the bird has traveled
        // if the pigeon has not crashed, keep increasing the currentScore
        // after the pigeon crashes, stop increasing currentScore
        //DecimalFormat df = new DecimalFormat("#.##");
        float formattedScore = (int) currentScore;

        if (pigeonHasNotCrashed) {
            currentScore = currentScore + GameVariables.pigeonSpeed * deltaTime;
            scoreString = "Distance    " + (int) formattedScore + "  m";
        }

    }

    public void render(SpriteBatch batch) {
        // display currentScore

        /**
         * Non-HTML Font
        font12.draw(batch, scoreString, 60, 45);
         **/

        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.draw(batch, scoreString, 60, 45);
    }

    public static void updateLocalGameStatisticsDataMobile(DatabaseAndPreferenceManager databaseAndPreferenceManager){

        //Update local game stats (high scores, total number of games, etc..) data for the user after a game is complete
        //Use the database manager to update local data

        //Get the current high score from the local database/shared prefs of the users device
        HighScore.currentHighScore = databaseAndPreferenceManager.getHighScore();

        //If the recent game score is greater than the high score, that score becomes the new high score
        if (HighScore.currentScore  > HighScore.currentHighScore){
            HighScore.currentHighScore = HighScore.currentScore;
        }

        //Insert the game round data into the local database
        databaseAndPreferenceManager.insert(HighScore.currentHighScore, currentScore);

    }

    public static void updateLocalGameStatisticsData(Preferences libgdxPrefs){

        //Update local game stats (high scores, total number of games, etc..) data for the user after a game is complete
        //Use the database manager to update local data

        //Get the current high score from the libgdx preferences

        HighScore.currentHighScore = libgdxPrefs.getFloat("highscore", 0);

        //If the recent game score is greater than the high score, that score becomes the new high score
        if (HighScore.currentScore  > HighScore.currentHighScore){
            HighScore.currentHighScore = HighScore.currentScore;
        }

        //Insert the game round data into the sharedprefs
        libgdxPrefs.putFloat("highscore", HighScore.currentHighScore);
        libgdxPrefs.putFloat("totalnumgames", libgdxPrefs.getFloat("totalnumgames") + 1);

    }

    public static boolean newHighScore(){
        //If the current High Score equals the current score, a new high score was reached this round
        return currentHighScore == currentScore;
    }

    public static boolean submitNewHighScore(PlayServices playServices){
        //Submit high score to play services if there is a high score (and return true), otherwise return false
        if (newHighScore()){
            if (playServices != null){
                //Format the currentScore for Google Play Services and submit the currentScore
                //Google play services does not take decimals, so the score must be multiplied by 100
                // to remove the decimal places
                int highScoreFormatted = (int)(currentScore);
                playServices.submitScore(highScoreFormatted, SettingsManager.userName);
            }
            return true;
        }
        return false;
    }

    public void dispose() {

        /**
         * Non-HTML Fonts
        generator.dispose();
        font12.dispose();
         **/

        //HTML Fonts
        font.dispose();
    }

    public void stopCounting() {
        this.pigeonHasNotCrashed = false;
    }



}
