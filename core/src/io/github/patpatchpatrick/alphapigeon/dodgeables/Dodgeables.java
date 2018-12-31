package io.github.patpatchpatrick.alphapigeon.dodgeables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
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

    //Scrolling Force
    //The force with which the current dodgeables are scrolling
    //When new dodgeables are spawned, they are automatically spawned with this force by default
    public Vector2 scrollingVelocity = new Vector2(0, 0);

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

        //Update all dodgeables so that they scroll when they leave the bounds of the screen
        for (Dodgeable dodgeable : activeDodgeables){
            float upperBound = camera.viewportHeight + GameVariables.BUFFER_HEIGHT;
            float lowerBound = 0 - dodgeable.HEIGHT;
            Boolean dodgeableOverUpperBound = dodgeable.getPosition().y > upperBound;
            Boolean dodgeableUnderLowerBound = dodgeable.getPosition().y < lowerBound;

          //Scroll dodgeables if they are out of bounds
            if (dodgeableUnderLowerBound){
                dodgeable.dodgeableBody.setTransform(dodgeable.getPosition().x, upperBound, dodgeable.getAngle());
            } else if (dodgeableOverUpperBound){
                dodgeable.dodgeableBody.setTransform(dodgeable.getPosition().x, lowerBound, dodgeable.getAngle());
            }

        }

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
