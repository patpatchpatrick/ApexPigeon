package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.AlienMissiles;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Birds;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Meteors;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Notifications;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Notifications.ExclamationMark;
import io.github.patpatchpatrick.alphapigeon.dodgeables.PowerUps;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Rockets;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Teleports;
import io.github.patpatchpatrick.alphapigeon.dodgeables.UFOs;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

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

    // RANDOM WAVE VARIABLES
    // Used to keep track of whether or not a random wave has started, and when
    protected boolean randomWaveIsInitiated = false;
    protected long lastRandomWaveStartTime = 0;
    protected float randomWave = 0f;
    protected final float RANDOM_WAVE_STANDARD_DURATION = 30000f;
    protected final float RANDOM_WAVE_SHORT_DURATION = 10000f;
    protected final float RANDOM_WAVE_LONG_DURATION = 60000f;

    // GAME TIMES
    protected float startTime = 0f;
    protected float totalGameTime;
    protected long currentTimeInMillis;
    protected float powerUpShieldInterval;

    //LEVEL TIMES in milliseconds
    protected final float LEVEL_ONE_START_TIME = 0f;
    //L1W1 is only level 1 birds,  L1W2 is level 1 and 2 birds
    //Start time should be 0f and end time should be 40000f
    protected final float LEVEL_ONE_WAVE_1 = 20000f;
    protected final float LEVEL_ONE_END_TIME = 40000f;
    //L2 is "easy" difficulty
    //End time should be 120000f
    protected final float LEVEL_TWO_START_TIME = LEVEL_ONE_END_TIME;
    protected final float LEVEL_TWO_END_TIME = 120000f;
    //L3 is "medium" difficulty
    protected final float LEVEL_THREE_START_TIME = LEVEL_TWO_END_TIME;
    protected final float LEVEL_THREE_END_TIME = 180000f;
    //L4 is "hard" difficulty
    protected final float LEVEL_FOUR_START_TIME = LEVEL_THREE_END_TIME;
    protected final float LEVEL_FOUR_END_TIME = 260000f;
    //Level Final is "insane" difficulty.  It is a continuous level and the last level of the game
    protected final float LEVEL_FINAL_START_TIME = LEVEL_FOUR_END_TIME;


    public Level(Dodgeables dodgeables) {

        this.dodgeables = dodgeables;
        this.birds = dodgeables.getBirds();
        this.rockets = dodgeables.getRockets();
        this.alienMissiles = dodgeables.getAlienMissiles();
        this.teleports = dodgeables.getTeleports();
        this.powerUps = dodgeables.getPowerUps();
        this.meteors = dodgeables.getMeteors();
        this.ufos = dodgeables.getUfos();

    }


    // METHODS USED BY ALL LEVELS:

    protected void spawnBirds(float levelOneBirdSpawnDuration, float levelTwoBirdSpawnDuration) {
        //Method shared by all of the levels
        //Spawns level one and level two birds based on inputted spawn durations

        if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > levelOneBirdSpawnDuration) {
            birds.spawnLevelOneBird(totalGameTime);
        }
        if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > levelTwoBirdSpawnDuration) {
            birds.spawnLevelTwoBird(totalGameTime);
        }

    }

    protected void spawnReverseBirds(float levelOneBirdSpawnDuration, float levelTwoBirdSpawnDuration) {
        //Method shared by all of the levels
        //Spawns level one and level two birds based on inputted spawn durations
        //Birds are spawned flying in reverse direction
        if (currentTimeInMillis - birds.getLastLevelOneReverseBirdSpawnTime() > levelOneBirdSpawnDuration) {
            birds.spawnLevelOneBirdReverse(totalGameTime);
        }
        if (currentTimeInMillis - birds.getLastLevelTwoReverseBirdSpawnTime() > levelTwoBirdSpawnDuration) {
            birds.spawnLevelTwoBirdReverse(totalGameTime);
        }
    }

    protected boolean checkIfRandomWaveIsComplete(float randomWaveDuration) {
        //Check if a wave is complete, if so, mark randomWaveInitiated as false so that a new random
        // wave will be run
        // Return true if random wave is complete
        if (currentTimeInMillis - lastRandomWaveStartTime > randomWaveDuration) {
            randomWaveIsInitiated = false;
            return true;
        }
        return false;
    }


}
