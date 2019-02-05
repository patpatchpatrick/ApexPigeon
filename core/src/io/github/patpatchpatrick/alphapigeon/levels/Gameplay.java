package io.github.patpatchpatrick.alphapigeon.levels;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.dodgeables.AlienMissiles;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Birds;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Meteors;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Dodgeable;
import io.github.patpatchpatrick.alphapigeon.dodgeables.PowerUps;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Rockets;
import io.github.patpatchpatrick.alphapigeon.dodgeables.Teleports;
import io.github.patpatchpatrick.alphapigeon.dodgeables.UFOs;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class Gameplay extends Level {

    //Levels
    private LevelOne levelOne;
    private LevelTwo levelTwo;
    private LevelThree levelThree;
    private LevelFour levelFour;
    private LevelFinal levelFinal;

    //GAME TIMES
    private boolean gamePlayInitiated = false;
    private float startTime = 0f;
    public static float totalGameTime; //USED TO KEEP TRACK OF TOTAL GAME TIME.. PRIMARY TIME VARIABLE USED IN LEVELS AND DODGEABLES
    private float powerUpShieldInterval;

    //Class to control the gameplay of the game
    public Gameplay(Dodgeables dodgeables) {
        super(dodgeables);

        levelOne = new LevelOne(dodgeables);
        levelTwo = new LevelTwo(dodgeables);
        levelThree = new LevelThree(dodgeables);
        levelFour = new LevelFour(dodgeables);
        levelFinal = new LevelFinal(dodgeables, levelOne, levelTwo, levelThree, levelFour);
    }


    public void render(float stateTime, SpriteBatch batch) {

        // Render all the dodgeable objects
        // Use the total game time so that the dodgeables stay in sync with the levels (both use totalGameTime)
        this.dodgeables.render(totalGameTime, batch);

    }

    public void update(float stateTime) {

        //Initialize start time
        if (!gamePlayInitiated){
            startTime = stateTime;
            gamePlayInitiated = true;
        }

        // Update all levels and gameplay
        updateLevels(stateTime);

        // Update all the dodgeable objects
        this.dodgeables.update(stateTime);

    }

    private void updateLevels(float stateTime) {

        // Method to keep track of how much time has passed and which level to run

        totalGameTime = stateTime - startTime;
        powerUpShieldInterval = powerUps.getPowerUpShieldIntervalTime();

        Gdx.app.log("STATETIME",  "" + totalGameTime);

        if (totalGameTime > LEVEL_ONE_START_TIME && totalGameTime <= LEVEL_ONE_END_TIME) {

            levelOne.run(totalGameTime, powerUpShieldInterval);

        } else if (totalGameTime > LEVEL_TWO_START_TIME && totalGameTime <= LEVEL_TWO_END_TIME){

            levelTwo.run(true, NO_WAVE, totalGameTime, powerUpShieldInterval, false);

        } else if (totalGameTime > LEVEL_THREE_START_TIME & totalGameTime <= LEVEL_THREE_END_TIME){

            levelThree.run(true, NO_WAVE, totalGameTime, powerUpShieldInterval, false);

        } else if (totalGameTime > LEVEL_FOUR_START_TIME & totalGameTime <= LEVEL_FOUR_END_TIME){

            levelFour.run(true, NO_WAVE, totalGameTime, powerUpShieldInterval, false);

        } else if (totalGameTime > LEVEL_FINAL_START_TIME){

            levelFinal.run(totalGameTime, powerUpShieldInterval);
        }


    }
}
