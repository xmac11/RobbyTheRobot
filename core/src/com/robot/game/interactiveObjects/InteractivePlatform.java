package com.robot.game.interactiveObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class InteractivePlatform {

    protected World world;
    protected Body body;
    protected float vX;
    protected float vY;


    protected InteractivePlatform(World world, Body body, float vX, float vY) {
        this.world = world;
        this.body = body;
        this.vX = vX;
        this.vY = vY;
        //body.setActive(false);
    }

    public abstract void update(float delta);
    public abstract boolean isDestroyed();

    public void movePlatform() {
        body.setLinearVelocity(vX, vY);
    }

    public Body getBody() {
        return body;
    }

    public float getvX() {
        return vX;
    }

    public float getvY() {
        return vY;
    }
}
