package io.github.patpatchpatrick.alphapigeon.resources;

public interface DatabaseManager {

    //Interface to manage local data handling in databases

    public void insert(float highScore, float lastScore, float totalGames);

    public void query();


}
