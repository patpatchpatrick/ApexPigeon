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
    private final int TOTAL_NUMBER_OF_WAVES = 5;
    private final float RANDOM_WAVE_UFO_HORIZONTAL = 1f;
    private final float RANDOM_WAVE_UFO_HORIZ_SPAWN_DURATION = 30000f;
    private final float RANDOM_WAVE_UFO_HORIZ_TOTAL_TIME = 30000f;
    private final float RANDOM_WAVE_UFO_VERTICAL = 2f;
    private final float RANDOM_WAVE_UFO_VERT_SPAWN_DURATION = 30000f;
    private final float RANDOM_WAVE_UFO_VERT_TOTAL_TIME = 30000f;
    private final float RANDOM_WAVE_METEORS = 3f;
    private final float RANDOM_WAVE_METEORS_SPAWN_DURATION = 2000f;
    private final float RANDOM_WAVE_METEORS_TOTAL_TIME = 20000f;
    private final float RANDOM_WAVE_MISSILES = 4f;
    private final float RANDOM_WAVE_MISSILES_SPAWN_DURATION = 2000f;
    private final float RANDOM_WAVE_MISSILES_TOTAL_TIME = 20000f;
    private final float RANDOM_WAVE_UFO_CENTER = 5f;
    private final float RANDOM_WAVE_UFO_CENTER_SPAWN_DURATION = 30000f;
    private final float RANDOM_WAVE_UFO_CENTER_TOTAL_TIME = 30000f;
    private final float RANDOM_WAVE_L1BIRD_SPAWN_DURATION = 2000;
    private final float RANDOM_WAVE_L2BIRD_SPAWN_DURATION = 2000;

    public LevelThree(Dodgeables dodgeables) {
        super(dodgeables);
    }

    public void run(float totalGameTime, long currentTimeInMillis, float powerUpShieldInterval) {

        this.totalGameTime = totalGameTime;
        this.currentTimeInMillis = currentTimeInMillis;
        this.powerUpShieldInterval = powerUpShieldInterval;

        if (!randomWaveIsInitiated) {
            //If a random isn't currently in progress:
            //Generate a random number to determine which random wave to run
            randomWave = MathUtils.random(1, TOTAL_NUMBER_OF_WAVES);
            //Save the time the last random wave was started
            lastRandomWaveStartTime = currentTimeInMillis;
            randomWaveIsInitiated = true;
        } else if (randomWave == RANDOM_WAVE_UFO_HORIZONTAL) {
            runRandomWaveHorizontalUFO();
        } else if (randomWave == RANDOM_WAVE_UFO_VERTICAL) {
            runRandomWaveVerticalUFO();
        } else if (randomWave == RANDOM_WAVE_METEORS) {
            runRandomWaveMeteors();
        } else if (randomWave == RANDOM_WAVE_MISSILES) {
            runRandomWaveMissiles();
        } else if (randomWave == RANDOM_WAVE_UFO_CENTER) {
            runRandomWaveCenterUFO();
        }

    }

    private void runRandomWaveCenterUFO(){

        if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > RANDOM_WAVE_L1BIRD_SPAWN_DURATION) {
            birds.spawnLevelOneBird(totalGameTime);
        }
        if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > RANDOM_WAVE_L2BIRD_SPAWN_DURATION) {
            birds.spawnLevelTwoBird(totalGameTime);
        }

        if (currentTimeInMillis - ufos.getLastUfoSpawnTime() > RANDOM_WAVE_UFO_CENTER_SPAWN_DURATION) {

            //Spawn a UFO that stops in center and shoots beams in all directions for a specified
            //amount of time
            ufos.spawnStopInCenterUfo(ufos.ENERGY_BEAM_ALL_DIRECTIONS, 5);
        }

        if (currentTimeInMillis - lastRandomWaveStartTime > RANDOM_WAVE_UFO_CENTER_TOTAL_TIME) {
            randomWaveIsInitiated = false;
        }

    }

    private void runRandomWaveVerticalUFO() {

        if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > RANDOM_WAVE_L1BIRD_SPAWN_DURATION) {
            birds.spawnLevelOneBird(totalGameTime);
        }
        if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > RANDOM_WAVE_L2BIRD_SPAWN_DURATION) {
            birds.spawnLevelTwoBird(totalGameTime);
        }

        if (currentTimeInMillis - ufos.getLastUfoSpawnTime() > RANDOM_WAVE_UFO_VERT_SPAWN_DURATION) {

            //Spawn a UFO that travels vertically from top to bottom and shoots a straight vertical
            //line of energy beams (i.e. a top and bottom beam)
            ufos.spawnVerticalUfo(ufos.ENERGY_BEAM_VERTICAL_DIRECTIONS);
        }

        if (currentTimeInMillis - lastRandomWaveStartTime > RANDOM_WAVE_UFO_VERT_TOTAL_TIME) {
            randomWaveIsInitiated = false;
        }



    }

    private void runRandomWaveMissiles() {
        //Spawn both regular rockets and alien missiles
        if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > RANDOM_WAVE_L1BIRD_SPAWN_DURATION) {
            birds.spawnLevelOneBird(totalGameTime);
        }
        if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > RANDOM_WAVE_L2BIRD_SPAWN_DURATION) {
            birds.spawnLevelTwoBird(totalGameTime);
        }
        if (currentTimeInMillis - rockets.getLastRocketSpawnTime() > RANDOM_WAVE_MISSILES_SPAWN_DURATION) {
            rockets.spawnRocket();
        }
        if (currentTimeInMillis - alienMissiles.getLastAlienMissileSpawnTime() > RANDOM_WAVE_MISSILES_SPAWN_DURATION) {
            alienMissiles.spawnAlienMissile();
        }
        if (currentTimeInMillis - lastRandomWaveStartTime > RANDOM_WAVE_MISSILES_TOTAL_TIME) {
            randomWaveIsInitiated = false;
        }


    }

    private void runRandomWaveMeteors() {
        if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > RANDOM_WAVE_L1BIRD_SPAWN_DURATION) {
            birds.spawnLevelOneBird(totalGameTime);
        }
        if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > RANDOM_WAVE_L2BIRD_SPAWN_DURATION) {
            birds.spawnLevelTwoBird(totalGameTime);
        }

        if (currentTimeInMillis - meteors.getLastMeteorSpawnTime() > RANDOM_WAVE_METEORS_SPAWN_DURATION) {
            //Spawn meteors
            meteors.spawnMeteor();
        }

        if (currentTimeInMillis - lastRandomWaveStartTime > RANDOM_WAVE_METEORS_TOTAL_TIME) {
            randomWaveIsInitiated = false;
        }

    }

    private void runRandomWaveHorizontalUFO() {

        if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > RANDOM_WAVE_L1BIRD_SPAWN_DURATION) {
            birds.spawnLevelOneBird(totalGameTime);
        }
        if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > RANDOM_WAVE_L2BIRD_SPAWN_DURATION) {
            birds.spawnLevelTwoBird(totalGameTime);
        }

        if (currentTimeInMillis - ufos.getLastUfoSpawnTime() > RANDOM_WAVE_UFO_HORIZ_SPAWN_DURATION) {

            //Spawn a UFO that travels horizontally from right to left and shoots a straight horizontal
            //line of energy beams (i.e. a left and right beam)
            ufos.spawnHorizontalUfo(ufos.ENERGY_BEAM_HORIZONAL_DIRECTIONS);
        }

        if (currentTimeInMillis - lastRandomWaveStartTime > RANDOM_WAVE_UFO_HORIZ_TOTAL_TIME) {
            randomWaveIsInitiated = false;
        }

    }
}
