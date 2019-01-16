package io.github.patpatchpatrick.alphapigeon.resources;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class Sounds {

    //Sounds
    public static Sound birdSound = Gdx.audio.newSound(Gdx.files.internal("sounds/birdSound.mp3"));
    public static Sound powerUpSkullSound = Gdx.audio.newSound(Gdx.files.internal("sounds/powerUpSkull.mp3"));
    public static Sound notificationSound = Gdx.audio.newSound(Gdx.files.internal("sounds/notification.mp3"));
    public static Music backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/backgroundMusic.mp3"));


}
