package com.robot.game.entities.crab;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.abstractEnemies.EnemyPathFollowingAI;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.SpriteDrawing;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;

public class CrabPathFollowingAI extends EnemyPathFollowingAI {

    public CrabPathFollowingAI(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        // set the size of the crab sprite
        sprite.setSize(CRAB_WIDTH / PPM, CRAB_HEIGHT / PPM);
        // set the origin of rotation to the middle of the sprite
        sprite.setOrigin(CRAB_WIDTH / 2 / PPM, CRAB_HEIGHT / 2 / PPM);
    }

    @Override
    public void update(float delta) {
        if(flagToChangeMask && body.getFixtureList().size != 0) {
            StaticMethods.setMaskBit(body.getFixtureList().first(), NOTHING_MASK);
            flagToChangeMask = false;
        }

        if(flagToKill) {
            if(deadElapsed >= DEAD_TIMER && !destroyed) {
                super.destroyBody();
                destroyed = true;
                flagToKill = false;
            }
            else {
                deadElapsed = (TimeUtils.nanoTime() - deadStartTime) * MathUtils.nanoToSec;
            }
        }

        if(steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            super.applySteering(delta);

            // rotate path-following crabs based on their velocity
            float vX = body.getLinearVelocity().x;
            float vY = body.getLinearVelocity().y;
            if(Math.abs(vX) >= 0.1f)
                body.setTransform(body.getWorldCenter(), (float) Math.atan2(vY, -vX));
            if(Math.abs(vY) >= 0.1f)
                body.setTransform(body.getWorldCenter(), (float) Math.atan2(-vY, -vX));
        }

        // calculate the elapsed time of the animation
        elapsedAnim = (TimeUtils.nanoTime() - startTimeAnim) * MathUtils.nanoToSec;
    }

    @Override
    public void draw(Batch batch) {
        SpriteDrawing.drawCrab(batch, sprite, assets, this);
    }

    @Override
    public int getDamage() {
        return DAMAGE_ON ? DAMAGE_FROM_CRAB : 0;
    }
}
