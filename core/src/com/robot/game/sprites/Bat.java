package com.robot.game.sprites;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.*;

public class Bat extends Enemy /*implements Steerable<Vector2>*/ {

    private TextureRegion textureRegion;
    private float deadStartTime;
    private float deadElapsed;

    // animation
    private float startTimeAnim;
    private float elapsedAnim;

    public Bat(World world, Body body, FixtureDef fixtureDef, MapObject object) {
        super(world, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        this.startTimeAnim = TimeUtils.nanoTime();

        setSize(BAT_WIDTH / PPM, BAT_HEIGHT / PPM);
    }

    @Override
    public void update(float delta) {
        // if bat is flagged to be killed
        if(flagToKill) {
            dead = true;
            if(deadElapsed >= 1.0f) {
                body.setLinearVelocity(0, -5);
                flagToKill = false;
            }
            else
                deadElapsed = (TimeUtils.nanoTime() - deadStartTime) * MathUtils.nanoToSec;
        }
        // if bat is dead and out of the map
        else if(dead && body.getPosition().y < 0)
            destroyBody();
        else if(!dead) {
            if(aiPathFollowing && steeringBehavior != null) {
                steeringBehavior.calculateSteering(steeringOutput);
                applySteering(steeringOutput, delta);
            }

            // bat moving horizontally
            else if(horizontal && outOfRangeX())
                reverseVelocity(true, false);

            // bat moving vertically
            else if(outOfRangeY())
                reverseVelocity(false, true);
        }

        // attach sprite to body
//        batSprite.setPosition(body.getPosition().x - BAT_WIDTH / 2 / PPM, body.getPosition().y - BAT_HEIGHT / 2 / PPM); // for rectangle
    }

    @Override
    public void draw(Batch batch) {
        elapsedAnim = (TimeUtils.nanoTime() - startTimeAnim) * MathUtils.nanoToSec;
        textureRegion = Assets.getInstance().batAssets.batFlyAnimation.getKeyFrame(elapsedAnim);

        // attach sprite to body and set the appropriate region
        setPosition(body.getPosition().x - BAT_WIDTH / 2 / PPM, body.getPosition().y - BAT_HEIGHT / 2 / PPM);
        setRegion(textureRegion);
        super.draw(batch);
    }

    public void setDeadStartTime(float deadStartTime) {
        this.deadStartTime = deadStartTime;
    }
}


