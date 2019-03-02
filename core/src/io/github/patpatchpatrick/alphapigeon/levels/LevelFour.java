package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Notifications;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Notifications.ExclamationMark;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Teleports;

public class LevelFour extends Level {

    // The fourth level of the game is considered the "HARD" level.
    // This level is incredibly difficult
    // This level consists of random waves/puzzles of dodgeable creatures
    // that are randomly selected

    //RANDOM WAVE VARIABLES
    //Durations are all in seconds
    public final static int TOTAL_NUMBER_OF_WAVES = 6;
    private final float RANDOM_WAVE_TELEPORT_MADNESS = 1f;
    private final float RANDOM_WAVE_TELEPORT_MADNESS_L1BIRD_DURATION = 0.7f;
    private final float RANDOM_WAVE_TELEPORT_SPAWN_DURATION = 4f;
    private final float RANDOM_WAVE_TELEPORT_MADNESS_TOTAL_TIME = 15f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_MADNESS = 2f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION = 5f;
    private final float RANDOM_WAVE_UFOS_IN_CORNERS = 3f;
    private final float RANDOM_WAVE_UFOS_IN_CORNERS_BIRD_DURATION = 2f;
    private boolean randomWaveCornerUfosAreSpawned = false;
    private final float RANDOM_WAVE_ROCKET_MADNESS = 4f;
    private final float RANDOM_WAVE_ROCKET_MADNESS_L1BIRD_DURATION = 0.8f;
    private final float RANDOM_WAVE_ROCKET_MADNESS_SPAWN_DURATION = 0.8f;
    private final float RANDOM_WAVE_BIRD_MADNESS = 5f;
    private final float RANDOM_WAVE_BIRD_MADNESS_L1BIRD_DURATION = 1.6f;
    private final float RANDOM_WAVE_BIRD_MADNESS_L2BIRD_DURATION = 3.2f;
    private final float RANDOM_WAVE_UFO_MAZE = 6f;
    private final float RANDOM_WAVE_UFO_MAZE_SPAWN_DURATION = 7.5f;
    private final float RANDOM_WAVE_UFO_MAZE_L1BIRD_DURATION = 2f;
    private final float RANDOM_WAVE_UFO_MAZE_L2BIRD_DURATION = 2f;
    private float randomWaveUfoMazeBeamDirection = ufos.ENERGY_BEAM_DOWN;


    public LevelFour(Dodgeables dodgeables) {
        super(dodgeables);
    }

    public boolean run(boolean runRandomWave, float waveNumber, float totalGameTime, boolean useStandardDuration) {

        this.totalGameTime = totalGameTime;

        //Manually run a wave
        // Return true if wave is complete
        if (!waveIsInitiated) {
            resetWaveVariables();
            if (runRandomWave){
                // If you are running a random wave, override the manually inputted wave number with a random number
                waveToRun = MathUtils.random(1, TOTAL_NUMBER_OF_WAVES);
            } else {
                // If manually running a wave, waveToRun is the manually inputted wave numebr
                waveToRun = waveNumber;
            }
            //Save the time the last random wave was started
            lastWaveStartTime = totalGameTime;
            waveIsInitiated = true;
            return false;
        } else if (waveToRun == RANDOM_WAVE_TELEPORT_MADNESS) {
            return runRandomWaveTeleportMadness();
        } else if (waveToRun == RANDOM_WAVE_ALIEN_MISSILE_MADNESS) {
            return runRandomWaveAlienMissileMadness();
        } else if (waveToRun == RANDOM_WAVE_UFOS_IN_CORNERS) {
            return runRandomWaveUFOsInCorners();
        } else if (waveToRun == RANDOM_WAVE_ROCKET_MADNESS) {
            return runRandomWaveRocketMadness();
        } else if (waveToRun == RANDOM_WAVE_BIRD_MADNESS) {
            return runRandomWaveBirdMadness();
        } else if (waveToRun == RANDOM_WAVE_UFO_MAZE) {
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

        // Spawn loads of teleports at random heights and level one birds
        spawnBirds(RANDOM_WAVE_TELEPORT_MADNESS_L1BIRD_DURATION, 100000);

        if (totalGameTime - teleports.getLastTeleportSpawnTime() > RANDOM_WAVE_TELEPORT_SPAWN_DURATION) {
            teleports.spawnTeleports(Teleports.VERT_POSITION_RANDOM);
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
            if (totalGameTime - alienMissiles.getLastAlienMissileSpawnTime(4) > RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION) {
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

        if (totalGameTime - rockets.getLastRocketSpawnTime(4) > RANDOM_WAVE_ROCKET_MADNESS_SPAWN_DURATION) {
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

        if (totalGameTime - ufos.getLastUfoSpawnTime(4) > RANDOM_WAVE_UFO_MAZE_SPAWN_DURATION) {
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
