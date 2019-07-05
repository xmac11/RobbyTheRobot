package com.robot.game.interactiveObjects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

public class FallingPlatform {

    private World world;
    private Body body;
    private boolean destroyed;

    private boolean flagToMove;
    private float delay;
    private float startTime; // time the player jumped on the falling platform
    private float elapsed;

    public FallingPlatform(World world, Body body, FixtureDef fixtureDef, float delay) {
        this.world = world;
        this.body = body;
        body.createFixture(fixtureDef).setUserData(this);

        this.delay = delay;
        this.elapsed = 0;


    }

    public void movePlatform(float vX, float vY) {
        body.setLinearVelocity(vX, vY);
    }

    public void update(float delta) {
        // if body is out of bounds, destroy it
        if(body.getPosition().y < 0 && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
        }

        if(flagToMove) {
            if(elapsed >= delay) {
                movePlatform(0, -8);
                flagToMove = false;
            }
            else
                elapsed = (TimeUtils.nanoTime() - startTime) * MathUtils.nanoToSec;
        }
    }

    public Body getBody() {
        return body;
    }

    public void setFlagToMove(boolean flagToMove) {
        // keep track of the time the robot jumped on the falling platform
        this.startTime = TimeUtils.nanoTime();
        this.flagToMove = flagToMove;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
