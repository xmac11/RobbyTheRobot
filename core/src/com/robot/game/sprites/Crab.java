package com.robot.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import static com.robot.game.util.Constants.*;

public class Crab extends Enemy {

    public Sprite spiderSprite;


    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float vX;
    private float vY;

    public Crab(Body body, FixtureDef fixtureDef, MapObject object) {
        super(body, fixtureDef, object);

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

        this.spiderSprite = new Sprite(new Texture("crab.png"));

    }

    public void update(float delta) {
        if(aiPathFollowing && steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            applySteering(steeringOutput, delta);
        }
        else if(!aiPathFollowing && getPosition().x < startX / PPM || getPosition().x > endX / PPM)
            reverseVelocity(true, false);

        // attach sprite to body
        spiderSprite.setPosition(body.getPosition().x - (CRAB_WIDTH / 2) / PPM, body.getPosition().y - CRAB_HEIGHT / 2 / PPM);
    }

}
