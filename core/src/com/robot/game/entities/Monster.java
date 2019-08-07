package com.robot.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.abstractEnemies.EnemyArriveAI;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;
import static com.robot.game.util.Enums.Facing;
import static com.robot.game.util.Enums.Facing.LEFT;
import static com.robot.game.util.Enums.Facing.RIGHT;


public class Monster extends EnemyArriveAI {

    private boolean activated;
    private Facing facing;
    private float alpha = 1;

    public Monster(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        fixtureDef.density = 1;
        body.createFixture(fixtureDef).setUserData(this);

        if(object.getProperties().get("facing").equals("right")) {
            this.facing = RIGHT;
        }
        else {
            this.facing = LEFT;
        }

        if(facing == LEFT) {
            flip(true, false);
        }

        setSize(38 / PPM, 48 / PPM);
    }

    @Override
    public void update(float delta) {
        if(flagToChangeMask && body.getFixtureList().size != 0) {
            Fixture fixture = body.getFixtureList().first();
            StaticMethods.setMaskBit(fixture, fixture.getFilterData().maskBits &= ~ROBOT_CATEGORY); // does not collide with robot anymore
            flagToChangeMask = false;
        }

        if(flagToKill) {
            if(deadAnimationFinished()) {
                super.destroyBody();
                destroyed = true;
                flagToKill = false;
            }
            else {
                deadElapsed = (TimeUtils.nanoTime() - deadStartTime) * MathUtils.nanoToSec;
            }
        }

        if(!activated) {
            checkIfShouldBeActivated();
        }

        if(steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            super.applySteering(delta);
        }

        // update facing direction
        determineFacingDirection();

        // calculate the elapsed time of the animation
        elapsedAnim = (TimeUtils.nanoTime() - startTimeAnim) * MathUtils.nanoToSec;
    }

    @Override
    public void draw(Batch batch) {
        if(!activated) {
            setRegion(assets.monsterAssets.monsterAttackAnim.getKeyFrame(0));
        }
        else if(!dead && Math.abs(robot.getBody().getPosition().x - body.getPosition().x) <= 80 / PPM) {
            setRegion(assets.monsterAssets.monsterAttackAnim.getKeyFrame(elapsedAnim));
        }
        else if(!dead) {
            setRegion(assets.monsterAssets.monsterWalkAnim.getKeyFrame(elapsedAnim));
        }
        else {
            setSize(48 / PPM, 48 / PPM);
            setRegion(assets.monsterAssets.monsterDeadAnim.getKeyFrame(deadElapsed));
        }

        if(facing == RIGHT) {
            if(isFlipX())
                flip(true, false);
        }
        else if(facing == LEFT) {
            if(!isFlipX())
                flip(true, false);
        }

        // attach enemy sprite to body
        if(!dead) {
            setPosition(body.getPosition().x - 38f / 2 / PPM, body.getPosition().y - 48f / 2 / PPM);
        }
        super.draw(batch);
    }

    private void checkIfShouldBeActivated() {
        if(Math.abs(robot.getBody().getPosition().x - body.getPosition().x) <= 256 / PPM
                && Math.abs(robot.getBody().getPosition(). y - body.getPosition().y) <= 48 / PPM) {
            arrive.setEnabled(true);
            activated = true;
            Gdx.app.log("Monster", "Arrive was activated for monster");
        }
    }

    private void determineFacingDirection() {
        if(body.getLinearVelocity().x > 0.5f && facing != RIGHT) {
            facing = RIGHT;
        }
        else if(body.getLinearVelocity().x < -0.5f && facing != LEFT) {
            facing = LEFT;
        }
    }

    private boolean deadAnimationFinished() {
        return deadElapsed >= playScreen.getAssets().monsterAssets.monsterDeadAnim.getAnimationDuration();
    }

    @Override
    public int getDamage() {
        return 0;
    }
}
