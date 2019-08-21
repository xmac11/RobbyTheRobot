package com.robot.game.steeringBehaviours;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.robot.game.entities.Robot;

public class SeekBehaviour {

    private Robot robot;
    private Body body;

    public SeekBehaviour(Robot robot, Body body) {
        this.robot = robot;
        this.body = body;
    }

    public void seek(Robot target) {
        // vector from position to target
        Vector2 desired = target.getPosition().sub(body.getPosition());

        // scale to maximum linear velocity
        desired.setLength(2);

        // steering = desired - velocity
        Vector2 steering = desired.sub(body.getLinearVelocity());

        System.out.println(steering.x);
        float newVelocity = MathUtils.clamp(body.getLinearVelocity().x + steering.x, -2, 2);
        body.setLinearVelocity(newVelocity, body.getLinearVelocity().y);
    }
}
