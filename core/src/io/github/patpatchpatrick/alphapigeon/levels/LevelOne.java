package io.github.patpatchpatrick.alphapigeon.levels;

import io.github.patpatchpatrick.alphapigeon.dodgeables.Dodgeables;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.UFO;
import io.github.patpatchpatrick.alphapigeon.dodgeables.PowerUps;
import io.github.patpatchpatrick.alphapigeon.dodgeables.UFOs;

public class LevelOne extends Level {

    //The first level of the game only consists of level one and level two birds that spawn
    //The level is very basic to get the user warmed up for the more difficult levels

    //Time durations in between spawns for dodgeables (seconds)
    private final float L1BIRD_SPAWN_DURATION_WAVE_0 = 2;
    private final float L1BIRD_SPAWN_DURATION_WAVE_1 = 2;
    private final float L2BIRD_SPAWN_DURATION_WAVE_1 = 4;


    public LevelOne(Dodgeables dodgeables) {
        super(dodgeables);
    }

    public void run(float totalGameTime){


        if (totalGameTime < LEVEL_ONE_WAVE_1) {

            if (totalGameTime - birds.getLastLevelOneBirdSpawnTime() > L1BIRD_SPAWN_DURATION_WAVE_0) {
                birds.spawnLevelOneBird(totalGameTime);
                meteors.spawnMeteor();

            }
            if (totalGameTime - PowerUps.lastpowerUpShieldSpawnTime > PowerUps.randomSpawnIntervalTime) {
                powerUps.spawnPowerUp(PowerUps.POWER_UP_TYPE_SHIELD);
                ufos.spawnVerticalUfo(ufos.ENERGY_BEAM_VERTICAL_DIRECTIONS, 3);
            }
        } else if (totalGameTime >= LEVEL_ONE_WAVE_1 && totalGameTime < LEVEL_ONE_END_TIME) {

            if (totalGameTime - birds.getLastLevelOneBirdSpawnTime() > L1BIRD_SPAWN_DURATION_WAVE_1) {
                birds.spawnLevelOneBird(totalGameTime);
            }
            if (totalGameTime - birds.getLastLevelTwoBirdSpawnTime() > L2BIRD_SPAWN_DURATION_WAVE_1) {
                birds.spawnLevelTwoBird(totalGameTime);
            }
            if (totalGameTime - PowerUps.lastpowerUpShieldSpawnTime > PowerUps.randomSpawnIntervalTime) {
                powerUps.spawnPowerUp(PowerUps.POWER_UP_TYPE_SHIELD);
            }

        }


    }

}
