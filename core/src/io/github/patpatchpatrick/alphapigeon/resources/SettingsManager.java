package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class SettingsManager {


    //Class to manage settings and updates to settings via database/preferences

    public static DatabaseManager databaseManager;

    //Booleans for Settings
    public static boolean musicSettingIsOn = true;
    public static boolean gameSoundsSettingIsOn = true;
    public static boolean touchSettingIsOn = true;
    public static boolean accelerometerSettingIsOn = true;
    public static boolean adRemovalPurchased = true; //Setting for if user has purchased ad removal  //TODO set to false if releasing on mobile device
    public static boolean fullScreenModeIsOn = false;

    //Floats for Sliders
    //--All slider values are between 0 and 1
    public static final float MINIMUM_SLIDER_VALUE = 0f;
    public static final float MAXIMUM_SLIDER_VALUE = 1f;
    public static float musicVolume = 0.5f;
    public static float gameVolume = 0.5f;
    public static float touchSensitivity = 0.5f;
    public static float accelSensitivity = 0.5f;

    //Floats for Other Stats
    public static float totalNumGames = 0;
    public static float highScore = 0;

    private static Preferences libgdxPrefs = Gdx.app.getPreferences("alpha.pigeon.prefs");

    //Strings for User Name
    public static String userName = "";

    public static void updateSettings() {

        //Fetch current settings from the database/shared preferences

        //Buttons
        musicSettingIsOn = libgdxPrefs.getBoolean("isMusicOn", true);
        gameSoundsSettingIsOn = libgdxPrefs.getBoolean("isGameSoundOn", true);
        touchSettingIsOn = libgdxPrefs.getBoolean("isTouchOn", true);
        accelerometerSettingIsOn = libgdxPrefs.getBoolean("isAccelOn", true);
        fullScreenModeIsOn = libgdxPrefs.getBoolean("isFullScreenOn", false);

        //Sliders
        musicVolume = libgdxPrefs.getFloat("musicVolume", 0.5f);
        gameVolume = libgdxPrefs.getFloat("gameVolume", 0.5f);
        touchSensitivity = libgdxPrefs.getFloat("touchSensitivity", 0.5f);
        accelSensitivity = libgdxPrefs.getFloat("accelSensitivity", 0.5f);

        //Other stats
        totalNumGames = libgdxPrefs.getFloat("totalnumgames", 0);
        highScore = libgdxPrefs.getFloat("highscore", 0);

        //Billing Settings
        adRemovalPurchased = libgdxPrefs.getBoolean("adRemovalPurchased", true);

        userName = libgdxPrefs.getString("userName", "");

        //If the game sounds setting is off, set game volume to 0
        if (!gameSoundsSettingIsOn) {
            gameVolume = 0;
        }


    }

    public static void toggleMusicSetting(Boolean isOn) {

        //If the setting changed, update the preferences

        if (musicSettingIsOn != isOn) {
            libgdxPrefs.putBoolean("isMusicOn", isOn).flush();
            musicSettingIsOn = isOn;
            Sounds.toggleBackgroundMusic(isOn);
        }
    }

    public static void toggleGameSoundsSetting(Boolean isOn) {

        //If the setting changed, update the preferences

        if (gameSoundsSettingIsOn != isOn) {
            libgdxPrefs.putBoolean("isGameSoundOn", isOn).flush();
            gameSoundsSettingIsOn = isOn;
        }

    }

    public static void toggleTouchSetting(Boolean isOn) {

        //If the setting changed, update the preferences

        if (touchSettingIsOn != isOn) {
            libgdxPrefs.putBoolean("isTouchOn", isOn).flush();
            touchSettingIsOn = isOn;
        }

    }

    public static void toggleAccelerometerSetting(Boolean isOn) {

        //If the setting changed, update the preferences

        if (accelerometerSettingIsOn != isOn) {
            libgdxPrefs.putBoolean("isAccelOn", isOn).flush();
            accelerometerSettingIsOn = isOn;
        }

    }

    public static void toggleMusicVolumeSetting(float value) {

        //If the setting changed, update the preferences

        if (musicVolume != value) {
            libgdxPrefs.putFloat("musicVolume", value).flush();
            musicVolume = value;
            Sounds.setBackgroundMusicVolume(value);
        }

    }

    public static void toggleGameVolumeSetting(float value) {

        //If the setting changed, update the preferences

        if (gameVolume != value) {
            libgdxPrefs.putFloat("gameVolume", value).flush();
            gameVolume = value;
        }

    }

    public static void toggleTouchSensitivitySetting(float value) {

        //If the setting changed, update the preferences

        if (touchSensitivity != value) {
            libgdxPrefs.putFloat("touchSensitivity", value).flush();
            touchSensitivity = value;
        }

    }

    public static void toggleAccelSensitivitySetting(float value) {

        //If the setting changed, update the preferences

        if (accelSensitivity != value) {
            libgdxPrefs.putFloat("accelSensitivity", value).flush();
            accelSensitivity = value;
        }

    }

    public static void toggleFullScreenSetting(boolean isOn) {

        //If the setting changed, update the mobile device preferences

        if (fullScreenModeIsOn != isOn) {
            libgdxPrefs.putBoolean("isFullScreenOn", isOn).flush();
            fullScreenModeIsOn = isOn;
        }

    }

    public static void increaseTotalNumGames(){

        libgdxPrefs.putFloat("totalnumgames", libgdxPrefs.getFloat("totalnumgames") + 1).flush();

    }

    public static void setHighScore(float score){

        libgdxPrefs.putFloat("highscore", score);

    }

    public static void setUserName(String userInputtedName) {

        libgdxPrefs.putString("userName", userInputtedName).flush();
    }


}
