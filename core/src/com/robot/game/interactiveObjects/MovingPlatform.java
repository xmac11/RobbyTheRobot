package com.robot.game.interactiveObjects;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.robot.game.sprites.Robot;

import static com.robot.game.util.Constants.PPM;

public class MovingPlatform {

    private World world;
    private Body body;
    private boolean destroyed;
    private Robot childRobot;

    public MovingPlatform(World world, Body body, FixtureDef fixtureDef) {
        this.world = world;
        this.body = body;
        body.setLinearDamping(0.8f);
        body.createFixture(fixtureDef).setUserData(this);

        // this is used for constantly moving platforms
        //body.setLinearVelocity(0, -3);
    }

    public void movePlatform(float vX, float vY) {
        body.setLinearVelocity(vX, vY);
    }

    public void update() {
        // if body is out of bounds, destroy it
        if(body.getPosition().y < 0 && !destroyed) {
            world.destroyBody(body);
            destroyed = true;
        }

        // this is used for constantly moving platforms
        /*if(body.getPosition().y < 68 / PPM || body.getPosition().y > 324 / PPM)
            this.reverseVelocity(false, true);*/
    }

    // this is used for constantly moving platforms
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
