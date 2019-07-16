package com.robot.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;

import static com.robot.game.util.Constants.*;

public class Crab extends Enemy {

    public Sprite spiderSprite;

    public Crab(World world, Body body, FixtureDef fixtureDef, MapObject object) {
        super(world, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        this.spiderSprite = new Sprite(new Texture("crab.png"));

    }

    @Override
    public void update(float delta) {
        if(flagToKill) {
            dead = true;
            destroyBody();
            flagToKill = false;
        }

        if(aiPathFollowing && steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            applySteering(steeringOutput, delta);
        }
        else if(!aiPathFollowing && getPosition().x < startX / PPM || getPosition().x > endX / PPM)
            reverseVelocity(true, false);

        // attach sprite to body
        spiderSprite.setPosition(body.getPosition().x - (CRAB_WIDTH / 2) / PPM, body.getPosition().y - CRAB_HEIGHT / 2 / PPM);
    }

    @Override
    public void draw(Batch batch) {

    }

}
