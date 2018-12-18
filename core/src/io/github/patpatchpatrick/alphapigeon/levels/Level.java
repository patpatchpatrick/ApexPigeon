package io.github.patpatchpatrick.alphapigeon.levels;

import io.github.patpatchpatrick.alphapigeon.dodgeables.AlienMissiles;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Birds;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Meteors;
import io.github.patpatchpatrick.alphapigeon.dodgeables.PowerUps;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Rockets;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Teleports;
import io.github.patpatchpatrick.alphapigeon.dodgeables.UFOs;

public abstract class Level {

    // All dodgeables used in the game
    protected Dodgeables dodgeables;
    protected Birds birds;
    protected Rockets rockets;
    protected AlienMissiles alienMissiles;
    protected Teleports teleports;
    protected PowerUps powerUps;
    protected Meteors meteors;
    protected UFOs ufos;

    //LEVEL TIMES in milliseconds
    protected final float LEVEL_ONE_START_TIME = 0f;
    protected final float LEVEL_ONE_WAVE_1 = 20000f;
    protected final float LEVEL_ONE_END_TIME = 40000f;
    protected final float LEVEL_TWO_START_TIME = LEVEL_ONE_END_TIME;

    public Level(Dodgeables dodgeables){

        this.dodgeables =  dodgeables;
        this.birds =  dodgeables.getBirds();
        this.rockets = dodgeables.getRockets();
        this.alienMissiles = dodgeables.getAlienMissiles();
        this.teleports =  dodgeables.getTeleports();
        this.powerUps = dodgeables.getPowerUps();
        this.meteors = dodgeables.getMeteors();
        this.ufos = dodgeables.getUfos();

    }

}
