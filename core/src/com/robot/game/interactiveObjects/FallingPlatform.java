package com.robot.game.interactiveObjects;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;

public class FallingPlatform extends InteractivePlatform {

    private float delay;
    private float startTime; // time the player jumped on the falling platform
    private float elapsed;
    private boolean flagToMove;
    private boolean falling;
    private boolean isDestroyed;
    private boolean toggle;

    public FallingPlatform(World world, Body body, FixtureDef fixtureDef, float delay) {
        super(world, body);
        body.createFixture(fixtureDef).setUserData(this);

        this.delay = delay;
        this.elapsed = 0;
    }

    private void movePlatform(float vX, float vY) {
        body.setLinearVelocity(vX, vY);
    }

    @Override
    public void update(float delta) {

        // if body is out of bounds, destroy it
        if(body.getPosition().y < 0 && !isDestroyed) {
            world.destroyBody(body);
            isDestroyed = true;
        }

        if(flagToMove) {
            if(elapsed >= delay) {
                movePlatform(0, -8);
                flagToMove = false;
                falling = true;
            }
            else {
                if(toggle)
                    body.setLinearVelocity(3, 3);
                else
                    body.setLinearVelocity(-3, -3);

                toggle = !toggle;
                elapsed = (TimeUtils.nanoTime() - startTime) * MathUtils.nanoToSec;
            }
        }
    }

    @Override
    public boolean isDestroyed() {
        return isDestroyed;
    }

    public void setFlagToMove(boolean flagToMove) {
        // keep track of the time the robot jumped on the falling platform
        this.startTime = TimeUtils.nanoTime();
        this.flagToMove = flagToMove;
    }

    public boolean isFalling() {
        return falling;
    }
}
