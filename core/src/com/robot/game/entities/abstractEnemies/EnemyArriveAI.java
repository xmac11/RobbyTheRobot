package com.robot.game.entities.abstractEnemies;

import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.steer.SteeringAcceleration;
import com.badlogic.gdx.ai.steer.SteeringBehavior;
import com.badlogic.gdx.ai.steer.behaviors.Arrive;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.entities.Robot;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Enums;

import static com.robot.game.util.Enums.Facing;

public abstract class EnemyArriveAI extends Enemy implements Steerable<Vector2> {

    // EnemyAI
    protected SteeringBehavior<Vector2> steeringBehavior;
    protected SteeringAcceleration<Vector2> steeringOutput;
    protected Arrive<Vector2> arrive;
    protected Robot robot;

    protected float maxLinearSpeed, maxLinearAcceleration;
    protected float maxAngularSpeed, maxAngularAcceleration;
    protected float boundingRadius;

    protected Facing facing;

    public EnemyArriveAI(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);
        this.robot = playScreen.getRobot();

        this.arrive = new Arrive<>(this, robot).setTimeToTarget(0.1f).setArrivalTolerance(0.001f).setDecelerationRadius(1f);
        arrive.setEnabled(false);

        this.steeringBehavior = arrive;
        this.steeringOutput = new SteeringAcceleration<>(new Vector2());

        this.maxLinearSpeed = /*(float) object.getProperties().get("aiSpeed")*/ 2f;
        this.maxLinearAcceleration = 500f;
        this.maxAngularSpeed = 3;
        this.maxAngularAcceleration = 3;
        this.boundingRadius = 2f;
    }

    protected void applySteering(float delta) {
        //        System.out.println("SteeringX: " + steeringOutput.linear.x + " SteeringY: " + steeringOutput.linear.y);

        if(!steeringOutput.linear.isZero()) {
            body.setLinearVelocity(MathUtils.clamp(getLinearVelocity().x + steeringOutput.linear.x * delta, -maxLinearSpeed, maxLinearSpeed),
                                   getLinearVelocity().y);
//            body.applyForceToCenter(steeringOutput.linear, true);
        }

        //        System.out.println(body.getLinearVelocity());
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

    public Arrive<Vector2> getArrive() {
        return arrive;
    }

    public Facing getFacing() {
        return facing;
    }
}
