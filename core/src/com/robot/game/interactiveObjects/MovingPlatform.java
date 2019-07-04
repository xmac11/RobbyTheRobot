package com.robot.game.interactiveObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

public class MovingPlatform {

    private World world;
    private Body body;
    private boolean destroyed;

    public MovingPlatform(World world, Body body, FixtureDef fixtureDef) {
        this.world = world;
        this.body = body;
        body.createFixture(fixtureDef).setUserData(this);
    }

    public void movePlatform(float vX, float vY) {
        body.setLinearVelocity(vX, vY);
    }

    public void update() {
        if(body.getPosition().y < 0 && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
        }
    }

    public Body getBody() {
        return body;
    }

}
