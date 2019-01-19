package io.github.patpatchpatrick.alphapigeon.resources;

public interface DatabaseManager {

    //Interface to manage local data handling in databases

    public void insert(float highScore, float lastScore);

    public void query();

    public float getHighScore();

    public float getTotalNumGames();


}
