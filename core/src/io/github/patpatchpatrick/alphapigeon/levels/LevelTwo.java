package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.math.MathUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;

public class LevelTwo extends Level {

    // The second level of the game is considered the "EASY" level.
    // Although not actually easy, this level consists of random waves/puzzles of dodgeable creatures
    // that are randomly selected

    //RANDOM WAVE VARIABLES
    // Level Two consists of "EASY" waves of dodgeables that occur randomly
    private final int TOTAL_NUMBER_OF_WAVES = 3;
    private final float RANDOM_WAVE_UFO = 1f;
    private final float RANDOM_WAVE_UFO_SPAWN_DURATION = 10000;
    private final float RANDOM_WAVE_UFO_TOTAL_TIME = 10000;
    private final float RANDOM_WAVE_ALIEN_MISSILE = 2f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION = 2500;
    private final float RANDOM_WAVE_ALIEN_MISSILE_TOTAL_TIME = 10000;
    private final float RANDOM_WAVE_ROCKETS = 3f;
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
            randomWave = MathUtils.random(1, TOTAL_NUMBER_OF_WAVES);
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

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (currentTimeInMillis - ufos.getLastUfoSpawnTime() > RANDOM_WAVE_UFO_SPAWN_DURATION) {
            //Spawn a ufo that shoots an energy beam in a random direction
            ufos.spawnUfo(ufos.ENERGY_BEAM_RANDOM);
        }

        checkIfRandomWaveIsComplete(RANDOM_WAVE_UFO_TOTAL_TIME);

    }

    private void runRandomWaveAlienMissile() {

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (currentTimeInMillis - alienMissiles.getLastAlienMissileSpawnTime() > RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION) {
            alienMissiles.spawnAlienMissile(alienMissiles.SPAWN_DIRECTION_LEFTWARD);
        }

        checkIfRandomWaveIsComplete(RANDOM_WAVE_ALIEN_MISSILE_TOTAL_TIME);

    }

    private void runRandomWaveRockets() {

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (currentTimeInMillis - powerUps.getLastpowerUpShieldSpawnTime() > powerUpShieldInterval) {
            powerUps.spawnPowerUpShield();
        }
        if (currentTimeInMillis - rockets.getLastRocketSpawnTime() > RANDOM_WAVE_ROCKETS_SPAWN_DURATION) {
            rockets.spawnRocket();
        }

        checkIfRandomWaveIsComplete(RANDOM_WAVE_ROCKETS_TOTAL_TIME);

    }


}
