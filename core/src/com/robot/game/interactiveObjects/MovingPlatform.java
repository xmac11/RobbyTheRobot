package com.robot.game.interactiveObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import static com.robot.game.util.Constants.MAP_HEIGHT;

public class MovingPlatform {

    private Body body;

    public MovingPlatform(Body body, FixtureDef fixtureDef) {
        this.body = body;
        body.createFixture(fixtureDef).setUserData(this);
    }

    public void movePlatform(float vX, float vY) {
        body.setLinearVelocity(vX, vY);
    }

    public void update() {
        if(body.getPosition().y < MAP_HEIGHT) {
            body.destroyFixture(body.getFixtureList().first());
        }
    }

    public Body getBody() {
        return body;
    }

}
