package io.github.patpatchpatrick.alphapigeon.resources;

public interface DatabaseManager {

    //Interface to manage local data handling in databases (used in Android and mobile devices)
    //Desktop and HTML versions of the game do not use a database

    public void insert(float highScore, float lastScore);

    public void queryHighScores();

    public float getHighScore();

    public float getTotalNumGames();


}
