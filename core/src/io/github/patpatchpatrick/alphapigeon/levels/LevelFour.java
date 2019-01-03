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
    private final int TOTAL_NUMBER_OF_WAVES = 4;
    private final float RANDOM_WAVE_TELEPORT_MADNESS = 1f;
    private final float RANDOM_WAVE_TELEPORT_MADNESS_L1BIRD_DURATION = 700f;
    private final float RANDOM_WAVE_TELEPORT_SPAWN_DURATION = 4000f;
    private final float RANDOM_WAVE_TELEPORT_MADNESS_TOTAL_TIME = 15000f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_MADNESS = 2f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION = 5000f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_MADNESS_TOTAL_TIME = 15000f;
    private final float RANDOM_WAVE_UFOS_IN_CORNERS = 3f;
    private final float RANDOM_WAVE_UFOS_IN_CORNERS_BIRD_DURATION = 2000f;
    private boolean randomWaveCornerUfosAreSpawned = false;
    private final float RANDOM_WAVE_UFOS_IN_CORNERS_TOTAL_TIME = 30000f;
    private final float RANDOM_WAVE_ROCKET_MADNESS = 4f;
    private final float RANDOM_WAVE_ROCKET_MADNESS_L1BIRD_DURATION = 400f;
    private final float RANDOM_WAVE_ROCKET_MADNESS_SPAWN_DURATION = 400f;
    private final float RANDOM_WAVE_ROCKET_MADNESS_TOTAL_TIME = 15000f;


    public LevelFour(Dodgeables dodgeables){
        super(dodgeables);
    }

    public void run(float totalGameTime, long currentTimeInMillis, float powerUpShieldInterval) {

        this.totalGameTime = totalGameTime;
        this.currentTimeInMillis = currentTimeInMillis;
        this.powerUpShieldInterval = powerUpShieldInterval;

        if (!randomWaveIsInitiated) {
            resetWaveVariables();
            //If a random isn't currently in progress:
            //Generate a random number to determine which random wave to run
            randomWave = MathUtils.random(4, 4);
            //Save the time the last random wave was started
            lastRandomWaveStartTime = currentTimeInMillis;
            randomWaveIsInitiated = true;
        } else if (randomWave == RANDOM_WAVE_TELEPORT_MADNESS) {
            runRandomWaveTeleportMadness();
        } else if (randomWave == RANDOM_WAVE_ALIEN_MISSILE_MADNESS){
            runRandomWaveAlienMissileMadness();
        } else if (randomWave == RANDOM_WAVE_UFOS_IN_CORNERS){
            runRandomWaveUFOsInCorners();
        } else if (randomWave == RANDOM_WAVE_ROCKET_MADNESS){
            runRandomWaveRocketMadness();
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

    private void runRandomWaveUFOsInCorners(){
        // Spawn two ufos in the top-right and bottom-left corners of the screen
        // The ufos shoot energy beams in all directions, causing the entire border of screen to be blocked off by beams

        spawnBirds(RANDOM_WAVE_UFOS_IN_CORNERS_BIRD_DURATION, RANDOM_WAVE_UFOS_IN_CORNERS_BIRD_DURATION);

        if (!randomWaveCornerUfosAreSpawned) {
            //Spawn corner UFOs and hold them in the corner for 30 seconds
            ufos.spawnTopRightCornerUfo(ufos.ENERGY_BEAM_ALL_DIRECTIONS, 20);
            ufos.spawnBottomLeftCornerUfo(ufos.ENERGY_BEAM_ALL_DIRECTIONS, 20);
            randomWaveCornerUfosAreSpawned = true;
        }

        checkIfRandomWaveIsComplete(RANDOM_WAVE_UFOS_IN_CORNERS_TOTAL_TIME);


    }

    private void runRandomWaveRocketMadness(){

        // Spawn level one birds and also lots of rockets to explode the birds

        spawnBirds(RANDOM_WAVE_ROCKET_MADNESS_L1BIRD_DURATION, 100000);

        if (currentTimeInMillis - rockets.getLastRocketSpawnTime() > RANDOM_WAVE_ROCKET_MADNESS_SPAWN_DURATION) {
            rockets.spawnRocket();
        }

        checkIfRandomWaveIsComplete(RANDOM_WAVE_ROCKET_MADNESS_TOTAL_TIME);


    }

    private void resetWaveVariables() {
        randomWaveCornerUfosAreSpawned = false;
    }


}
