package com.robot.game.entities.bat;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.abstractEnemies.EnemyPathFollowingAI;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.staticMethods.SpriteDrawing;
import com.robot.game.util.staticMethods.StaticMethods;

import static com.robot.game.util.constants.Constants.*;

public class BatPathFollowingAI extends EnemyPathFollowingAI {

    public BatPathFollowingAI(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        // set the size of the bat sprite
        sprite.setSize(BAT_WIDTH / PPM, BAT_HEIGHT / PPM);
    }

    @Override
    public void update(float delta) {
        //System.out.println(followPathBehaviour.onPath);
        if(flagToChangeMask && body.getFixtureList().size != 0) {
            StaticMethods.setMaskBit(body.getFixtureList().first(), NOTHING_MASK);
            flagToChangeMask = false;
        }

        // check if enemy should be activated
        if(!activated) {
            this.checkIfShouldBeActivated();
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
        else if(activated && !dead){
            followPathBehaviour.follow();
        }

        // calculate the elapsed time of the animation
        elapsedAnim = (TimeUtils.nanoTime() - startTimeAnim) * MathUtils.nanoToSec;
    }

    @Override
    public void draw(Batch batch) {
        SpriteDrawing.drawBat(batch, sprite, assets, this);
    }

    private void checkIfShouldBeActivated() {
        if(Math.abs(robot.getBody().getPosition().x - body.getPosition().x) <= activationRange / PPM) {
            activated = true;
            Gdx.app.log("BatAI", "FollowPath was activated for bat");
        }
    }

    @Override
    public int getDamage() {
        return playScreen.isDamageON() ? DAMAGE_FROM_BAT : 0;
    }

}
