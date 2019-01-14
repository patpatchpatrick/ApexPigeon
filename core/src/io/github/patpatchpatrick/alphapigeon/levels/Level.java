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

    // WAVE VARIABLES
    // Used to keep track of whether or not a random wave has started, and when
    protected boolean waveIsInitiated = false;
    protected final float NO_WAVE = 0f;
    protected float waveToRun;
    protected long lastWaveStartTime = 0;
    protected final float RANDOM_WAVE_STANDARD_DURATION = 30000f;
    protected final float RANDOM_WAVE_SHORT_DURATION = 15000f;
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
    protected final float LEVEL_ONE_END_TIME = 1f;
    //L2 is "easy" difficulty
    //End time should be 120000f
    protected final float LEVEL_TWO_START_TIME = LEVEL_ONE_END_TIME;
    protected final float LEVEL_TWO_END_TIME = 2f;
    //L3 is "medium" difficulty
    //End time shoudl be 180000f
    protected final float LEVEL_THREE_START_TIME = LEVEL_TWO_END_TIME;
    protected final float LEVEL_THREE_END_TIME = 6f;
    //L4 is "hard" difficulty
    //End time should be 260000f
    protected final float LEVEL_FOUR_START_TIME = LEVEL_THREE_END_TIME;
    protected final float LEVEL_FOUR_END_TIME = 7f;
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
        if (currentTimeInMillis - lastWaveStartTime > randomWaveDuration) {
            waveIsInitiated = false;
            return true;
        }
        return false;
    }


}
