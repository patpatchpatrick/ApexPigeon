package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.net.HttpRequestBuilder;


public class HighScore {

    public static float currentScore;
    public static float currentHighScore = 0;
    private String scoreString;
    private static boolean newHighScore = false;

    //Fonts
    private BitmapFont font;
    private Boolean pigeonHasNotCrashed = true;

    public HighScore() {

        // set default currentScore and create and set up the font used for the high currentScore display
        currentScore = 0;
        scoreString = "Distance: 0";

        //Initialize FONTS
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

        font.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        font.draw(batch, scoreString, 60, 45);
    }

    public static boolean checkForNewHighScoreAndUpdateNetworkAndDatabase(DatabaseManager databaseManager, Net.HttpResponseListener listener) {

        //Update local game stats (high scores, total number of games, etc..) data for the user after a game is complete
        //For Android/Mobile devices, use the database manager to insert score history into mobile SQLITE db
        //For Android/Mobile/Desktop/HTML, use the Libgdx Prefs to store other score stats

        //Get the current high score from database/prefs
        HighScore.currentHighScore = SettingsManager.highScore;

        //If the recent game score is greater than the high score, that score becomes the new high score
        if (HighScore.currentScore > HighScore.currentHighScore) {
            HighScore.currentHighScore = HighScore.currentScore;
            newHighScore = true;
        }

        //Update database with new high score and increment total number of games
        if (databaseManager != null) {
            databaseManager.insert(HighScore.currentHighScore, currentScore);
        }

        //Update the high score and total number of games in the libgdx prefs
        SettingsManager.setHighScore(HighScore.currentHighScore);
        SettingsManager.increaseTotalNumGames();

        //Update leaderboard via network HTTP get request if there was a new high score
        boolean newHighScoreSubmitted = submitNewHighScoreToNetwork(listener);

        return newHighScoreSubmitted;

    }

    private static boolean submitNewHighScoreToNetwork(Net.HttpResponseListener listener) {

        //Submit high score to network if there is a high score (and return true), otherwise return false
        if (newHighScore) {

            //Format the high score for the dreamlo leaderboard and submit the score via HTTP Get Request
            int highScoreFormatted = (int) (currentScore);

            //Build the url to submit a new high score to network
            StringBuilder urlScoreReq = new StringBuilder("https://dreamlo.com/lb/XtrQXD_4BUGkPBmdz2WSUg3OwYKXHfZUqIcuUscCsXUw/add/");
            urlScoreReq.append(SettingsManager.userName.trim());
            urlScoreReq.append("/");
            urlScoreReq.append(highScoreFormatted);
            urlScoreReq.append("/");
            String urlString = urlScoreReq.toString();

            //Submit the score (the GameOverScreen handles the HTTP response)
            HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
            Net.HttpRequest httpRequest = requestBuilder.newRequest().method(Net.HttpMethods.GET).url(urlString).build();
            Gdx.net.sendHttpRequest(httpRequest, listener);

            //Reset the high score boolean
            newHighScore = false;


            return true;
        }
        return false;
    }

    public void dispose() {

        font.dispose();
    }

    public void stopCounting() {
        this.pigeonHasNotCrashed = false;
    }


}
