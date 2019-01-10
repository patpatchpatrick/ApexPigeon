package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.math.MathUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Dodgeable;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Notifications;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Notifications.ExclamationMark;

public class LevelFour extends Level {

    // The fourth level of the game is considered the "HARD" level.
    // This level is incredibly difficult
    // This level consists of random waves/puzzles of dodgeable creatures
    // that are randomly selected

    //RANDOM WAVE VARIABLES
    public final static int TOTAL_NUMBER_OF_WAVES = 6;
    private final float RANDOM_WAVE_TELEPORT_MADNESS = 1f;
    private final float RANDOM_WAVE_TELEPORT_MADNESS_L1BIRD_DURATION = 700f;
    private final float RANDOM_WAVE_TELEPORT_SPAWN_DURATION = 4000f;
    private final float RANDOM_WAVE_TELEPORT_MADNESS_TOTAL_TIME = 15000f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_MADNESS = 2f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION = 5000f;
    private final float RANDOM_WAVE_UFOS_IN_CORNERS = 3f;
    private final float RANDOM_WAVE_UFOS_IN_CORNERS_BIRD_DURATION = 2000f;
    private boolean randomWaveCornerUfosAreSpawned = false;
    private final float RANDOM_WAVE_ROCKET_MADNESS = 4f;
    private final float RANDOM_WAVE_ROCKET_MADNESS_L1BIRD_DURATION = 800f;
    private final float RANDOM_WAVE_ROCKET_MADNESS_SPAWN_DURATION = 800f;
    private final float RANDOM_WAVE_BIRD_MADNESS = 5f;
    private final float RANDOM_WAVE_BIRD_MADNESS_L1BIRD_DURATION = 1600f;
    private final float RANDOM_WAVE_BIRD_MADNESS_L2BIRD_DURATION = 3200f;
    private final float RANDOM_WAVE_UFO_MAZE = 6f;
    private final float RANDOM_WAVE_UFO_MAZE_SPAWN_DURATION = 7500f;
    private final float RANDOM_WAVE_UFO_MAZE_L1BIRD_DURATION = 2000f;
    private final float RANDOM_WAVE_UFO_MAZE_L2BIRD_DURATION = 2000f;
    private float randomWaveUfoMazeBeamDirection = ufos.ENERGY_BEAM_DOWN;


    public LevelFour(Dodgeables dodgeables) {
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
            randomWave = MathUtils.random(1, TOTAL_NUMBER_OF_WAVES);
            //Save the time the last random wave was started
            lastRandomWaveStartTime = currentTimeInMillis;
            randomWaveIsInitiated = true;
        } else if (randomWave == RANDOM_WAVE_TELEPORT_MADNESS) {
            runRandomWaveTeleportMadness();
        } else if (randomWave == RANDOM_WAVE_ALIEN_MISSILE_MADNESS) {
            runRandomWaveAlienMissileMadness();
        } else if (randomWave == RANDOM_WAVE_UFOS_IN_CORNERS) {
            runRandomWaveUFOsInCorners();
        } else if (randomWave == RANDOM_WAVE_ROCKET_MADNESS) {
            runRandomWaveRocketMadness();
        } else if (randomWave == RANDOM_WAVE_BIRD_MADNESS) {
            runRandomWaveBirdMadness();
        } else if (randomWave == RANDOM_WAVE_UFO_MAZE) {
            runRandomWaveUfoMaze(false);
        }

    }

    public boolean runManualWave(float waveNumber, float totalGameTime, long currentTimeInMillis, float powerUpShieldInterval, boolean useStandardDuration) {

        this.totalGameTime = totalGameTime;
        this.currentTimeInMillis = currentTimeInMillis;
        this.powerUpShieldInterval = powerUpShieldInterval;

        //Manually run a wave
        // Return true if wave is complete
        if (!randomWaveIsInitiated) {
            resetWaveVariables();
            //Save the time the last random wave was started
            lastRandomWaveStartTime = currentTimeInMillis;
            randomWaveIsInitiated = true;
            return false;
        } else if (waveNumber == RANDOM_WAVE_TELEPORT_MADNESS) {
            return runRandomWaveTeleportMadness();
        } else if (waveNumber == RANDOM_WAVE_ALIEN_MISSILE_MADNESS) {
            return runRandomWaveAlienMissileMadness();
        } else if (waveNumber == RANDOM_WAVE_UFOS_IN_CORNERS) {
            return runRandomWaveUFOsInCorners();
        } else if (waveNumber == RANDOM_WAVE_ROCKET_MADNESS) {
            return runRandomWaveRocketMadness();
        } else if (waveNumber == RANDOM_WAVE_BIRD_MADNESS) {
            return runRandomWaveBirdMadness();
        } else if (waveNumber == RANDOM_WAVE_UFO_MAZE) {
            return runRandomWaveUfoMaze(useStandardDuration);
        } else {
            return false;
        }
    }

    private boolean runRandomWaveBirdMadness() {

        if (!ExclamationMark.notificationSpawned) {
            //Spawn a warning notification if it is not yet spawned ( for bird coming from left side)
            ExclamationMark.spawnExclamationMark(Notifications.DIRECTION_LEFT);
        }

        if (ExclamationMark.notificationIsComplete()) {
            //If notification has finished displaying,
            // Spawn loads of L1birds and L2birds flying in regular and reverse directions
            spawnBirds(RANDOM_WAVE_BIRD_MADNESS_L1BIRD_DURATION, RANDOM_WAVE_BIRD_MADNESS_L2BIRD_DURATION);

            spawnReverseBirds(RANDOM_WAVE_BIRD_MADNESS_L1BIRD_DURATION, RANDOM_WAVE_BIRD_MADNESS_L2BIRD_DURATION);
        }

        return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);

    }

    private boolean runRandomWaveTeleportMadness() {

        // Spawn loads of teleports and level one birds
        spawnBirds(RANDOM_WAVE_TELEPORT_MADNESS_L1BIRD_DURATION, 100000);

        if (currentTimeInMillis - teleports.getLastTeleportSpawnTime() > RANDOM_WAVE_TELEPORT_SPAWN_DURATION) {
            teleports.spawnTeleports();
        }

        return checkIfRandomWaveIsComplete(RANDOM_WAVE_TELEPORT_MADNESS_TOTAL_TIME);

    }

    private boolean runRandomWaveAlienMissileMadness() {

        if (!ExclamationMark.notificationSpawned) {
            //Spawn a warning notification if it is not yet spawned ( for alien missiles coming from all directions)
            // Don't need a warning for the right since dodgeables typically come from that direction
            ExclamationMark.spawnExclamationMark(Notifications.DIRECTION_LEFT);
            ExclamationMark.spawnExclamationMark(Notifications.DIRECTION_BOTTOM);
            ExclamationMark.spawnExclamationMark(Notifications.DIRECTION_TOP);
        }

        if (ExclamationMark.notificationIsComplete()) {
            //If notification has finished displaying,
            // Spawn loads of alien missiles
            if (currentTimeInMillis - alienMissiles.getLastAlienMissileSpawnTime(4) > RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION) {
                alienMissiles.spawnAlienMissile(alienMissiles.SPAWN_DIRECTION_LEFTWARD, 4);
                alienMissiles.spawnAlienMissile(alienMissiles.SPAWN_DIRECTION_UPWARD, 4);
                alienMissiles.spawnAlienMissile(alienMissiles.SPAWN_DIRECTION_RIGHTWARD, 4);
                alienMissiles.spawnAlienMissile(alienMissiles.SPAWN_DIRECTION_DOWNWARD, 4);
            }
        }


        return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);


    }

    private boolean runRandomWaveUFOsInCorners() {
        // Spawn two ufos in the top-right and bottom-left corners of the screen
        // The ufos shoot energy beams in all directions, causing the entire border of screen to be blocked off by beams

        if (!ExclamationMark.notificationSpawned) {
            //Spawn a warning notification if it is not yet spawned ( for UFOs coming from all directions)
            // Don't need a warning for the right since dodgeables typically come from that direction
            ExclamationMark.spawnExclamationMark(Notifications.DIRECTION_LEFT);
            ExclamationMark.spawnExclamationMark(Notifications.DIRECTION_BOTTOM);
            ExclamationMark.spawnExclamationMark(Notifications.DIRECTION_TOP);
        }

        if (ExclamationMark.notificationIsComplete()) {
            //If notification has finished displaying,
            //Spawn corner UFOs and hold them in the corner for 30 seconds
            if (!randomWaveCornerUfosAreSpawned) {
                ufos.spawnTopRightCornerUfo(ufos.ENERGY_BEAM_ALL_DIRECTIONS, 20, 4);
                ufos.spawnBottomLeftCornerUfo(ufos.ENERGY_BEAM_ALL_DIRECTIONS, 20, 4);
                randomWaveCornerUfosAreSpawned = true;
            }
        }

        spawnBirds(RANDOM_WAVE_UFOS_IN_CORNERS_BIRD_DURATION, RANDOM_WAVE_UFOS_IN_CORNERS_BIRD_DURATION);


        return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);


    }

    private boolean runRandomWaveRocketMadness() {

        // Spawn level one birds and also lots of rockets to explode the birds

        spawnBirds(RANDOM_WAVE_ROCKET_MADNESS_L1BIRD_DURATION, 100000);

        if (currentTimeInMillis - rockets.getLastRocketSpawnTime(4) > RANDOM_WAVE_ROCKET_MADNESS_SPAWN_DURATION) {
            rockets.spawnRocket(4);
        }

        return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);


    }

    private boolean runRandomWaveUfoMaze(boolean useStandardDuration) {

        // Spawn ufos (some of them shoot beams upwards and some downwards
        // The player must navigate through the maze of beams
        // Every other beam should shoot downwards, then upwards, and so forth...

        //This wave uses the long duration by default unless it is run on the final level, then it uses the standard duration

        spawnBirds(RANDOM_WAVE_UFO_MAZE_L1BIRD_DURATION, RANDOM_WAVE_UFO_MAZE_L2BIRD_DURATION);

        if (currentTimeInMillis - ufos.getLastUfoSpawnTime(4) > RANDOM_WAVE_UFO_MAZE_SPAWN_DURATION) {
            if (randomWaveUfoMazeBeamDirection == ufos.ENERGY_BEAM_DOWN) {
                ufos.spawnStopInRightCenterUfo(randomWaveUfoMazeBeamDirection,  6, 4);
                randomWaveUfoMazeBeamDirection = ufos.ENERGY_BEAM_UP;
            } else {
                ufos.spawnStopInRightCenterUfo(randomWaveUfoMazeBeamDirection,  6, 4);
                randomWaveUfoMazeBeamDirection = ufos.ENERGY_BEAM_DOWN;
            }
        }


        if (useStandardDuration){
            return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);
        } else {
            return checkIfRandomWaveIsComplete(RANDOM_WAVE_LONG_DURATION);
        }

    }


    private void resetWaveVariables() {
        randomWaveCornerUfosAreSpawned = false;
        ExclamationMark.notificationSpawned = false;
    }


}
