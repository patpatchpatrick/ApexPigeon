package io.github.patpatchpatrick.alphapigeon.resources;

public interface DatabaseAndPreferenceManager {

    //Interface to manage local data handling in databases

    public void insert(float highScore, float lastScore);

    public void queryHighScores();

    public float getHighScore();

    public float getTotalNumGames();

    public void toggleMusicOnOff(Boolean isOn);

    public void toggleGameSoundsOnOff(Boolean isOn);

    public void toggleTouchControlsOnOff(Boolean isOn);

    public void toggleAccelButtonOnOff(Boolean isOn);

    public boolean isMusicOn();

    public boolean  isGameSoundsOn();

    public boolean isTouchControlsOn();

    public boolean isAccelButtonOn();


}
