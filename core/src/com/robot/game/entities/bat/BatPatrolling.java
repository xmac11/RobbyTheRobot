package com.robot.game.entities.bat;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.abstractEnemies.EnemyPatrolling;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.SpriteDrawing;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;
import static com.robot.game.util.Constants.PPM;

public class BatPatrolling extends EnemyPatrolling {

    public BatPatrolling(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        // set the size of the bat sprite
        sprite.setSize(BAT_WIDTH / PPM, BAT_HEIGHT / PPM);
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
            // bat moving horizontally
            if(horizontal && outOfRangeX())
                reverseVelocity(true, false);

                // bat moving vertically
            else if(outOfRangeY())
                reverseVelocity(false, true);
        }

        // calculate the elapsed time of the animation
        elapsedAnim = (TimeUtils.nanoTime() - startTimeAnim) * MathUtils.nanoToSec;
    }

    @Override
    public void draw(Batch batch) {
        SpriteDrawing.drawBat(batch, sprite, assets, this);
    }

    @Override
    public int getDamage() {
        return DAMAGE_ON ? DAMAGE_FROM_BAT : 0;
    }
}
