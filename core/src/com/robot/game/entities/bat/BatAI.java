package com.robot.game.entities.bat;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.abstractEnemies.EnemyAI;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;

public class BatAI extends EnemyAI {

    public BatAI(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        if(object.getProperties().containsKey("waitForPlayer")) {
            body.setActive(false);
        }

        // set the size of the bat sprite
        setSize(BAT_WIDTH / PPM, BAT_HEIGHT / PPM);
    }

    @Override
    public void update(float delta) {
        if(flagToChangeMask && body.getFixtureList().size != 0) {
            StaticMethods.setMaskBit(body.getFixtureList().first(), NOTHING_MASK);
            flagToChangeMask = false;
        }

        // if bat is flagged to be killed
        if(flagToKill) {
            if(deadElapsed >= DEAD_TIMER) {
                body.setLinearVelocity(0, -8);
                flagToKill = false;
            }
            else {
                deadElapsed = (TimeUtils.nanoTime() - deadStartTime) * MathUtils.nanoToSec;
            }
        }
        // if the bat is out of the map (dead), destroy it
        else if(body.getPosition().y < 0 && !destroyed) {
            super.destroyBody();
            destroyed = true;
        }
        else if(!dead){
            if(steeringBehavior != null) {
                steeringBehavior.calculateSteering(steeringOutput);
                applySteering(steeringOutput, delta);
            }
        }

        // calculate the elapsed time of the animation
        elapsedAnim = (TimeUtils.nanoTime() - startTimeAnim) * MathUtils.nanoToSec;
    }

    @Override
    public void draw(Batch batch) {
        // determine the appropriate texture region of the animation
        if(!dead) {
            setRegion(assets.batAssets.batFlyAnimation.getKeyFrame(elapsedAnim));
        }
        else {
            setRegion(assets.batAssets.batDeadAnimation.getKeyFrame(elapsedAnim));
        }

        // attach sprite to body
        setPosition(body.getPosition().x - BAT_WIDTH / 2 / PPM, body.getPosition().y - BAT_HEIGHT / 2 / PPM);

        super.draw(batch); // call to Sprite superclass
    }

    @Override
    public int getDamage() {
        return DAMAGE_FROM_BAT;
    }

}