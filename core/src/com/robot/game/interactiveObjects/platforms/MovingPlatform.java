package com.robot.game.interactiveObjects.platforms;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.playscreens.PlayScreen;

import static com.robot.game.util.constants.Constants.PPM;

public class MovingPlatform extends InteractivePlatform {

    protected float startX;
    protected float startY;
    protected float endX;
    protected float endY;
    protected boolean horizontal;

    // will probably need to pass the whole map object
    public MovingPlatform(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, object);
        body.createFixture(fixtureDef).setUserData(this);

        this.startX = (float) object.getProperties().get("startX");
        this.startY = (float) object.getProperties().get("startY");
        this.endX = (float) object.getProperties().get("endX");
        this.endY = (float) object.getProperties().get("endY");

        this.horizontal = (vX != 0);

        // if platform is not an elevator (activated by robot), move it
        if(!(this instanceof Elevator)) {
            movePlatform();
        }
    }

    @Override
    public void update(float delta) {

        // moving horizontally
        if(horizontal && outOfRangeX()) {
            reverseVelocity(true, false);
        }
        // moving vertically
        else if(outOfRangeY()) {
            reverseVelocity(false, true);
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
    protected boolean outOfRangeX() {
        return body.getPosition().x <= startX / PPM || body.getPosition().x >= endX / PPM;
    }

    // check if moving platform is outside its moving range in y-direction
    protected boolean outOfRangeY() {
        return body.getPosition().y <= startY / PPM || body.getPosition().y >= endY / PPM;
    }

    public boolean isHorizontal() {
        return horizontal;
    }
}
