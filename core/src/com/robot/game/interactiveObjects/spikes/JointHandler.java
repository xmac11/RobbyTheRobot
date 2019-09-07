package com.robot.game.interactiveObjects.spikes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.robot.game.entities.Robot;
import com.robot.game.screens.playscreens.PlayScreen;
import com.robot.game.util.staticMethods.StaticMethods;

import static com.robot.game.util.constants.Constants.*;

public class JointHandler {

    private Robot robot;
    private ObjectMap<PrismaticJoint, MovingSpike> jointSpikeMap;
    private boolean handlerActivated;
    private boolean handlerDisabled;

    public JointHandler(PlayScreen playScreen) {
        this.robot = playScreen.getRobot();
        Array<PrismaticJoint> joints = playScreen.getJoints();
        Array<MovingSpike> movingSpikes = playScreen.getMovingSpikes();
        this.jointSpikeMap = new ObjectMap<>();

        // pair joints with moving spikes by using the id's
        for(PrismaticJoint joint: joints) {
            for(MovingSpike movingSpike: movingSpikes) {

                if((int) joint.getUserData() == movingSpike.getId()) {

                    // set upper limit of joint to pointA
                    joint.setLimits(0, movingSpike.getUpperTranslationA());

                    // position of the static body to draw the base
                    positionStaticBase(joint, movingSpike);

                    // add joint-spike pair to HashMap
                    jointSpikeMap.put(joint, movingSpike);
                }
            }
        }
    }

    public void update(float delta) {

        // first check if joints should be activated / disabled
        if(!handlerActivated && !handlerDisabled) {
            checkForJointActivation();
        }
        else if(handlerActivated) {
            checkForJointDisabling();
        }

        // if it is activated, handle their attacking
        if(handlerActivated) {
            handleJoints(delta);
        }
    }

    private void checkForJointActivation() {
        if(Math.abs(robot.getBody().getPosition().x * PPM - TANKBALL_ACTIVATION_AREA.x) <= CHECKPOINT_TOLERANCE &&
                Math.abs(robot.getBody().getPosition().y * PPM - TANKBALL_ACTIVATION_AREA.y) <= CHECKPOINT_TOLERANCE) {

            handlerActivated = true;
            Gdx.app.log("JointHandler", "Joints were activated");
        }
    }

    private void checkForJointDisabling() {
        if(Math.abs(robot.getBody().getPosition().x * PPM - TANKBALL_DISABLING_AREA.x) <= CHECKPOINT_TOLERANCE &&
                Math.abs(robot.getBody().getPosition().y * PPM - TANKBALL_DISABLING_AREA.y) <= CHECKPOINT_TOLERANCE) {

            handlerActivated = false;
            handlerDisabled = true;
            Gdx.app.log("JointHandler", "Joints were disabled");
        }
    }

    private void handleJoints(float delta) {
        for(PrismaticJoint jointKey: jointSpikeMap.keys()) {

            MovingSpike movingSpike = jointSpikeMap.get(jointKey);
            // update time
            movingSpike.setTimeElapsed(movingSpike.getTimeElapsed() + delta);

            // if spike is at pointA, set spike in balance position
            if(!movingSpike.isInBalancePosition() && Math.abs(jointKey.getJointTranslation() - movingSpike.getUpperTranslationA()) <= 0.1f) {
                movingSpike.setInBalancePosition(true);
            }
            // else if spike is at pointB, set balance position off, and stop attacking (it will start moving upwards)
            else if(movingSpike.isInBalancePosition() && Math.abs(jointKey.getJointTranslation() - movingSpike.getUpperTranslationB()) <= 0.1f) {
                movingSpike.setInBalancePosition(false);
                movingSpike.setAttacking(false);
            }

            // if spike is in balance position for more than 'n' seconds and is not attacking, reset timer and start attacking
            if(movingSpike.isInBalancePosition() && movingSpike.shouldAttack() && !movingSpike.isAttacking()) {
                jointKey.setLimits(0, movingSpike.getUpperTranslationB());
                movingSpike.setTimeElapsed(0);
                movingSpike.setAttacking(true);
            }
            // else if spike is not attacking (meaning it should be moving balance position), lerp upper limit towards pointA
            else if(!movingSpike.isAttacking()){
                jointKey.setLimits(0, Math.max(movingSpike.getUpperTranslationA(), jointKey.getUpperLimit() - 0.1f));
            }
        }
    }

    public void draw(Batch batch) {

        for(PrismaticJoint jointKey: jointSpikeMap.keys()) {

            MovingSpike movingSpike = jointSpikeMap.get(jointKey);

            // horizontal
            if(movingSpike.isHorizontal()) {
                float x1 = jointKey.getBodyA().getPosition().x;
                float x2 = jointKey.getBodyB().getPosition().x;
                float width = 16 / PPM;
                float height = Math.abs(x2 - x1);

                movingSpike.getStickSpirte().setSize(width, height);
                movingSpike.getStickSpirte().setOrigin(width / 2, height / 2);

                movingSpike.getStickSpirte().setRotation(90);
                movingSpike.getStickSpirte().setPosition((x1 + x2) / 2 - width / 2, jointKey.getBodyA().getPosition().y - height / 2);
            }
            // vertical
            else {
                float y1 = jointKey.getBodyA().getPosition().y;
                float y2 = jointKey.getBodyB().getPosition().y;
                float width = 16 / PPM;
                float height = Math.abs(y1 - y2);

                movingSpike.getStickSpirte().setSize(width, height);

                movingSpike.getStickSpirte().setPosition(jointKey.getBodyA().getPosition().x - width / 2 , (y1 + y2) / 2 - height / 2);
            }
            // render the joint (stick)
            movingSpike.getStickSpirte().draw(batch);
        }
    }

    private void positionStaticBase(PrismaticJoint joint, MovingSpike movingSpike) {
        Vector2 basePosition = StaticMethods.getStaticBodyOfJoint(joint).getPosition();
        movingSpike.getBaseSpirte().setPosition(basePosition.x - 32f / 2 / PPM, basePosition.y - 8f / 2 / PPM);
        if(movingSpike.isHorizontal()) {
            movingSpike.getBaseSpirte().setRotation(90);
        }
    }

    public void setToNull() {
        robot = null;
        jointSpikeMap = null;
        Gdx.app.log("JointHandler", "Objects were set to null");
    }
}
