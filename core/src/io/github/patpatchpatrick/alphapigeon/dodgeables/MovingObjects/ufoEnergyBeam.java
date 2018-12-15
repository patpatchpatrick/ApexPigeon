package io.github.patpatchpatrick.alphapigeon.dodgeables.MovingObjects;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;

import io.github.patpatchpatrick.alphapigeon.AlphaPigeon;

public class ufoEnergyBeam extends Dodgeable {
    public ufoEnergyBeam(World gameWorld, AlphaPigeon game, OrthographicCamera camera) {
        super(gameWorld, game, camera);
    }
}
