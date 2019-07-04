package com.robot.game.interactiveObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class FallingPlatform {

    private World world;
    private Body body;
    private boolean destroyed;

    private boolean flagToMove;
    private float delay;
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
                elapsed += delta;
        }
    }

    public Body getBody() {
        return body;
    }

    public void setFlagToMove(boolean flagToMove) {
        this.flagToMove = flagToMove;
    }

    public boolean isDestroyed() {
        return destroyed;
    }
}
