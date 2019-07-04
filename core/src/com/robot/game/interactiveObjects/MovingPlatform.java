package com.robot.game.interactiveObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import static com.robot.game.util.Constants.PPM;

public class MovingPlatform {

    private World world;
    private Body body;

    public MovingPlatform(World world, Body body, FixtureDef fixtureDef, float vX, float vY) {
        this.world = world;
        this.body = body;
        body.createFixture(fixtureDef).setUserData(this);

        body.setLinearVelocity(vX, vY);
    }

    public void update(float delta) {
        // this is used for constantly moving platforms (probably make these variables in properties)
        if(body.getPosition().y < 110 / PPM || body.getPosition().y > 324 / PPM)
            this.reverseVelocity(false, true);
    }

    // reverse velocity of a moving platform
    public void reverseVelocity(boolean reverseVx, boolean reverseVy) {
        if(reverseVx)
            body.setLinearVelocity(-body.getLinearVelocity().x, body.getLinearVelocity().y);
        if(reverseVy)
            body.setLinearVelocity(body.getLinearVelocity().x, -body.getLinearVelocity().y);
    }

    public Body getBody() {
        return body;
    }
}
