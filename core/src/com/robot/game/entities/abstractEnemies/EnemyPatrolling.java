package com.robot.game.entities.abstractEnemies;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;
import static com.robot.game.util.Enums.Facing;
import static com.robot.game.util.Enums.Facing.LEFT;
import static com.robot.game.util.Enums.Facing.RIGHT;


public abstract class EnemyPatrolling extends Enemy {

    // EnemyPatrolling range (non-ai)
    protected float startX;
    protected float startY;
    protected float endX;
    protected float endY;
    protected float vX;
    protected float vY;
    protected boolean horizontal;

    protected Facing facing;

    public EnemyPatrolling(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        this.startX = (float) object.getProperties().get("startX");
        this.startY = (float) object.getProperties().get("startY");
        this.endX = (float) object.getProperties().get("endX");
        this.endY = (float) object.getProperties().get("endY");

        this.vX = (float) object.getProperties().get("vX");
        this.vY = (float) object.getProperties().get("vY");

        this.horizontal = (vX != 0);

        body.setLinearVelocity(vX, vY);
    }

    protected void updateHorizontalPatrolling(float delta) {
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

    // check if enemy is outside its moving range in x-direction
    protected boolean outOfRangeX() {
        return body.getPosition().x <= startX / PPM || body.getPosition().x >= endX / PPM;
    }

    // check if enemy is outside its moving range in y-direction
    protected boolean outOfRangeY() {
        return body.getPosition().y <= startY / PPM || body.getPosition().y >= endY / PPM;
    }

    protected void determineFacingDirection() {
        if(body.getLinearVelocity().x > 0.5f && facing != RIGHT) {
            facing = RIGHT;
        }
        else if(body.getLinearVelocity().x < -0.5f && facing != LEFT) {
            facing = LEFT;
        }
    }
}
