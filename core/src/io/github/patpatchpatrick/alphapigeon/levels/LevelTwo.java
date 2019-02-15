package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.math.MathUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Notifications;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Notifications.ExclamationMark;
import io.github.patpatchpatrick.alphapigeon.dodgeables.PowerUps;

public class LevelTwo extends Level {

    // The second level of the game is considered the "EASY" level.
    // Although not actually easy, this level consists of random waves/puzzles of dodgeable creatures
    // that are randomly selected
    // Level 2 uses shorter durations for waves than other levels (you'll notice in the random wave methods)

    //RANDOM WAVE VARIABLES
    // Level Two consists of "EASY" waves of dodgeables that occur randomly
    // Durations are all in seconds
    public final static int TOTAL_NUMBER_OF_WAVES = 4;
    private final float RANDOM_WAVE_UFO = 1f;
    private final float RANDOM_WAVE_UFO_SPAWN_DURATION = 10f;
    private final float RANDOM_WAVE_ALIEN_MISSILE = 2f;
    private final float RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION = 2.5f;
    private final float RANDOM_WAVE_ROCKETS = 3f;
    private final float RANDOM_WAVE_ROCKETS_SPAWN_DURATION = 4f;
    private final float RANDOM_WAVE_L1BIRD_SPAWN_DURATION = 2f;
    private final float RANDOM_WAVE_L2BIRD_SPAWN_DURATION = 2f;
    private final float RANDOM_WAVE_VERT_BIRD_LINE = 4f;
    private boolean randomWaveVertBirdLineSpawned = false;

    public LevelTwo(Dodgeables dodgeables) {
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
                // If manually running a wave, use the manually inputted wave number as the wave to run
                waveToRun = waveNumber;
            }
            //Save the time the last random wave was started
            lastWaveStartTime = totalGameTime;
            waveIsInitiated = true;
            return false;
        } else if (waveToRun == RANDOM_WAVE_UFO) {
            return runRandomWaveUFO(useStandardDuration);
        } else if (waveToRun == RANDOM_WAVE_ALIEN_MISSILE) {
            return runRandomWaveAlienMissile(useStandardDuration);
        } else if (waveToRun == RANDOM_WAVE_ROCKETS) {
            return runRandomWaveRockets(useStandardDuration);
        } else if (waveToRun == RANDOM_WAVE_VERT_BIRD_LINE){
            return runRandomWaveVerticalBirdLine(useStandardDuration);
        } else {
            return false;
        }
    }

    private void resetWaveVariables(){

        //Reset all variables that need to be reset before starting a new wave
        randomWaveVertBirdLineSpawned = false;


    }


    private boolean runRandomWaveUFO(boolean useStandardDuration) {

        //Spawn a ufo that shoots an energy beam in a random direction
        //Also spawn birds

        //Return true if wave is complete
        //useStandardDuration boolean determines if wave should be run with a standard or short duration

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (totalGameTime - ufos.getLastUfoSpawnTime(2) > RANDOM_WAVE_UFO_SPAWN_DURATION) {
            ufos.spawnUfo(ufos.ENERGY_BEAM_RANDOM, 2);
        }


        return checkIfWaveIsComplete(useStandardDuration);
    }

    private boolean runRandomWaveAlienMissile(boolean useStandardDuration) {

        //Spawn alien missiles in regular intervals
        //Also spawn birds

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (totalGameTime - alienMissiles.getLastAlienMissileSpawnTime(2) > RANDOM_WAVE_ALIEN_MISSILE_SPAWN_DURATION) {
            alienMissiles.spawnAlienMissile(alienMissiles.SPAWN_DIRECTION_LEFTWARD, 2);
        }

        return checkIfWaveIsComplete(useStandardDuration);

    }

    private boolean runRandomWaveRockets(boolean useStandardDuration) {

        //Spawn rockets that launch at regular intervals
        //Also spawn birds

        spawnBirds(RANDOM_WAVE_L1BIRD_SPAWN_DURATION, RANDOM_WAVE_L2BIRD_SPAWN_DURATION);

        if (totalGameTime - rockets.getLastRocketSpawnTime(2) > RANDOM_WAVE_ROCKETS_SPAWN_DURATION) {
            rockets.spawnRocket(2);
        }

        return checkIfWaveIsComplete(useStandardDuration);

    }

    private boolean runRandomWaveVerticalBirdLine(boolean useStandardDuration){

        // Spawn a vertical line of birds that must be jumped over via teleports

        if (!ExclamationMark.notificationSpawned) {
            //Spawn a warning notification if it is not yet spawned ( for teleports coming from the left)
            ExclamationMark.spawnExclamationMark(Notifications.DIRECTION_LEFT);
        }

        if (ExclamationMark.notificationIsComplete()) {
            //If notification has finished displaying,
            // Spawn birds and teleports
            if (!randomWaveVertBirdLineSpawned){
                birds.spawnVerticalLineOfBirds(totalGameTime);
                teleports.spawnTeleports(10);
                teleports.spawnTeleports(30);
                randomWaveVertBirdLineSpawned = true;
            }
        }



        return checkIfWaveIsComplete(useStandardDuration);

    }

    private boolean checkIfWaveIsComplete(boolean useStandardDuration){

        //Determine if a wave is complete
        //If the wave is using standard duration, check if the standard wave time duration has passed
        //If the wave is using short duration, check if the short wave time duration has passed

        if (useStandardDuration){
            return checkIfRandomWaveIsComplete(RANDOM_WAVE_STANDARD_DURATION);
        } else {
            return checkIfRandomWaveIsComplete(RANDOM_WAVE_SHORT_DURATION);
        }
    }



}
