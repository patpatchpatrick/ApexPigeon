package io.github.patpatchpatrick.alphapigeon.dodgeables;

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
    private final float WAVE_1 = 60000;
    private final float WAVE_2 = WAVE_1 * 2;
    private final float BIRD_SPAWN_DURATION_WAVE_0 = 2000;

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


        if (totalGameTime > WAVE_0 && totalGameTime < WAVE_1) {

            if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() / GameVariables.MILLION_SCALE > BIRD_SPAWN_DURATION_WAVE_0)
                birds.spawnLevelOneBird();

        } else if (totalGameTime > WAVE_1){

        } else {

            if (currentTimeInMillis - birds.getLastLevelOneBirdSpawnTime() / GameVariables.MILLION_SCALE > 2000)
                birds.spawnLevelOneBird();
            if (currentTimeInMillis - meteors.getLastMeteorSpawnTime() / GameVariables.MILLION_SCALE > 1000000)
                meteors.spawnMeteor();
            if (currentTimeInMillis - birds.getLastLevelTwoBirdSpawnTime() / GameVariables.MILLION_SCALE > 1000000)
                birds.spawnLevelTwoBird();
            if (currentTimeInMillis - rockets.getLastRocketSpawnTime() / GameVariables.MILLION_SCALE > 1000000)
                rockets.spawnRocket();
            if (currentTimeInMillis - alienMissiles.getLastAlienMissileSpawnTime() / GameVariables.MILLION_SCALE > 1000000)
                alienMissiles.spawnAlienMissile();
            if (currentTimeInMillis - powerUps.getLastpowerUpShieldSpawnTime() / GameVariables.MILLION_SCALE > 5000000)
                powerUps.spawnPowerUpShield();
            if (currentTimeInMillis - teleports.getLastTeleportSpawnTime() / GameVariables.MILLION_SCALE > 5000000)
                teleports.spawnTeleports();
            if (currentTimeInMillis - ufos.getLastUfoSpawnTime() / GameVariables.MILLION_SCALE > 1500000)
                ufos.spawnUfo();

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
