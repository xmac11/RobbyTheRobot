package com.robot.game.interactiveObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import static com.robot.game.util.Constants.PPM;

public class MovingPlatform extends InteractivePlatform {

    public MovingPlatform(World world, Body body, FixtureDef fixtureDef, float vX, float vY) {
        super(world, body);
        body.createFixture(fixtureDef).setUserData(this);

        body.setLinearVelocity(vX, vY);
    }

    @Override
    public void update(float delta) {
        // this is used for constantly moving platforms (probably make these variables in properties)
        if(body.getPosition().y < 110 / PPM || body.getPosition().y > 324 / PPM)
            this.reverseVelocity(false, true);
    }


    @Override
    public boolean isDestroyed() {
        return false;
    }

    // reverse velocity of a moving platform
    private void reverseVelocity(boolean reverseVx, boolean reverseVy) {
        if(reverseVx)
            body.setLinearVelocity(-body.getLinearVelocity().x, body.getLinearVelocity().y);
        if(reverseVy)
            body.setLinearVelocity(body.getLinearVelocity().x, -body.getLinearVelocity().y);
    }
}
