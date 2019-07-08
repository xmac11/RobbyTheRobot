package com.robot.game.sprites;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.FollowPath;
import com.badlogic.gdx.ai.steer.utils.paths.LinePath;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;

import static com.robot.game.util.Constants.PPM;

public class Enemy implements Steerable<Vector2> {

//    SteeringBehavior
//    SteeringAcceleration

    private SteeringBehavior<Vector2> steeringBehavior;
    private SteeringAcceleration steeringOutput;
    private FollowPath<Vector2, LinePath.LinePathParam> followPath;
    public Array<Vector2> wayPoints;
    private LinePath<Vector2> linePath;

    private float maxLinearSpeed, maxLinearAcceleration;
    private float maxAngularSpeed, maxAngularAcceleration;
    private float boundingRadius;

    private Body body;
    private FixtureDef fixtureDef;
//    public CatmullRomSpline<Vector2> path;
//    public Array<Vector2> path;
    public Vector2 target;
    public float timer;
    public int point = 0;
    public Vector2 velocity = new Vector2();

    public Enemy(Body body, FixtureDef fixtureDef) {
        this.body = body;
        this.fixtureDef = fixtureDef;
//        body.setTransform(200 / PPM, 300 / PPM, 0);
        body.createFixture(fixtureDef).setUserData(this);

        /*this.target = new Vector2();
        this.path = new CatmullRomSpline<>(new Vector2[] { new Vector2(350 / PPM, 350 / PPM),
                new Vector2(350 / PPM, 75 / PPM),
                new Vector2(200 / PPM, 75 / PPM),
                new Vector2(200 / PPM, 350 / PPM) }, true);*/

        /*this.path = new Array<>();
        path.addAll( new Vector2(300 / PPM, 300 / PPM),
                new Vector2(300 / PPM, 75 / PPM),
                new Vector2(0, 75 / PPM),
                new Vector2(0, 300 / PPM));*/

        this.wayPoints = new Array<>(new Vector2[]{new Vector2(176 / PPM, 176 / PPM),
                                                    new Vector2(176 / PPM, 75 / PPM),
                                                    new Vector2(50 / PPM, 75 / PPM),
                                                    new Vector2(50 / PPM, 176 / PPM),
                                                    new Vector2(176 / PPM, 176 / PPM)});

        this.linePath = new LinePath<>(wayPoints, false);
        this.followPath = new FollowPath<>(this, linePath, 0.1f).setTimeToTarget(0.1f).setArrivalTolerance(0.001f).setDecelerationRadius(0.01f);
        followPath.setArrivalTolerance(2f);
        this.setSteeringBehavior(followPath);
        steeringBehavior.setEnabled(true);
        this.steeringOutput = new SteeringAcceleration<>(new Vector2());

        //        this.steeringBehavior = new FollowPath<>(this, path);
//        this.steeringBehavior = new Seek<>(this, followPath);

        this.maxLinearSpeed = 2f;
        this.maxLinearAcceleration = 50f;
        this.maxAngularSpeed = 30;
        this.maxAngularAcceleration = 3;
        this.boundingRadius = 1f;
    }

    public void update(float delta) {
        timer += delta;
        float f = timer / 4f;
        /*if(f <= 1) {
            Vector2 enemyPosition = body.getWorldCenter();
            path.valueAt(target, f); // this method sets the value of target

            Vector2 positionDelta = new Vector2(target).sub(enemyPosition);

            if(delta > 0.01f)
                body.setLinearVelocity(positionDelta.scl(1/delta));
        }
        else
            timer = 0;*/

        /*float angle = (float) Math.atan2(path.get(point).y - body.getWorldCenter().y, path.get(point).x - body.getWorldCenter().y);
        velocity.set((float) Math.cos(angle) * 5, (float) Math.sin(angle) * 5);

        body.setTransform(body.getPosition().add(velocity.scl(delta)), 0);

        point = (point++) % path.size;*/

        if(steeringBehavior != null) {
            steeringBehavior.calculateSteering(steeringOutput);
            applySteering(steeringOutput, delta);
        }
    }

    private void applySteering(SteeringAcceleration<Vector2> steeringOutput, float delta) {
        System.out.println("SteeringX: " + steeringOutput.linear.x + " SteeringY: " + steeringOutput.linear.y);

        boolean anyAccelerations = false;

        if(!steeringOutput.linear.isZero()) {
            body.setLinearVelocity(getLinearVelocity().mulAdd(steeringOutput.linear, delta).limit(getMaxLinearSpeed()));
            anyAccelerations = true;
        }

        if(anyAccelerations) {
            // Cap the linear speed
            Vector2 velocity = body.getLinearVelocity();
            float currentSpeedSquare = velocity.len2();
            float maxLinearSpeed = getMaxLinearSpeed();
            if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
                body.setLinearVelocity(velocity.scl(maxLinearSpeed / (float)Math.sqrt(currentSpeedSquare)));
            }

            // Cap the angular speed
            float maxAngVelocity = getMaxAngularSpeed();
            if (body.getAngularVelocity() > maxAngVelocity) {
                body.setAngularVelocity(maxAngVelocity);
            }
        }
//        System.out.println(body.getLinearVelocity());

//        getLinearVelocity().set(getLinearVelocity().mulAdd(steeringOutput.linear, delta));
//        body.setLinearVelocity(getLinearVelocity());


        /*if(!steeringOutput.linear.isZero()) {
            body.applyForceToCenter(steeringOutput.linear, true);
        }*/
    }


    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return 0;
    }

    @Override
    public float getBoundingRadius() {
        return boundingRadius;
    }

    @Override
    public boolean isTagged() {
        return false;
    }

    @Override
    public void setTagged(boolean tagged) {

    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return 0;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {

    }

    @Override
    public float getMaxLinearSpeed() {
        return maxLinearSpeed;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {
        this.maxLinearSpeed = maxLinearSpeed;
    }

    @Override
    public float getMaxLinearAcceleration() {
        return maxLinearAcceleration;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {
        this.maxLinearAcceleration = maxLinearAcceleration;
    }

    @Override
    public float getMaxAngularSpeed() {
        return maxAngularSpeed;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {
        this.maxAngularSpeed = maxAngularSpeed;
    }

    @Override
    public float getMaxAngularAcceleration() {
        return maxAngularAcceleration;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {
        this.maxAngularAcceleration = maxAngularAcceleration;
    }

    @Override
    public Vector2 getPosition() {
        return body.getPosition();
    }

    @Override
    public float getOrientation() {
        return body.getAngle();
    }

    @Override
    public void setOrientation(float orientation) {

    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return (float) Math.atan2(-vector.x, vector.y);
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        outVector.x = -(float)Math.sin(angle);
        outVector.y = (float)Math.cos(angle);
        return outVector;
    }

    @Override
    public Location<Vector2> newLocation() {
        return null;
    }

    public SteeringBehavior<Vector2> getSteeringBehavior() {
        return steeringBehavior;
    }

    public void setSteeringBehavior(SteeringBehavior<Vector2> steeringBehavior) {
        this.steeringBehavior = steeringBehavior;
    }
}


