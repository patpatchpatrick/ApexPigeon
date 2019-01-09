package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.math.MathUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;

public class LevelTwo extends Level {

    // The second level of the game is considered the "EASY" level.
    // Although not actually easy, this level consists of random waves/puzzles of dodgeable creatures
    // that are randomly selected
    // Level 2 uses shorter durations for waves than other levels (you'll notice in the random wave methods)

    //RANDOM WAVE VARIABLES
    // Level Two consists of "EASY" waves of dodgeables that occur randomly
    public final static int TOTAL_NUMBER_OF_WAVES = 3;
    private final float RANDOM_WAVE_UFO = 1f;
    private final float RANDOM_WAVE_UFO_SPAWN_DURATION = 10000;
    private final float RANDOM_WAVE_ALIEN_MISSILE = 2f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION = 2500;
    private final float RANDOM_WAVE_ROCKETS = 3f;
    private final float RANDOM_WAVE_ROCKETS_SPAWN_DURATION = 4000;
    private final float RANDOM_WAVE_L1BIRD_SPAWN_DURATION = 2000;
    private final float RANDOM_WAVE_L2BIRD_SPAWN_DURATION = 2000;

    public LevelTwo(Dodgeables dodgeables) {
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
            //Level 2 waves do not use standard durations by default
            //They use short durations
        } else if (randomWave == RANDOM_WAVE_UFO) {
            runRandomWaveUFO(false);
        } else if (randomWave == RANDOM_WAVE_ALIEN_MISSILE) {
            runRandomWaveAlienMissile(false);
        } else if (randomWave == RANDOM_WAVE_ROCKETS) {
            runRandomWaveRockets(false);
        }

    }

    public boolean runManualWave(float waveNumber, float totalGameTime, long currentTimeInMillis, float powerUpShieldInterval, boolean useStandardDuration) {

        this.totalGameTime = totalGameTime;
        this.currentTimeInMillis = currentTimeInMillis;
        this.powerUpShieldInterval = powerUpShieldInterval;

        //Manually run a wave
        // Return true if wave is complete
        if (!randomWaveIsInitiated) {
            //Save the time the last random wave was started
            lastRandomWaveStartTime = currentTimeInMillis;
            randomWaveIsInitiated = true;
            return false;
        } else if (waveNumber == RANDOM_WAVE_UFO) {
            return runRandomWaveUFO(useStandardDuration);
        } else if (waveNumber == RANDOM_WAVE_ALIEN_MISSILE) {
            return runRandomWaveAlienMissile(useStandardDuration);
        } else if (waveNumber == RANDOM_WAVE_ROCKETS) {
            return runRandomWaveRockets(useStandardDuration);
        } else {
            return false;
        }
    }


    private boolean runRandomWaveUFO(boolean useStandardDuration) {

        //Return true if wave is complete
        //useStandardDuration boolean determines if wave should be run with a standard or short duration

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (currentTimeInMillis - ufos.getLastUfoSpawnTime(2) > RANDOM_WAVE_UFO_SPAWN_DURATION) {
            //Spawn a ufo that shoots an energy beam in a random direction
            ufos.spawnUfo(ufos.ENERGY_BEAM_RANDOM, 2);
        }

        if (useStandardDuration){
            return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);
        } else {
            return checkIfRandomWaveIsComplete(RANDOM_WAVE_SHORT_DURATION);
        }
    }

    private boolean runRandomWaveAlienMissile(boolean useStandardDuration) {

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (currentTimeInMillis - alienMissiles.getLastAlienMissileSpawnTime(2) > RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION) {
            alienMissiles.spawnAlienMissile(alienMissiles.SPAWN_DIRECTION_LEFTWARD, 2);
        }

        if (useStandardDuration){
            return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);
        } else {
            return checkIfRandomWaveIsComplete(RANDOM_WAVE_SHORT_DURATION);
        }

    }

    private boolean runRandomWaveRockets(boolean useStandardDuration) {

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (currentTimeInMillis - powerUps.getLastpowerUpShieldSpawnTime() > powerUpShieldInterval) {
            powerUps.spawnPowerUpShield();
        }
        if (currentTimeInMillis - rockets.getLastRocketSpawnTime(2) > RANDOM_WAVE_ROCKETS_SPAWN_DURATION) {
            rockets.spawnRocket(2);
        }

        if (useStandardDuration){
            return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);
        } else {
            return checkIfRandomWaveIsComplete(RANDOM_WAVE_SHORT_DURATION);
        }

    }


}
