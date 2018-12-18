package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.math.MathUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;

public class LevelTwo extends Level {

    // GAME TIMES
    private float startTime = 0f;
    private float totalGameTime;
    private long currentTimeInMillis;
    private float powerUpShieldInterval;

    //RANDOM WAVE VARIABLES
    // Level Two consists of "EASY" waves of dodgeables that occur randomly
    //The randomWaveIsInitiated boolean is used to track whether or not a random wave is in progress
    private boolean randomWaveIsInitiated = false;
    private long lastRandomWaveStartTime = 0;
    private float randomWave = 0f;
    private final float RANDOM_WAVE_UFO = 0f;
    private final float RANDOM_WAVE_UFO_SPAWN_DURATION = 10000;
    private final float RANDOM_WAVE_UFO_TOTAL_TIME = 10000;
    private final float RANDOM_WAVE_ALIEN_MISSILE = 1f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION = 2500;
    private final float RANDOM_WAVE_ALIEN_MISSILE_TOTAL_TIME = 10000;
    private final float RANDOM_WAVE_ROCKETS = 2f;
    private final float RANDOM_WAVE_ROCKETS_SPAWN_DURATION = 4000;
    private final float RANDOM_WAVE_ROCKETS_TOTAL_TIME = 10000;
    private final float RANDOM_WAVE_L1BIRD_SPAWN_DURATION = 2000;
    private final float RANDOM_WAVE_L2BIRD_SPAWN_DURATION = 2000;

    public LevelTwo(Dodgeables dodgeables) {
        super(dodgeables);
    }

    public void run(float totalGameTime, long currentTimeInMillis, float powerUpShieldInterval){

        this.totalGameTime = totalGameTime;
        this.currentTimeInMillis = currentTimeInMillis;
        this.powerUpShieldInterval =  powerUpShieldInterval;

        if (!randomWaveIsInitiated) {
            //If a random isn't currently in progress:
            //Generate a random number to determine which random wave to run
            randomWave = MathUtils.random(0, 2);
            //Save the time the last random wave was started
            lastRandomWaveStartTime = currentTimeInMillis;
            randomWaveIsInitiated = true;
        } else if (randomWave == RANDOM_WAVE_UFO) {
            runRandomWaveUFO();
        } else if (randomWave == RANDOM_WAVE_ALIEN_MISSILE){
            runRandomWaveAlienMissile();
        } else if (randomWave == RANDOM_WAVE_ROCKETS){
            runRandomWaveRockets();
        }

    }


    private void runRandomWaveUFO() {

        if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > RANDOM_WAVE_L1BIRD_SPAWN_DURATION) {
            birds.spawnLevelOneBird(totalGameTime);
        }
        if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > RANDOM_WAVE_L2BIRD_SPAWN_DURATION) {
            birds.spawnLevelTwoBird(totalGameTime);
        }
        if (currentTimeInMillis - ufos.getLastUfoSpawnTime() > RANDOM_WAVE_UFO_SPAWN_DURATION) {
            ufos.spawnUfo();
        }
        if (currentTimeInMillis - lastRandomWaveStartTime > RANDOM_WAVE_UFO_TOTAL_TIME) {
            randomWaveIsInitiated = false;
        }

    }

    private void runRandomWaveAlienMissile() {

        if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > RANDOM_WAVE_L1BIRD_SPAWN_DURATION) {
            birds.spawnLevelOneBird(totalGameTime);
        }
        if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > RANDOM_WAVE_L2BIRD_SPAWN_DURATION) {
            birds.spawnLevelTwoBird(totalGameTime);
        }
        if (currentTimeInMillis - alienMissiles.getLastAlienMissileSpawnTime() > RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION) {
            alienMissiles.spawnAlienMissile();
        }
        if (currentTimeInMillis - lastRandomWaveStartTime > RANDOM_WAVE_ALIEN_MISSILE_TOTAL_TIME) {
            randomWaveIsInitiated = false;
        }

    }

    private void runRandomWaveRockets() {

        if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > RANDOM_WAVE_L1BIRD_SPAWN_DURATION) {
            birds.spawnLevelOneBird(totalGameTime);
        }
        if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > RANDOM_WAVE_L2BIRD_SPAWN_DURATION) {
            birds.spawnLevelTwoBird(totalGameTime);
        }
        if (currentTimeInMillis - powerUps.getLastpowerUpShieldSpawnTime() > powerUpShieldInterval) {
            powerUps.spawnPowerUpShield();
        }
        if (currentTimeInMillis - rockets.getLastRocketSpawnTime() > RANDOM_WAVE_ROCKETS_SPAWN_DURATION) {
            rockets.spawnRocket();
        }
        if (currentTimeInMillis - lastRandomWaveStartTime > RANDOM_WAVE_ROCKETS_TOTAL_TIME) {
            randomWaveIsInitiated = false;
        }

    }


}
