package io.github.patpatchpatrick.alphapigeon.resources;

public class SettingsManager {


    //Class to manage settings and updates to settings via mobile device database/preferences

    public static DatabaseAndPreferenceManager databaseAndPreferenceManager;

    //Booleans for Settings
    public static boolean musicSettingIsOn = false;
    public static boolean gameSoundsSettingIsOn = false;
    public static boolean touchSettingIsOn = false;
    public static boolean accelerometerSettingIsOn = false;

    public static void updateSettings(){

        //Fetch current settings from the mobile device database/preferences

        if (databaseAndPreferenceManager != null){

            musicSettingIsOn = databaseAndPreferenceManager.isMusicOn();
            gameSoundsSettingIsOn = databaseAndPreferenceManager.isGameSoundsOn();
            touchSettingIsOn = databaseAndPreferenceManager.isTouchControlsOn();
            accelerometerSettingIsOn = databaseAndPreferenceManager.isAccelButtonOn();

        }

    }

    public static void toggleMusicSetting(Boolean isOn){

        //If the setting changed, update the mobile device preferences

        if (musicSettingIsOn != isOn && databaseAndPreferenceManager != null){
            databaseAndPreferenceManager.toggleMusicOnOff(isOn);
            musicSettingIsOn = isOn;
            Sounds.toggleBackgroundMusic(isOn);
        }

    }

    public static void toggleGameSoundsSetting(Boolean isOn){

        //If the setting changed, update the mobile device preferences

        if (gameSoundsSettingIsOn != isOn && databaseAndPreferenceManager != null){
            databaseAndPreferenceManager.toggleGameSoundsOnOff(isOn);
            gameSoundsSettingIsOn = isOn;
        }

    }

    public static void toggleTouchSetting(Boolean isOn){

        //If the setting changed, update the mobile device preferences

        if (touchSettingIsOn != isOn && databaseAndPreferenceManager != null){
            databaseAndPreferenceManager.toggleTouchControlsOnOff(isOn);
            touchSettingIsOn = isOn;
        }

    }

    public static void toggleAccelerometerSetting(Boolean isOn){

        //If the setting changed, update the mobile device preferences

        if (accelerometerSettingIsOn != isOn && databaseAndPreferenceManager != null){
            databaseAndPreferenceManager.toggleAccelButtonOnOff(isOn);
            accelerometerSettingIsOn = isOn;
        }

    }



}
