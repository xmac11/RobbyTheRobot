package com.robot.game.interactiveObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import static com.robot.game.util.Constants.PPM;

public class MovingPlatform extends InteractivePlatform {

    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private boolean waiting;
    private boolean shouldStop;
    private boolean horizontal;

    // will probably need to pass the whole map object
    public MovingPlatform(World world, Body body, FixtureDef fixtureDef, MapObject object) {
        super(world, body, (float) object.getProperties().get("vX"), (float) object.getProperties().get("vY"));
        body.createFixture(fixtureDef).setUserData(this);

        this.startX = (float) object.getProperties().get("startX");
        this.startY = (float) object.getProperties().get("startY");
        this.endX = (float) object.getProperties().get("endX");
        this.endY = (float) object.getProperties().get("endY");

        this.waiting = (boolean) object.getProperties().get("waiting");
        this.shouldStop = (boolean) object.getProperties().get("shouldStop");

        this.horizontal = vX != 0;

        if(!waiting)
            body.setLinearVelocity(vX, vY);
    }

    @Override
    public void update(float delta) {

        // moving horizontally
        if(horizontal) {
            if(shouldStop && body.getPosition().x > endX / PPM)
                stop();
            else if(body.getPosition().x < startX / PPM || body.getPosition().x > endX / PPM)
                reverseVelocity(true, false);
        }
        // moving vertically
        else if(body.getPosition().y < startY / PPM || body.getPosition().y > endY / PPM)
                this.reverseVelocity(false, true);
    }

    @Override
    public boolean isDestroyed() {
        return false;
    }

    public void movePlatform() {
        body.setLinearVelocity(vX, vY);
    }

    // reverse velocity of a moving platform
    private void reverseVelocity(boolean reverseVx, boolean reverseVy) {
        if(reverseVx)
            body.setLinearVelocity(-body.getLinearVelocity().x, body.getLinearVelocity().y);
        if(reverseVy)
            body.setLinearVelocity(body.getLinearVelocity().x, -body.getLinearVelocity().y);
    }

    // stop platform
    private void stop() {
        body.setLinearVelocity(0, 0);
    }

    public boolean isWaiting() {
        return waiting;
    }

    public boolean shouldStop() {
        return shouldStop;
    }
}
