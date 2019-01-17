package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Sounds {

    //OnOff
    public static boolean backgroundMusicOn = true;

    //Sounds
    public static Sound birdSound = Gdx.audio.newSound(Gdx.files.internal("sounds/birdSound.mp3"));
    public static Sound powerUpSkullSound = Gdx.audio.newSound(Gdx.files.internal("sounds/powerUpSkull.mp3"));
    public static Sound notificationSound = Gdx.audio.newSound(Gdx.files.internal("sounds/notification.mp3"));
    public static Sound gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sounds/gameOver.mp3"));
    public static Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/backgroundMusic.mp3"));

    public static void initializeBackgroundMusic(){
        setBackgroundMusic(backgroundMusicOn);
    }

    public static void setBackgroundMusic(boolean musicOn){

        //Turn background music on or off

        if (musicOn){
            backgroundMusicOn = true;
            backgroundMusic.setLooping(true);
            if (!backgroundMusic.isPlaying()){
                backgroundMusic.play();
            }
        } else {
            backgroundMusicOn = false;
            if (backgroundMusic.isPlaying()){
                backgroundMusic.stop();
            }
        }

    }

}
