package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.math.MathUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;

public class LevelThree extends Level {

    // The third level of the game is considered the "MEDIUM" level.
    // This is the level where things begin to get very difficult
    // This level consists of random waves/puzzles of dodgeable creatures
    // that are randomly selected

    // GAME TIMES
    private float startTime = 0f;
    private float totalGameTime;
    private long currentTimeInMillis;
    private float powerUpShieldInterval;

    //RANDOM WAVE VARIABLES
    // Level Three consists of "MEDIUM" difficulty waves of dodgeables that occur randomly
    //The randomWaveIsInitiated boolean is used to track whether or not a random wave is in progress
    private boolean randomWaveIsInitiated = false;
    private long lastRandomWaveStartTime = 0;
    private float randomWave = 0f;
    private final int TOTAL_NUMBER_OF_WAVES = 2;
    private final float RANDOM_WAVE_UFO_IN_MIDDLE = 0f;
    private final float RANDOM_WAVE_METEORS = 1f;

    public LevelThree(Dodgeables dodgeables) {
        super(dodgeables);
    }

    public void run(float totalGameTime, long currentTimeInMillis, float powerUpShieldInterval){

        this.totalGameTime = totalGameTime;
        this.currentTimeInMillis = currentTimeInMillis;
        this.powerUpShieldInterval =  powerUpShieldInterval;

        if (!randomWaveIsInitiated) {
            //If a random isn't currently in progress:
            //Generate a random number to determine which random wave to run
            randomWave = MathUtils.random(0, TOTAL_NUMBER_OF_WAVES);
            //Save the time the last random wave was started
            lastRandomWaveStartTime = currentTimeInMillis;
            randomWaveIsInitiated = true;
        } else if (randomWave == RANDOM_WAVE_UFO_IN_MIDDLE) {
            //runRandomWaveUFO();
        } else if (randomWave == RANDOM_WAVE_METEORS){

        }

    }
}
