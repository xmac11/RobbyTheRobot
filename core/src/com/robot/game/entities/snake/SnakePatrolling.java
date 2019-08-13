package com.robot.game.entities.snake;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.entities.abstractEnemies.EnemyPatrolling;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;
import static com.robot.game.util.Enums.Facing.LEFT;
import static com.robot.game.util.Enums.Facing.RIGHT;

public class SnakePatrolling extends EnemyPatrolling {

    public SnakePatrolling(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        if(object.getProperties().get("facing").equals("right")) {
            super.facing = RIGHT;
        }
        else {
            super.facing = LEFT;
        }

        if(facing == LEFT) {
            sprite.flip(true, false);
        }

        sprite.setSize(SNAKE_WIDTH / PPM, SNAKE_HEIGHT / PPM);
    }

    @Override
    public void update(float delta) {
        super.updateHorizontalPatrolling(delta);

        super.determineFacingDirection();
    }

    @Override
    public void draw(Batch batch) {
        // attacking
        if(!dead && Math.abs(robot.getBody().getPosition().x - body.getPosition().x) <= 64 / PPM
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



    @Override
    public int getDamage() {
        return playScreen.isDamageON() ? DAMAGE_FROM_SNAKE : 0;
    }
}
