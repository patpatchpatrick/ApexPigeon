package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.math.MathUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Notifications;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Notifications.ExclamationMark;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class LevelThree extends Level {

    // The third level of the game is considered the "MEDIUM" level.
    // This is the level where things begin to get very difficult
    // This level consists of random waves/puzzles of dodgeable creatures
    // that are randomly selected

    //RANDOM WAVE VARIABLES
    // Level Three consists of "MEDIUM" difficulty waves of dodgeables that occur randomly
    // Durations are all in seconds
    public final static int TOTAL_NUMBER_OF_WAVES = 6;
    private final float RANDOM_WAVE_UFO_HORIZONTAL = 1f;
    private boolean randomWaveHorizUfoSpawned = false;
    private final float RANDOM_WAVE_UFO_VERTICAL = 2f;
    private boolean randomWaveVertUfoSpawned = false;
    private final float RANDOM_WAVE_METEORS = 3f;
    private final float RANDOM_WAVE_METEORS_SPAWN_DURATION = 2f;
    private final float RANDOM_WAVE_MISSILES = 4f;
    private final float RANDOM_WAVE_MISSILES_SPAWN_DURATION = 2f;
    private final float RANDOM_WAVE_UFO_CENTER = 5f;
    private boolean randomWaveCenterUfoSpawned = false;
    private final float RANDOM_WAVE_VERT_UFO_TELEPORT = 6f;
    private boolean randomWaveVertTeleUfoSpawned = false;
    private boolean randomWaveVertTeleUfoTeleportsSpawned = false;
    private float randomWaveVerTeleUfoTimeUfoSpawned = 999999999f;
    private final float RANDOM_WAVE_VERT_UFO_TELEPORT_TIME_BEFORE_TELE_SPAWN = 12f;
    private final float RANDOM_WAVE_L1BIRD_SPAWN_DURATION = 2f;
    private final float RANDOM_WAVE_L2BIRD_SPAWN_DURATION = 2f;


    public LevelThree(Dodgeables dodgeables) {
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
                // If manually running a wave, the wave to run should be the manually inputted wave number
                waveToRun = waveNumber;
            }
            //Save the time the last random wave was started
            lastWaveStartTime = totalGameTime;
            waveIsInitiated = true;
            return false;
        } else if (waveToRun == RANDOM_WAVE_UFO_HORIZONTAL) {
            return runRandomWaveHorizontalUFO();
        } else if (waveToRun == RANDOM_WAVE_UFO_VERTICAL) {
            return runRandomWaveVerticalUFO();
        } else if (waveToRun == RANDOM_WAVE_METEORS) {
            return runRandomWaveMeteors();
        } else if (waveToRun == RANDOM_WAVE_MISSILES) {
            return runRandomWaveMissiles();
        } else if (waveToRun == RANDOM_WAVE_UFO_CENTER) {
            return runRandomWaveCenterUFO();
        } else if (waveToRun == RANDOM_WAVE_VERT_UFO_TELEPORT) {
            return runRandomWaveVerticalUFOAndTeleport();
        } else {
            return false;
        }
    }


    private void resetWaveVariables() {
        //Reset all variables that need to be reset before selecting a new wave
        randomWaveHorizUfoSpawned = false;
        randomWaveVertUfoSpawned = false;
        randomWaveCenterUfoSpawned = false;
        randomWaveVertTeleUfoSpawned = false;
        randomWaveVertTeleUfoTeleportsSpawned = false;
        ExclamationMark.notificationSpawned = false;
        randomWaveVerTeleUfoTimeUfoSpawned = 99999999;

    }

    private boolean runRandomWaveCenterUFO() {

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (!randomWaveCenterUfoSpawned) {

            //Spawn a UFO that stops in center and shoots beams in all directions for a specified
            //amount of time
            ufos.spawnStopInCenterUfo(ufos.ENERGY_BEAM_ALL_DIRECTIONS, 5, 3);
            //Spawn a teleport that can be used to dodge the center UFO
            teleports.spawnTeleports(10);
            randomWaveCenterUfoSpawned = true;
        }


        return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);

    }

    private boolean runRandomWaveVerticalUFO() {

        if (!ExclamationMark.notificationSpawned) {
            //Spawn a warning notification if it is not yet spawned ( for UFO coming from the top)
            ExclamationMark.spawnExclamationMark(Notifications.DIRECTION_TOP);
        }

        if (ExclamationMark.notificationIsComplete()) {
            //If notification has finished displaying,
            // Spawn a UFO coming from the top of the screen
            if (!randomWaveVertUfoSpawned) {

                //Spawn a UFO that travels vertically from top to bottom and shoots a straight vertical
                //line of energy beams (i.e. a top and bottom beam)
                ufos.spawnVerticalUfo(ufos.ENERGY_BEAM_VERTICAL_DIRECTIONS, 3);
                randomWaveVertUfoSpawned = true;
            }
        }

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);


        return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);


    }

    private boolean runRandomWaveMissiles() {
        //Spawn both regular rockets and alien missiles

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (totalGameTime - rockets.getLastRocketSpawnTime(3) > RANDOM_WAVE_MISSILES_SPAWN_DURATION) {
            rockets.spawnRocket(3);
        }
        if (totalGameTime - alienMissiles.getLastAlienMissileSpawnTime(3) > RANDOM_WAVE_MISSILES_SPAWN_DURATION) {
            alienMissiles.spawnAlienMissile(alienMissiles.SPAWN_DIRECTION_LEFTWARD, 3);
        }

        return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);

    }

    private boolean runRandomWaveMeteors() {

        if (!ExclamationMark.notificationSpawned) {
            //Spawn a warning notification if it is not yet spawned ( for meteors coming from the top)
            ExclamationMark.spawnExclamationMark(Notifications.DIRECTION_TOP);
        }

        if (ExclamationMark.notificationIsComplete()) {
            //If notification has finished displaying,
            // Spawn meteors coming from the top of the screen
            if (totalGameTime - meteors.getLastMeteorSpawnTime() > RANDOM_WAVE_METEORS_SPAWN_DURATION) {
                //Spawn meteors
                meteors.spawnMeteor();
            }
        }

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);

    }

    private boolean runRandomWaveHorizontalUFO() {

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (!randomWaveHorizUfoSpawned) {

            //Spawn a UFO that travels horizontally from right to left and shoots a straight horizontal
            //line of energy beams (i.e. a left and right beam)
            ufos.spawnHorizontalUfo(ufos.ENERGY_BEAM_HORIZONAL_DIRECTIONS, 3);
            randomWaveHorizUfoSpawned = true;
        }


        return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);

    }

    private boolean runRandomWaveVerticalUFOAndTeleport() {

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (!randomWaveVertTeleUfoSpawned) {

            //Spawn a UFO that travels horizontally from right to left and shoots a straight vertical
            //line of energy beams (i.e. a top and bottom beam) that must be jumped over using a teleport
            // The ufo stops at the right of the screen for 6 seconds to let the power beam generate before moving
            ufos.spawnStopInRightCenterUfo(ufos.ENERGY_BEAM_VERTICAL_DIRECTIONS, 6, 3);
            randomWaveVerTeleUfoTimeUfoSpawned = totalGameTime;
            randomWaveVertTeleUfoSpawned = true;

        }

        if (!randomWaveVertTeleUfoTeleportsSpawned && totalGameTime - randomWaveVerTeleUfoTimeUfoSpawned > RANDOM_WAVE_VERT_UFO_TELEPORT_TIME_BEFORE_TELE_SPAWN) {
            //If time before when the teleport should spawn has passed and teleports haven't spawned yet
            // , spawn teleports that can be used by the player to dodge the energy beam
            teleports.spawnTeleports(30);
            teleports.spawnTeleports(10);
            randomWaveVertTeleUfoTeleportsSpawned = true;

        }


        return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);


    }
}
