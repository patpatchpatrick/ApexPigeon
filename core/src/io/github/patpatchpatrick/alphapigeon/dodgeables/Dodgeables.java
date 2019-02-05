package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;
import io.github.patpatchpatrick.alphapigeon.Pigeon;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.Dodgeable;
import io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects.UFO;
import io.github.patpatchpatrick.alphapigeon.resources.GameVariables;

public class Dodgeables {

    //Class to define objects that the player should dodge

    World gameWorld;
    private AlphaPigeon game;
    private OrthographicCamera camera;
    public final Array<Dodgeable> activeDodgeables = new Array<Dodgeable>();

    //Dodgeable variables
    private Birds birds;
    private Rockets rockets;
    private AlienMissiles alienMissiles;
    private Teleports teleports;
    private PowerUps powerUps;
    private Meteors meteors;
    private UFOs ufos;
    public Notifications notifications;

    public Dodgeables(Pigeon pigeon, World world, AlphaPigeon game, OrthographicCamera camera) {

        gameWorld = world;
        this.game = game;
        this.camera = camera;

        birds = new Birds(gameWorld, game, camera, this);
        rockets = new Rockets(gameWorld, game, camera, this);
        alienMissiles = new AlienMissiles(gameWorld, game, camera, this);
        teleports = new Teleports(gameWorld, game, camera, this);
        powerUps = new PowerUps(gameWorld, game, camera, this);
        meteors = new Meteors(gameWorld, game, camera, this);
        ufos = new UFOs(gameWorld, game, camera, this);
        notifications = new Notifications(gameWorld, game, camera, this);

    }


    public void spawnRocketExplosion(float explosionPositionX, float explosionPositionY) {
        rockets.spawnRocketExplosion(explosionPositionX, explosionPositionY);
    }

    public Birds getBirds() {
        return this.birds;
    }

    public Rockets getRockets() {
        return this.rockets;
    }

    public AlienMissiles getAlienMissiles() {
        return this.alienMissiles;
    }

    public Teleports getTeleports() {
        return this.teleports;
    }

    public PowerUps getPowerUps() {
        return this.powerUps;
    }

    public Meteors getMeteors() {
        return this.meteors;
    }

    public UFOs getUfos() {
        return this.ufos;
    }


    public void render(float totalGameTime, SpriteBatch batch) {

        //Render all dodgeable classes

        birds.render(totalGameTime, batch);
        rockets.render(totalGameTime, batch);
        alienMissiles.render(totalGameTime, batch);
        teleports.render(totalGameTime, batch);
        powerUps.render(totalGameTime, batch);
        meteors.render(totalGameTime, batch);
        ufos.render(totalGameTime, batch);
        notifications.render(totalGameTime, batch);


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
        notifications.update();

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
        notifications.dispose();
        birds.dispose();
        rockets.dispose();
        alienMissiles.dispose();
        teleports.dispose();
        powerUps.dispose();
        meteors.dispose();
        ufos.dispose();

    }

}
