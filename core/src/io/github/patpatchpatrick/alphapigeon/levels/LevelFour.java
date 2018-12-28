package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.math.MathUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Dodgeable;

public class LevelFour extends Level {

    // The fourth level of the game is considered the "HARD" level.
    // This level is incredibly difficult
    // This level consists of random waves/puzzles of dodgeable creatures
    // that are randomly selected

    //RANDOM WAVE VARIABLES
    private final int TOTAL_NUMBER_OF_WAVES = 2;
    private final float RANDOM_WAVE_TELEPORT_MADNESS = 1f;
    private final float RANDOM_WAVE_TELEPORT_MADNESS_L1BIRD_DURATION = 500f;
    private final float RANDOM_WAVE_TELEPORT_SPAWN_DURATION = 3000f;
    private final float RANDOM_WAVE_TELEPORT_MADNESS_TOTAL_TIME = 15000f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_MADNESS = 2f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION = 5000f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_MADNESS_TOTAL_TIME = 15000f;


    public LevelFour(Dodgeables dodgeables){
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
        } else if (randomWave == RANDOM_WAVE_TELEPORT_MADNESS) {
            runRandomWaveTeleportMadness();
        } else if (randomWave == RANDOM_WAVE_ALIEN_MISSILE_MADNESS){
            runRandomWaveAlienMissileMadness();
        }

    }

    private void runRandomWaveTeleportMadness() {

        // Spawn loads of teleports and level one birds
        spawnBirds(RANDOM_WAVE_TELEPORT_MADNESS_L1BIRD_DURATION, 100000);

        if (currentTimeInMillis - teleports.getLastTeleportSpawnTime() > RANDOM_WAVE_TELEPORT_SPAWN_DURATION) {
            teleports.spawnTeleports();
        }

        checkIfRandomWaveIsComplete(RANDOM_WAVE_TELEPORT_MADNESS_TOTAL_TIME);

    }

    private void runRandomWaveAlienMissileMadness(){

        // Spawn loads of alien missiles
        if (currentTimeInMillis - alienMissiles.getLastAlienMissileSpawnTime() > RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION) {
            alienMissiles.spawnAlienMissile(alienMissiles.SPAWN_DIRECTION_LEFTWARD);
            alienMissiles.spawnAlienMissile(alienMissiles.SPAWN_DIRECTION_UPWARD);
            alienMissiles.spawnAlienMissile(alienMissiles.SPAWN_DIRECTION_RIGHTWARD);
            alienMissiles.spawnAlienMissile(alienMissiles.SPAWN_DIRECTION_DOWNWARD);
        }

        checkIfRandomWaveIsComplete(RANDOM_WAVE_ALIEN_MISSILE_MADNESS_TOTAL_TIME);


    }


}
