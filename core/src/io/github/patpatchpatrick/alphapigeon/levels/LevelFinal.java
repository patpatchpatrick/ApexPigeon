package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.math.MathUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;

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

    public void run(float totalGameTime, long currentTimeInMillis, float powerUpShieldInterval) {

        this.totalGameTime = totalGameTime;
        this.currentTimeInMillis = currentTimeInMillis;
        this.powerUpShieldInterval = powerUpShieldInterval;

        if (!randomWaveIsInitiated) {
            resetWaveVariables();
            //Determine which levels to run random waves from
            //Do not run random waves from the same levels
            randomWaveOneLevel = MathUtils.random(2, TOTAL_NUMBER_OF_LEVELS);
            while (randomWaveTwoLevel != randomWaveOneLevel) {
                randomWaveTwoLevel = MathUtils.random(2, TOTAL_NUMBER_OF_LEVELS);
            }
            //Determine which specific waves from the randomly selected levels to run
            randomWaveOne = determineRandomWavesToRun(randomWaveOneLevel);
            randomWaveTwo = determineRandomWavesToRun(randomWaveTwoLevel);
            //Save the time the last random wave was started
            lastRandomWaveStartTime = currentTimeInMillis;
            randomWaveIsInitiated = true;
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
        if (!waveOneComplete){
            waveOneComplete = runWaveFromLevel(randomWaveOneLevel, randomWaveOne);
        }

        if (!waveTwoComplete){
            waveTwoComplete = runWaveFromLevel(randomWaveTwoLevel, randomWaveTwo);
        }


        if (waveOneComplete && waveTwoComplete){
            randomWaveIsInitiated = false;
        }


    }

    private boolean runWaveFromLevel(float level, float wave){

        //Run a wave from a specified level

        if (level == 2){
            return levelTwo.runManualWave(wave, this.totalGameTime, this.currentTimeInMillis, this.powerUpShieldInterval);
        } else if (level == 3){
            return levelThree.runManualWave(wave, this.totalGameTime, this.currentTimeInMillis, this.powerUpShieldInterval);
        } else {
            return levelFour.runManualWave(wave, this.totalGameTime, this.currentTimeInMillis, this.powerUpShieldInterval);
        }

    }
}
