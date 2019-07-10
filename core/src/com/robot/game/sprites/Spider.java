package com.robot.game.sprites;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import static com.robot.game.util.Constants.PPM;

public class Spider extends Enemy {

    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float vX;
    private float vY;

    public Spider(Body body, FixtureDef fixtureDef, float offset, String platformID, MapObject object, boolean aiPathFollowing) {
        super(body, fixtureDef, offset, platformID, object, aiPathFollowing);

        body.createFixture(fixtureDef).setUserData(this);

        if(!aiPathFollowing) {
            this.startX = (float) object.getProperties().get("startX");
            this.startY = (float) object.getProperties().get("startY");
            this.endX = (float) object.getProperties().get("endX");
            this.endY = (float) object.getProperties().get("endY");

            this.vX = (float) object.getProperties().get("vX");
            this.vY = (float) object.getProperties().get("vY");

            body.setLinearVelocity(vX, vY);
        }
    }

    public void update(float delta) {
        if(aiPathFollowing && steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            applySteering(steeringOutput, delta);
        }
        else if(!aiPathFollowing && getPosition().x < startX / PPM || getPosition().x > endX / PPM)
            reverseVelocity(true, false);
    }

}
