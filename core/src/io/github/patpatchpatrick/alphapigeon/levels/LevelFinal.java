package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.dodgeables.PowerUps;

public class LevelFinal extends Level {

    private float randomWaveOneLevel = 0f;
    private float randomWaveOne = 0f;
    private boolean waveOneComplete = false;
    private float randomWaveTwoLevel = 0f;
    private float randomWaveTwo = 0f;
    private boolean waveTwoComplete = false;
    private final int TOTAL_NUMBER_OF_LEVELS = 4;


    //List of levels
    private LevelOne levelOne;
    private LevelTwo levelTwo;
    private LevelThree levelThree;
    private LevelFour levelFour;

    //The final level of the game is the most difficult and it never ends
    // The level spawns 2 randomWaves from levels (2-4) that occur simultaneously

    public LevelFinal(Dodgeables dodgeables, LevelOne levelOne, LevelTwo levelTwo, LevelThree levelThree, LevelFour levelFour) {
        super(dodgeables);

        this.levelOne = levelOne;
        this.levelTwo = levelTwo;
        this.levelThree = levelThree;
        this.levelFour = levelFour;
    }

    public void run(float totalGameTime, float powerUpShieldInterval) {

        this.totalGameTime = totalGameTime;
        this.powerUpShieldInterval = powerUpShieldInterval;

        if (!waveIsInitiated) {
            resetWaveVariables();
            //Determine which levels to run random waves from
            //Do not run random waves from the same levels
            randomWaveOneLevel = MathUtils.random(2, TOTAL_NUMBER_OF_LEVELS);
            //Ensure the 2nd level does not equal the first level
            randomWaveTwoLevel = randomWaveOneLevel;
            while (randomWaveTwoLevel == randomWaveOneLevel) {
                randomWaveTwoLevel = MathUtils.random(2, TOTAL_NUMBER_OF_LEVELS);
            }
            //Determine which specific waves from the randomly selected levels to run
            randomWaveOne = determineRandomWavesToRun(randomWaveOneLevel);
            randomWaveTwo = determineRandomWavesToRun(randomWaveTwoLevel);
            //Save the time the last random wave was started
            lastWaveStartTime = totalGameTime;
            waveIsInitiated = true;
        } else {
            runWave();
        }

    }

    private float determineRandomWavesToRun(float level) {

        //Return a random wave to run from a given level

        if (level == 2) {
            return MathUtils.random(1, LevelTwo.TOTAL_NUMBER_OF_WAVES);
        } else if (level == 3) {
            return MathUtils.random(1, LevelThree.TOTAL_NUMBER_OF_WAVES);
        } else {
            return MathUtils.random(1, LevelFour.TOTAL_NUMBER_OF_WAVES);
        }

    }

    public void resetWaveVariables() {
        randomWaveOneLevel = 0f;
        randomWaveTwoLevel = 0f;
        randomWaveOne = 0f;
        randomWaveTwo = 0f;
        waveOneComplete = false;
        waveTwoComplete = false;
    }

    private void runWave() {

        //Run a random wave from 2 random levels
        // If both waves are complete, reset the randomWaveIsInitiated variable so a new wave will be initiated

        Gdx.app.log("Level1", randomWaveOneLevel + " " + randomWaveOne);
        Gdx.app.log("Level2", randomWaveTwoLevel + " " + randomWaveTwo);

        if (!waveOneComplete){
            waveOneComplete = runWaveFromLevel(randomWaveOneLevel, randomWaveOne);
        }

        if (!waveTwoComplete){
            waveTwoComplete = runWaveFromLevel(randomWaveTwoLevel, randomWaveTwo);
        }


        if (waveOneComplete && waveTwoComplete){
            waveIsInitiated = false;
        }

        if (totalGameTime - PowerUps.lastpowerUpShieldSpawnTime > powerUpShieldInterval) {
            powerUps.spawnPowerUp(PowerUps.POWER_UP_TYPE_SHIELD);
            powerUps.spawnPowerUp(PowerUps.POWER_UP_TYPE_SKULL);
        }


    }

    private boolean runWaveFromLevel(float level, float wave){

        //Run a wave from a specified level

        if (level == 2){
            //Level 2 waves need to be specified to use the standard duration because the duration for level 2 waves change to be longer on the final level
            return levelTwo.run(false, wave, this.totalGameTime, this.powerUpShieldInterval, true);
        } else if (level == 3){
            return levelThree.run(false, wave, this.totalGameTime, this.powerUpShieldInterval, true);
        } else {
            //Level 4 waves need to be specified to use the standard duration because the duration for some level 3 waves change on the final level
            return levelFour.run(false, wave, this.totalGameTime, this.powerUpShieldInterval, true);
        }

    }
}
