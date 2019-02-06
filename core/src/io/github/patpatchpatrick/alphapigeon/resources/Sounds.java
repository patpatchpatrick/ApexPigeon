package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;

public class Sounds {

    private static Boolean backgroundMusicInitialized = false;

    //SOUNDS (Game Sound Effects and Background Music)
    //Array list of all of the active sounds currently playing in the game
    public static ArrayList<Sound> activeSounds = new ArrayList<Sound>();
    //..Background Music
    public static Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/backgroundMusic.mp3"));
    //..Bird
    public static Sound birdSound = Gdx.audio.newSound(Gdx.files.internal("sounds/birdSound.mp3"));
    //..PowerUps
    public static Sound powerUpSkullSound = Gdx.audio.newSound(Gdx.files.internal("sounds/powerUpSkull.mp3"));
    public static Sound powerUpShieldSound = Gdx.audio.newSound(Gdx.files.internal("sounds/powerUpShield.mp3"));
    public static Sound powerUpShieldZapSound = Gdx.audio.newSound(Gdx.files.internal("sounds/powerUpShieldZap.mp3"));
    //..Teleports
    public static Sound teleportSound = Gdx.audio.newSound(Gdx.files.internal("sounds/teleportSound.mp3"));
    //..Notifications
    public static Sound notificationSound = Gdx.audio.newSound(Gdx.files.internal("sounds/notification.mp3"));
    //..Game Over
    public static Sound gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/gameOver.mp3"));
    //..High Score
    public static Sound newHighScoreSound = Gdx.audio.newSound(Gdx.files.internal("sounds/newHighScore.mp3"));
    //..UFO
    public static Sound ufoFlyingSound = Gdx.audio.newSound(Gdx.files.internal("sounds/ufoFlying.mp3"));
    public static Sound ufoEnergyBallSound = Gdx.audio.newSound(Gdx.files.internal("sounds/ufoEnergyBall.mp3"));
    public static Sound ufoEnergyBeamSound = Gdx.audio.newSound(Gdx.files.internal("sounds/ufoEnergyBeam.mp3"));
    //..Rockets
    public static Sound rocketSpawnSound = Gdx.audio.newSound(Gdx.files.internal("sounds/rocketSpawn.wav"));
    public static Sound rocketExplosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/rocketExplosion.wav"));
    //..Meteors
    public static Sound meteorSound = Gdx.audio.newSound(Gdx.files.internal("sounds/meteor.mp3"));
    //..Alien Missiles
    public static Sound alienMissileExplosionSound = Gdx.audio.newSound(Gdx.files.internal("sounds/alienMissileExplosion.mp3"));


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

    public static void pause(){
        //When game is paused, pause all actively playing sounds
        for (Sound sound : activeSounds){
            sound.pause();
        }
        if (SettingsManager.musicSettingIsOn){
            backgroundMusic.pause();
        }
    }

    public static void resume(){
        //When game is resumed, resume all actively playing sounds
        for (Sound sound: activeSounds){
            sound.resume();
        }
        if (SettingsManager.musicSettingIsOn){
            backgroundMusic.play();
        }

    }

    public static void setBackgroundMusicVolume(float value) {
        backgroundMusic.setVolume(value);
    }

    public static void dispose() {
        backgroundMusic.dispose();
        birdSound.dispose();
        powerUpSkullSound.dispose();
        powerUpShieldSound.dispose();
        powerUpShieldZapSound.dispose();
        teleportSound.dispose();
        notificationSound.dispose();
        gameOverSound.dispose();
        newHighScoreSound.dispose();
        ufoFlyingSound.dispose();
        ufoEnergyBallSound.dispose();
        ufoEnergyBeamSound.dispose();
        rocketSpawnSound.dispose();
        rocketExplosionSound.dispose();
        meteorSound.dispose();
        alienMissileExplosionSound.dispose();

    }


}
