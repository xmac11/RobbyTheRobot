package com.robot.game.entities;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;

public class Fish extends Enemy {

    private float attackTimer;
    private Vector2 initialPosition;
    private boolean attacking;

    // splash animation
    private boolean splashActive;
    private float splashAnimElapsed;

    public Fish(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);

        body.createFixture(fixtureDef).setUserData(this);

        this.initialPosition = new Vector2(body.getPosition());

        // set the size of the crab sprite
        setSize(FISH_WIDTH / PPM, FISH_HEIGHT / PPM);
    }
    @Override
    public void update(float delta) {
        if(flagToChangeMask && body.getFixtureList().size != 0) {
            StaticMethods.setMaskBit(body.getFixtureList().first(), NOTHING_MASK);
            flagToChangeMask = false;
        }

        // if fish is not dead, determine whether it should attack
        if(!dead && !attacking) {
            checkIfShouldAttack(delta);
        }
        // if the fish is not dead (therefore attacking) and reaches its initial position with negative velocity, stop it
        else if(!dead /*&& attacking*/ && body.getPosition().y - initialPosition.y < 1f / PPM && body.getLinearVelocity().y < 0) {
            body.setLinearVelocity(0, 0);
            body.setTransform(initialPosition, 0);
            body.setGravityScale(0);
            attacking = false;

            splashActive = true;
        }
        else if(body.getPosition().y < -16 / PPM && !destroyed) { // -16 to give more time for the splash animation when fish dies
            super.destroyBody();
            destroyed = true;
        }

        // in this case the fish has died, so activate splash animation as well
        if(body.getPosition().y < initialPosition.y - 4 / PPM && !splashActive) {
            splashActive = true;
        }
        if(splashActive) {
            splashAnimElapsed += delta;
        }

        // calculate the elapsed time of the fish animation
        elapsedAnim = (TimeUtils.nanoTime() - startTimeAnim) * MathUtils.nanoToSec;
    }

    @Override
    public void draw(Batch batch) {
        if(!dead) {
            setRegion(assets.fishAssets.textureAnimation.getKeyFrame(elapsedAnim, true));
        }
        else{
            setRegion(assets.fishAssets.deadFish);
        }

        // attach enemy sprite to body
        setPosition(body.getPosition().x - FISH_WIDTH / 2 / PPM, body.getPosition().y - FISH_HEIGHT / 2 / PPM);
        super.draw(batch);

        handleSplashAnimation(batch);
    }

    private void checkIfShouldAttack(float delta) {
        if(attackTimer >= MathUtils.random(1f, 3f)) {
            body.setGravityScale(1);
            body.applyLinearImpulse(FISH_IMPULSE, body.getWorldCenter(), true);
            attacking = true;
            attackTimer = 0;
        }
        else {
            attackTimer += delta;
        }
    }

    private void handleSplashAnimation(Batch batch) {
        if(splashActive) {
            batch.draw(assets.splashAssets.splashAnimation.getKeyFrame(splashAnimElapsed),
                    initialPosition.x - 64f / 2 / PPM,
                    initialPosition.y,
                    64f / PPM,
                    64f / PPM);
        }

        // animation finished
        if(splashAnimElapsed > 7 * 0.05f) {
            splashActive = false;
            splashAnimElapsed = 0;
        }
    }

    @Override
    public int getDamage() {
        return DAMAGE_FROM_FISH;
    }
}
