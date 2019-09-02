package com.robot.game.util.staticMethods;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.Assets;

import static com.robot.game.util.constants.Constants.*;

public class SpriteDrawing {

    public static void drawBat(Batch batch, Sprite sprite, Assets assets, Enemy bat) {
        // determine the appropriate texture region of the animation
        if(!bat.isDead()) {
            sprite.setRegion(assets.batAssets.batFlyAnimation.getKeyFrame(bat.getElapsedAnim()));
        }
        else {
            sprite.setRegion(assets.batAssets.batDeadAnimation.getKeyFrame(bat.getElapsedAnim()));
        }

        // attach sprite to body
        sprite.setPosition(bat.getBody().getPosition().x - BAT_WIDTH / 2 / PPM, bat.getBody().getPosition().y - BAT_HEIGHT / 2 / PPM);

        sprite.draw(batch);
    }

    public static void drawCrab(Batch batch, Sprite sprite, Assets assets, Enemy crab) {
        // determine the appropriate texture region of the animation
        if(!crab.isDead()) {
            sprite.setRegion(assets.crabAssets.crabWalkAnimation.getKeyFrame(crab.getElapsedAnim()));
        }
        else {
            sprite.setRegion(assets.crabAssets.crabDeadAnimation.getKeyFrame(crab.getElapsedAnim()));
        }

        // attach enemy sprite to body
        sprite.setPosition(crab.getBody().getPosition().x - CRAB_WIDTH / 2 / PPM, crab.getBody().getPosition().y - CRAB_HEIGHT / 2 / PPM);

        // rotate sprite with body
        sprite.setRotation(crab.getBody().getAngle() * MathUtils.radiansToDegrees);

        sprite.draw(batch); // call to Sprite superclass
    }
}
