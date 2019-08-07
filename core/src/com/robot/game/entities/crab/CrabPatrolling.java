package com.robot.game.entities.crab;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.abstractEnemies.EnemyPatrolling;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;

public class CrabPatrolling extends EnemyPatrolling {

    public CrabPatrolling(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        // set the size of the crab sprite
        setSize(CRAB_WIDTH / PPM, CRAB_HEIGHT / PPM);

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

        if(outOfRangeX())
            reverseVelocity(true, false);

        // calculate the elapsed time of the animation
        elapsedAnim = (TimeUtils.nanoTime() - startTimeAnim) * MathUtils.nanoToSec;
    }

    @Override
    public void draw(Batch batch) {
        // determine the appropriate texture region of the animation
        if(!dead) {
            setRegion(assets.crabAssets.crabWalkAnimation.getKeyFrame(elapsedAnim));
        }
        else {
            setRegion(assets.crabAssets.crabDeadAnimation.getKeyFrame(elapsedAnim));
        }

        // attach enemy sprite to body
        setPosition(body.getPosition().x - CRAB_WIDTH / 2 / PPM, body.getPosition().y - CRAB_HEIGHT / 2 / PPM);

        // rotate sprite with body
        setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        super.draw(batch); // call to Sprite superclass
    }

    @Override
    public int getDamage() {
        return DAMAGE_ON ? DAMAGE_FROM_CRAB : 0;
    }
}
