package io.github.patpatchpatrick.alphapigeon.resources;

public interface DatabaseAndPreferenceManager {

    //Interface to manage local data handling in databases

    public void insert(float highScore, float lastScore);

    public void queryHighScores();

    public float getHighScore();

    public float getTotalNumGames();

    //SETTINGS METHODS
    //--On Off Buttons

    public void toggleMusicOnOff(Boolean isOn);

    public void toggleGameSoundsOnOff(Boolean isOn);

    public void toggleTouchControlsOnOff(Boolean isOn);

    public void toggleAccelButtonOnOff(Boolean isOn);

    public boolean isMusicOn();

    public boolean  isGameSoundsOn();

    public boolean isTouchControlsOn();

    public boolean isAccelButtonOn();

    public boolean isFullScreenModeOn();

    //SETTINGS METHODS
    //--Sliders

    public void toggleMusicVolumeSlider(float value);

    public void toggleGameVolumeSlider(float value);

    public void toggleTouchSensitivity(float value);

    public void toggleAccelSensitivity(float value);

    public void toggleFullScreenMode(boolean isOn);

    public float getMusicVolumeSliderValue();

    public float getGameVolumeSliderValue();

    public float getTouchSensitivitySliderValue();

    public float getAccelSensitivitySliderValue();

    public boolean getAdRemovalPurchasedValue();




}
