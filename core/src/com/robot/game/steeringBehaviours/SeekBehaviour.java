package com.robot.game.steeringBehaviours;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;

public class SeekBehaviour {

    private Body body;
    private boolean isDynamicBody;
    private float maxLinearVelocity;
    private Vector2 desiredVelocity = new Vector2();

    public SeekBehaviour(Body body, float maxLinearVelocity) {
        this.body = body;
        this.isDynamicBody = (body.getType() == BodyDef.BodyType.DynamicBody);
        this.maxLinearVelocity = maxLinearVelocity;
    }

    public void seek(Vector2 target) {
        // vector from position to target
        desiredVelocity.set(target);                    // Vector2 desiredVelocity = target.cpy().sub(body.getPosition());
        desiredVelocity.sub(body.getPosition());

        // scale to maximum linear velocity
        desiredVelocity.setLength(maxLinearVelocity);

        // steering = desired velocity - current velocity
        Vector2 steering = desiredVelocity.sub(body.getLinearVelocity());

        if(isDynamicBody) {
            float newVelocityX = MathUtils.clamp(body.getLinearVelocity().x + steering.x, -2, 2);
            body.setLinearVelocity(newVelocityX, body.getLinearVelocity().y);
        }
        else {
            body.setLinearVelocity(body.getLinearVelocity().add(steering));
        }
    }

    public void setToNull() {
        desiredVelocity = null;
        Gdx.app.log("SeekBehaviour", "Objects were set to null");
    }
}
