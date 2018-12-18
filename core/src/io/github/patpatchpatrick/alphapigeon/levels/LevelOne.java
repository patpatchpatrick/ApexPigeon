package io.github.patpatchpatrick.alphapigeon.levels;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;

public class LevelOne extends Level {

    //Time durations in between spawns for dodgeables (milliseconds)
    private final float L1BIRD_SPAWN_DURATION_WAVE_0 = 2000;
    private final float L1BIRD_SPAWN_DURATION_WAVE_1 = 2000;
    private final float L2BIRD_SPAWN_DURATION_WAVE_1 = 4000;


    public LevelOne(Dodgeables dodgeables) {
        super(dodgeables);
    }

    public void run(float totalGameTime, long currentTimeInMillis, float powerUpShieldInterval){


        if (totalGameTime < LEVEL_ONE_WAVE_1) {

            if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > L1BIRD_SPAWN_DURATION_WAVE_0) {
                birds.spawnLevelOneBird(totalGameTime);
            }
            if (currentTimeInMillis - powerUps.getLastpowerUpShieldSpawnTime() > powerUpShieldInterval) {
                powerUps.spawnPowerUpShield();
            }


        } else if (totalGameTime >= LEVEL_ONE_WAVE_1 && totalGameTime < LEVEL_ONE_END_TIME) {

            if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > L1BIRD_SPAWN_DURATION_WAVE_1) {
                birds.spawnLevelOneBird(totalGameTime);
            }
            if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > L2BIRD_SPAWN_DURATION_WAVE_1) {
                birds.spawnLevelTwoBird(totalGameTime);
            }
            if (currentTimeInMillis - powerUps.getLastpowerUpShieldSpawnTime() > powerUpShieldInterval) {
                powerUps.spawnPowerUpShield();
            }

        }


    }

}