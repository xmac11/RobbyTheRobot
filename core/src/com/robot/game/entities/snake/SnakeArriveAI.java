package com.robot.game.entities.snake;

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

public class SnakeArriveAI extends EnemyArriveAI {

    public SnakeArriveAI(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);
        body.createFixture(fixtureDef).setUserData(this);


        sprite.setSize(SNAKE_WIDTH / PPM, SNAKE_HEIGHT / PPM);
    }

    @Override
    public void update(float delta) {
        if(flagToChangeMask && body.getFixtureList().size != 0) {
            super.removeCollisionWithRobot();
        }

        // check if dead
        super.checkIfDead();

        // check if enemy should be activated
        if(!activated && !locked) {
            this.checkIfShouldBeActivated();
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
            sprite.setRegion(assets.snakeAssets.slitherAnimation.getKeyFrame(0));
        }
        // attacking
        else if(!dead && Math.abs(robot.getBody().getPosition().x - body.getPosition().x) <= 64 / PPM
                && Math.abs(robot.getBody().getPosition().y - body.getPosition().y) <= 48 / PPM) {
            sprite.setRegion(assets.snakeAssets.biteAnimation.getKeyFrame(elapsedAnim));
        }
        // slithering
        else if(!dead) {
            sprite.setRegion(assets.snakeAssets.slitherAnimation.getKeyFrame(elapsedAnim));
        }
        // dead
        else {
            sprite.setRegion(assets.snakeAssets.deadAnimation.getKeyFrame(deadElapsed));
        }

        // check if the texture has to be flipped based on the monster's facing direction
        StaticMethods.checkToFlipTexture(sprite, facing);

        sprite.setPosition(body.getPosition().x - SNAKE_WIDTH / 2 / PPM, body.getPosition().y - SNAKE_HEIGHT / 2 / PPM + 5 / PPM);

        sprite.draw(batch);
    }

    private void checkIfShouldBeActivated() {
        // snakes are activated when the get into the screen
        if(Math.abs(robot.getBody().getPosition().x - body.getPosition().x) <= playScreen.getViewport().getWorldWidth() / 2
                && Math.abs(robot.getBody().getPosition(). y - body.getPosition().y) <= 48 / PPM) {
            arrive.setEnabled(true);
            super.setActivated(true);
        }
    }

    @Override
    public int getDamage() {
        return playScreen.isDamageON() ? DAMAGE_FROM_SNAKE : 0;
    }
}
