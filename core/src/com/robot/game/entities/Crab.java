package com.robot.game.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class Crab extends Enemy {

    public Crab(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        // set the size of the crab sprite
        setSize(CRAB_WIDTH / PPM, CRAB_HEIGHT / PPM);
        // set the origin of rotation to the middle of the sprite
        setOrigin(CRAB_WIDTH / 2 / PPM, CRAB_HEIGHT / 2 / PPM);
    }

    @Override
    public void update(float delta) {
        if(flagToKill) {
            if(deadElapsed >= DEAD_TIMER)
                destroyBody();
            else
                deadElapsed = (TimeUtils.nanoTime() - deadStartTime) * MathUtils.nanoToSec;
        }

        if(aiPathFollowing && steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            applySteering(steeringOutput, delta);

            // rotate path-following crabs based on their velocity
            float vX = body.getLinearVelocity().x;
            float vY = body.getLinearVelocity().y;
            if(Math.abs(vX) >= 0.1f)
                body.setTransform(body.getWorldCenter(), (float) Math.atan2(vY, -vX));
            if(Math.abs(vY) >= 0.1f)
                body.setTransform(body.getWorldCenter(), (float) Math.atan2(-vY, -vX));
        }
        else if(!aiPathFollowing && getPosition().x < startX / PPM || getPosition().x > endX / PPM)
            reverseVelocity(true, false);
    }

    @Override
    public void draw(Batch batch) {
        elapsedAnim = (TimeUtils.nanoTime() - startTimeAnim) * MathUtils.nanoToSec;

        if(!flagToKill) {
            textureRegion = assets.crabAssets.crabWalkAnimation.getKeyFrame(elapsedAnim);
        }
        else {
            textureRegion = assets.crabAssets.crabDeadAnimation.getKeyFrame(elapsedAnim);
        }

        // set the appropriate region and attach sprite to body
        setRegion(textureRegion);
        setPosition(body.getPosition().x - CRAB_WIDTH / 2 / PPM, body.getPosition().y - CRAB_HEIGHT / 2 / PPM);
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);
        super.draw(batch); // call to Sprite superclass
    }

    @Override
    public int getDamage() {
        return DAMAGE_FROM_CRAB;
    }
}
