package io.github.patpatchpatrick.alphapigeon.resources;

public interface DatabaseAndPreferenceManager {

    //Interface to manage local data handling in databases

    public void insert(float highScore, float lastScore);

    public void queryHighScores();

    public float getHighScore();

    public float getTotalNumGames();


}
