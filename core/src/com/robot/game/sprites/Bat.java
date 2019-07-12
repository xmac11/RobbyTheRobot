package com.robot.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;

import static com.robot.game.util.Constants.*;

public class Bat extends Enemy /*implements Steerable<Vector2>*/ {

    public Sprite batSprite;


    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private float vX;
    private float vY;
    private boolean horizontal;

    public Bat(Body body, FixtureDef fixtureDef, MapObject object) {
        super(body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        if(!aiPathFollowing) {
            this.startX = (float) object.getProperties().get("startX");
            this.startY = (float) object.getProperties().get("startY");
            this.endX = (float) object.getProperties().get("endX");
            this.endY = (float) object.getProperties().get("endY");

            this.vX = (float) object.getProperties().get("vX");
            this.vY = (float) object.getProperties().get("vY");

            this.horizontal = vX != 0;

            body.setLinearVelocity(vX, vY);
        }

        this.batSprite = new Sprite(new Texture("000.png"));
    }

    public void update(float delta) {
            if(aiPathFollowing && steeringBehavior != null) {
                steeringBehavior.calculateSteering(steeringOutput);
                applySteering(steeringOutput, delta);
            }
            // bat moving horizontally
            else if(horizontal) {
                if(getPosition().x < startX / PPM || getPosition().x > endX / PPM)
                    reverseVelocity(true, false);
            }
            // bat moving vertically
            else if(getPosition().y < startY / PPM || getPosition().y > endY / PPM)
                    reverseVelocity(false, true);

        // attach sprite to body
        batSprite.setPosition(body.getPosition().x - (BAT_WIDTH / 2) / PPM, body.getPosition().y - BAT_HEIGHT / 2 / PPM); // for rectangle
    }


}


