package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Sounds {

    private static Boolean backgroundMusicInitialized = false;

    //Sounds
    public static Sound birdSound = Gdx.audio.newSound(Gdx.files.internal("sounds/birdSound.mp3"));
    public static Sound powerUpSkullSound = Gdx.audio.newSound(Gdx.files.internal("sounds/powerUpSkull.mp3"));
    public static Sound notificationSound = Gdx.audio.newSound(Gdx.files.internal("sounds/notification.mp3"));
    public static Sound gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/gameOver.mp3"));
    public static Sound newHighScoreSound = Gdx.audio.newSound(Gdx.files.internal("sounds/newHighScore.mp3"));
    public static Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/backgroundMusic.mp3"));

    public static void initializeBackgroundMusic() {
        //If background music has not been initialized, initialize it
        if (!backgroundMusicInitialized) {
            toggleBackgroundMusic(SettingsManager.musicSettingIsOn);
        }
    }

    public static void toggleBackgroundMusic(boolean musicOn) {

        //Turn background music on or off

        if (musicOn) {
            backgroundMusic.setLooping(true);
            if (!backgroundMusic.isPlaying()) {
                backgroundMusic.setVolume(SettingsManager.musicVolume); //Set volume based on player settings
                backgroundMusic.play();
            }
        } else {
            if (backgroundMusic.isPlaying()) {
                backgroundMusic.stop();
            }
        }

    }

    public static void setBackgroundMusicVolume(float value){
        backgroundMusic.setVolume(value);
    }


}
