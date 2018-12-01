package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.Pigeon;
import io.github.patpatchpatrick.alphapigeon.resources.BodyData;
import io.github.patpatchpatrick.alphapigeon.resources.BodyEditorLoader;
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

    }


    public void spawnDodgeables() {
        //class to determine if we need to spawn new dodgeables depending on how much time has passed
        if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - birds.getLastLevelOneBirdSpawnTime() / GameVariables.MILLION_SCALE > 50000)
            birds.spawnLevelOneBird();
        if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - meteors.getLastMeteorSpawnTime() / GameVariables.MILLION_SCALE > 10000)
            meteors.spawnMeteor();
        if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - birds.getLastLevelTwoBirdSpawnTime() / GameVariables.MILLION_SCALE > 10000)
            birds.spawnLevelTwoBird();
        if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - rockets.getLastRocketSpawnTime() / GameVariables.MILLION_SCALE > 10000)
            rockets.spawnRocket();
        if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - alienMissiles.getLastAlienMissileSpawnTime() / GameVariables.MILLION_SCALE > 10000)
            alienMissiles.spawnAlienMissile();
        if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - powerUps.getLastpowerUpShieldSpawnTime() / GameVariables.MILLION_SCALE > 50000)
            powerUps.spawnPowerUpShield();
        if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - teleports.getLastTeleportSpawnTime() / GameVariables.MILLION_SCALE > 50000)
            teleports.spawnTeleports();
        if (TimeUtils.nanoTime() / GameVariables.MILLION_SCALE - ufos.getLastUfoSpawnTime() / GameVariables.MILLION_SCALE > 10000)
            ufos.spawnUfo();
    }

    public void spawnRocketExplosion(float explosionPositionX, float explosionPositionY){
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
        ufos.update();

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
