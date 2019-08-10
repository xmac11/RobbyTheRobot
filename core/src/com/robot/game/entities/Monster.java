package com.robot.game.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.abstractEnemies.EnemyArriveAI;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;


public class Monster extends EnemyArriveAI {

    public Monster(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        fixtureDef.density = 1;
        body.createFixture(fixtureDef).setUserData(this);

        sprite.setSize(MONSTER_WIDTH / PPM, MONSTER_HEIGHT / PPM);
    }

    @Override
    public void update(float delta) {
        if(flagToChangeMask && body.getFixtureList().size != 0) {
            super.removeCollisionWithRobot();
        }

        // check if dead
        super.checkIfDead();

        // check if enemy should be activated
        if(!activated) {
            this.checkIfShouldBeActivated();
        }

        // update state
        if(body.getLinearVelocity().y <= -1f && !falling) {
            falling = true;
        }
        else if(body.getLinearVelocity().y > -1f && falling) {
            falling = false;
        }

        // calculate steering
        if(activated && steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            super.applySteering(delta);
        }

        // update facing direction
        if(!dead) {
            super.determineFacingDirection();
        }

        // calculate the elapsed time of the animation
        elapsedAnim = (TimeUtils.nanoTime() - startTimeAnim) * MathUtils.nanoToSec;
    }

    @Override
    public void draw(Batch batch) {
        if(!activated) {
            sprite.setRegion(assets.monsterAssets.monsterAttackAnim.getKeyFrame(0));
        }
        // attacking
        else if(!dead && Math.abs(robot.getBody().getPosition().x - body.getPosition().x) <= 64 / PPM
                && Math.abs(robot.getBody().getPosition().y - body.getPosition().y) <= 16 / PPM) {
            sprite.setRegion(assets.monsterAssets.monsterAttackAnim.getKeyFrame(elapsedAnim));
        }
        else if(robot.isOnLadder()
                && Math.abs(robot.getBody().getPosition().x - body.getPosition().x) <= 32 / PPM
                && robot.getBody().getPosition(). y - body.getPosition().y > 16 / PPM) {
            arrive.setEnabled(false);
            super.setActivated(false);
        }
        // walking
        else if(!dead && !falling) {
            sprite.setRegion(assets.monsterAssets.monsterWalkAnim.getKeyFrame(elapsedAnim));
        }
        // falling
        else if(!dead) {
            sprite.setRegion(assets.monsterAssets.monsterWalkAnim.getKeyFrame(0));
        }
        // dead
        else {
            sprite.setSize(48 / PPM, 48 / PPM);
            sprite.setRegion(assets.monsterAssets.monsterDeadAnim.getKeyFrame(deadElapsed));
        }

        // check if the texture has to be flipped based on the monster's facing direction
        StaticMethods.checkToFlipTexture(sprite, facing);

        // attach enemy sprite to body
        if(!dead) {
            sprite.setPosition(body.getPosition().x - MONSTER_WIDTH / 2 / PPM, body.getPosition().y - MONSTER_HEIGHT / 2 / PPM);
        }
        else {
            sprite.setAlpha(0.6f);
            sprite.setPosition(body.getPosition().x - MONSTER_WIDTH / 2 / PPM - 16f / PPM, body.getPosition().y - MONSTER_HEIGHT / 2 / PPM);
        }
        sprite.draw(batch);
    }

    private void checkIfShouldBeActivated() {
        if(Math.abs(robot.getBody().getPosition().x - body.getPosition().x) <= playScreen.getViewport().getWorldWidth() / 2 - 48 / PPM
                && Math.abs(robot.getBody().getPosition(). y - body.getPosition().y) <= 32 / PPM) {
            arrive.setEnabled(true);
            super.setActivated(true);
        }
    }

    @Override
    public int getDamage() {
        return DAMAGE_ON ? DAMAGE_FROM_MONSTER : 0;
    }
}
