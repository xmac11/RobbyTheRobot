package com.robot.game.entities.abstractEnemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.screens.playscreens.PlayScreen;
import com.robot.game.steeringBehaviours.SeekBehaviour;
import com.robot.game.util.staticMethods.StaticMethods;

import static com.robot.game.util.constants.Constants.DEAD_TIMER;
import static com.robot.game.util.constants.Constants.ROBOT_CATEGORY;
import static com.robot.game.util.constants.Enums.Facing;
import static com.robot.game.util.constants.Enums.Facing.LEFT;
import static com.robot.game.util.constants.Enums.Facing.RIGHT;

public abstract class EnemySeekAI extends Enemy {

    // EnemyAI
    protected float maxLinearSpeed;

    protected Facing facing;
    protected boolean activated;
    protected boolean locked;

    protected SeekBehaviour seekBehaviour;

    public EnemySeekAI(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);
        this.robot = playScreen.getRobot();

        this.maxLinearSpeed = 2f;

        if(object.getProperties().get("facing").equals("right")) {
            this.facing = RIGHT;
        }
        else {
            this.facing = LEFT;
        }

        if(facing == LEFT) {
            sprite.flip(true, false);
        }

        this.seekBehaviour = new SeekBehaviour(body, maxLinearSpeed);
    }

    protected void checkIfDead() {
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
        if(body.getPosition().y < 0 && !destroyed) {
            super.destroyBody();
            destroyed = true;
        }
    }

    protected void removeCollisionWithRobot() {
        Fixture fixture = body.getFixtureList().first();
        StaticMethods.setMaskBit(fixture, fixture.getFilterData().maskBits &= ~ROBOT_CATEGORY); // does not collide with robot anymore
        flagToChangeMask = false;
    }

    protected void determineFacingDirection() {
        if(body.getLinearVelocity().x > 0.5f && facing != RIGHT) {
            facing = RIGHT;
        }
        else if(body.getLinearVelocity().x < -0.5f && facing != LEFT) {
            facing = LEFT;
        }
    }

    public Facing getFacing() {
        return facing;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
        Gdx.app.log("EnemyArriveAI", "Arrive = " + String.valueOf(activated).toUpperCase() + " for " + this.getClass());
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        Gdx.app.log("EnemyArriveAI", "Arrive was locked for " + this.getClass());
    }


    @Override
    public void setToNull() {
        seekBehaviour.setToNull();
        facing = null;
        seekBehaviour = null;
        Gdx.app.log("EnemyArriveAI", "Objects were set to null");
    }
}
