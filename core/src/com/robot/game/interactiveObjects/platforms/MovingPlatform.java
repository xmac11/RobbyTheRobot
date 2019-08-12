package com.robot.game.interactiveObjects.platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.PPM;

public class MovingPlatform extends InteractivePlatform {

    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private boolean waiting;
    private boolean shouldStop;
    private boolean stopped;
    private boolean horizontal;

    // will probably need to pass the whole map object
    public MovingPlatform(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, object);
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
            if(shouldStop && outOfRangeX() && !stopped) {
                stop();
                stopped = true;
            }
            else if(outOfRangeX())
                reverseVelocity(true, false);
        }
        // moving vertically
        else {
            if(shouldStop && outOfRangeY() && !stopped) {
                stop();
                stopped = true;
            }
            else if (outOfRangeY())
                this.reverseVelocity(false, true);
        }
    }

    // reverse velocity of a moving platform
    private void reverseVelocity(boolean reverseVx, boolean reverseVy) {
        if(reverseVx)
            body.setLinearVelocity(-body.getLinearVelocity().x, body.getLinearVelocity().y);
        if(reverseVy)
            body.setLinearVelocity(body.getLinearVelocity().x, -body.getLinearVelocity().y);
    }

    // check if moving platform is outside its moving range in x-direction
    private boolean outOfRangeX() {
        return body.getPosition().x <= startX / PPM || body.getPosition().x >= endX / PPM;
    }

    // check if moving platform is outside its moving range in y-direction
    private boolean outOfRangeY() {
        return body.getPosition().y <= startY / PPM || body.getPosition().y >= endY / PPM;
    }

    // stop platform
    private void stop() {
        body.setLinearVelocity(0, 0);
        Gdx.app.log("MovingPlatform", "Platform stopped");

        // for the case of the 'elevator' in the cave, in order for the robot to stop with it
        if(!horizontal && playScreen.getRobot().isOnInteractivePlatform()) {
            playScreen.getRobot().getBody().setLinearVelocity(playScreen.getRobot().getBody().getLinearVelocity().x, 0);
            Gdx.app.log("MovingPlatform", "Robot stopped with vertically moving platform");
        }
    }

    public boolean isWaiting() {
        return waiting;
    }

    public boolean shouldStop() {
        return shouldStop;
    }

    public boolean isStopped() {
        return stopped;
    }
}
