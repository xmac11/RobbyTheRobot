package com.robot.game.sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.*;

public class Crab extends Enemy {

    public Crab(World world, Body body, FixtureDef fixtureDef, MapObject object) {
        super(world, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        // set the size of the crab sprite
        setSize(CRAB_WIDTH / PPM, CRAB_HEIGHT / PPM);
    }

    @Override
    public void update(float delta) {
        if(flagToKill) {
            dead = true;
            if(deadElapsed >= 1.2f) {
                destroyBody();
                flagToKill = false;
            }
            else
                deadElapsed = (TimeUtils.nanoTime() - deadStartTime) * MathUtils.nanoToSec;
        }

        if(aiPathFollowing && steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            applySteering(steeringOutput, delta);
        }
        else if(!aiPathFollowing && getPosition().x < startX / PPM || getPosition().x > endX / PPM)
            reverseVelocity(true, false);
    }

    @Override
    public void draw(Batch batch) {
        elapsedAnim = (TimeUtils.nanoTime() - startTimeAnim) * MathUtils.nanoToSec;

        if(!dead) {
            textureRegion = Assets.getInstance().crabAssets.crabWalkAnimation.getKeyFrame(elapsedAnim);
        }
        else {
            textureRegion = Assets.getInstance().crabAssets.crabDeadAnimation.getKeyFrame(elapsedAnim);
        }

        // attach sprite to body and set the appropriate region
        setPosition(body.getPosition().x - CRAB_WIDTH / 2 / PPM, body.getPosition().y - CRAB_HEIGHT / 2 / PPM);
        setRegion(textureRegion);
        super.draw(batch); // call to Sprite superclass
    }

}
