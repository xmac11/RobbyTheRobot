package com.robot.game.interactiveObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

public abstract class InteractivePlatform {

    protected World world;
    protected Body body;

    protected InteractivePlatform(World world, Body body) {
        this.world = world;
        this.body = body;
        //body.setActive(false);
    }

    public abstract void update(float delta);
    public abstract boolean isDestroyed();

    public Body getBody() {
        return body;
    }
}
