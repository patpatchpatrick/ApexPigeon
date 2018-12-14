package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.Pigeon;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class Dodgeables {

    //Class to define objects that the player should dodge

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;

    //Dodgeable variables
    private Birds birds;
    private Rockets rockets;
    private AlienMissiles alienMissiles;
    private Teleports teleports;
    private PowerUps powerUps;
    private Meteors meteors;
    private UFOs ufos;

    //GAME TIMES
    private float startTime = 0f;
    //WAVE TIMES in milliseconds
    private final float WAVE_0 = 0f;
    private final float WAVE_1 = 1000;
    private final float WAVE_2 = 2000;
    private final float WAVE_3 = 800000;
    //Time durations in between spawns for dodgeables (milliseconds)
    private final float L1BIRD_SPAWN_DURATION_WAVE_0 = 2000;
    private final float L1BIRD_SPAWN_DURATION_WAVE_1 = 2000;
    private final float L1BIRD_SPAWN_DURATION_WAVE_2 = 2000;
    private final float L2BIRD_SPAWN_DURATION_WAVE_1 = 4000;
    private final float L2BIRD_SPAWN_DURATION_WAVE_2 = 4000;
    private final float ROCKET_SPAWN_DURATION_WAVE_2 = 4000;

    public Dodgeables(Pigeon pigeon, World world, AlphaPigeon game, OrthographicCamera camera) {

        gameWorld = world;
        this.game = game;
        this.camera = camera;

        birds = new Birds(gameWorld, game, camera);
        rockets = new Rockets(gameWorld, game, camera);
        alienMissiles = new AlienMissiles(gameWorld, game, camera);
        teleports = new Teleports(gameWorld, game, camera);
        powerUps = new PowerUps(gameWorld, game, camera);
        meteors = new Meteors(gameWorld, game, camera);
        ufos = new UFOs(gameWorld, game, camera);

        startTime = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;

    }


    public void spawnDodgeables() {

        //class to determine if we need to spawn new dodgeables depending on how much time has passed
        //Get current time in milliseconds
        long currentTimeInMillis = TimeUtils.nanoTime() / GameVariables.MILLION_SCALE;
        float totalGameTime = currentTimeInMillis - startTime;
        float powerUpShieldInterval = powerUps.getPowerUpShieldIntervalTime();


        if (totalGameTime > WAVE_0 && totalGameTime < WAVE_1) {

            if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > L1BIRD_SPAWN_DURATION_WAVE_0){
                birds.spawnLevelOneBird();
            }
            if (currentTimeInMillis - powerUps.getLastpowerUpShieldSpawnTime() > powerUpShieldInterval) {
                Gdx.app.log("MyTag", "" + currentTimeInMillis);
                Gdx.app.log("MyTag", "" + powerUps.getLastpowerUpShieldSpawnTime());
                Gdx.app.log("MyTag", "" + powerUpShieldInterval);
                powerUps.spawnPowerUpShield();
            }


        } else if (totalGameTime >= WAVE_1 && totalGameTime < WAVE_2) {

            if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > L1BIRD_SPAWN_DURATION_WAVE_1){
                birds.spawnLevelOneBird();
            }
            if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > L2BIRD_SPAWN_DURATION_WAVE_1){
                birds.spawnLevelTwoBird();
            }
            if (currentTimeInMillis - powerUps.getLastpowerUpShieldSpawnTime() > powerUpShieldInterval){
                powerUps.spawnPowerUpShield();
            }


        } else if (totalGameTime >= WAVE_2 && totalGameTime < WAVE_3) {

            if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > L1BIRD_SPAWN_DURATION_WAVE_1){
                birds.spawnLevelOneBird();
            }
            if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > L2BIRD_SPAWN_DURATION_WAVE_1){
                birds.spawnLevelTwoBird();
            }
            if (currentTimeInMillis - powerUps.getLastpowerUpShieldSpawnTime() > powerUpShieldInterval){
                powerUps.spawnPowerUpShield();
            }
            if (currentTimeInMillis - rockets.getLastRocketSpawnTime() > ROCKET_SPAWN_DURATION_WAVE_2){
                rockets.spawnRocket();
            }




        } else {

            /**

            if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() > 2000)
                birds.spawnLevelOneBird();
            if (currentTimeInMillis - meteors.getLastMeteorSpawnTime() > 1000000)
                meteors.spawnMeteor();
            if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() > 1000000)
                birds.spawnLevelTwoBird();
            if (currentTimeInMillis - rockets.getLastRocketSpawnTime() > 1000000)
                rockets.spawnRocket();
            if (currentTimeInMillis - alienMissiles.getLastAlienMissileSpawnTime() > 1000000)
                alienMissiles.spawnAlienMissile();
            if (currentTimeInMillis - powerUps.getLastpowerUpShieldSpawnTime() > 5000000)
                powerUps.spawnPowerUpShield();
            if (currentTimeInMillis - teleports.getLastTeleportSpawnTime() > 5000000)
                teleports.spawnTeleports();
            if (currentTimeInMillis - ufos.getLastUfoSpawnTime() > 1500000)
                ufos.spawnUfo();

             */

        }


    }

    public void spawnRocketExplosion(float explosionPositionX, float explosionPositionY) {
        rockets.spawnRocketExplosion(explosionPositionX, explosionPositionY);
    }

    public void render(float stateTime, SpriteBatch batch) {

        //Render all dodgeable classes
        birds.render(stateTime, batch);
        rockets.render(stateTime, batch);
        alienMissiles.render(stateTime, batch);
        teleports.render(stateTime, batch);
        powerUps.render(stateTime, batch);
        meteors.render(stateTime, batch);
        ufos.render(stateTime, batch);

    }

    public void update(float stateTime) {
        //Check if we need to spawn new dodgeables depending on game time
        spawnDodgeables();

        //Update all dodgeable classes
        birds.update();
        rockets.update();
        alienMissiles.update();
        teleports.update();
        powerUps.update();
        meteors.update();
        ufos.update(stateTime);

    }

    public void sweepDeadBodies(){
        this.birds.sweepDeadBodies();
        this.rockets.sweepDeadBodies();
        this.powerUps.sweepDeadBodies();

    }

    public void dispose() {

        //Dispose all dodgeable classes' textures
        birds.dispose();
        rockets.dispose();
        alienMissiles.dispose();
        teleports.dispose();
        powerUps.dispose();
        meteors.dispose();
        ufos.dispose();

    }

}
