package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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


    public void spawnRocketExplosion(float explosionPositionX, float explosionPositionY) {
        rockets.spawnRocketExplosion(explosionPositionX, explosionPositionY);
    }

    public Birds getBirds(){
        return this.birds;
    }

    public Rockets getRockets(){
        return this.rockets;
    }

    public AlienMissiles getAlienMissiles(){
        return this.alienMissiles;
    }

    public Teleports getTeleports(){
        return this.teleports;
    }

    public PowerUps getPowerUps(){
        return this.powerUps;
    }

    public  Meteors getMeteors(){
        return this.meteors;
    }

    public UFOs getUfos(){
        return this.ufos;
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

        //Update all dodgeable classes
        birds.update();
        rockets.update();
        alienMissiles.update();
        teleports.update();
        powerUps.update();
        meteors.update();
        ufos.update(stateTime);

    }

    public void sweepDeadBodies() {
        this.birds.sweepDeadBodies();
        this.rockets.sweepDeadBodies();
        this.powerUps.sweepDeadBodies();
        this.meteors.sweepDeadBodies();
        this.alienMissiles.sweepDeadBodies();
        this.ufos.sweepDeadBodies();
        this.teleports.sweepDeadBodies();

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
