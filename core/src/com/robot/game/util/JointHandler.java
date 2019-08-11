package com.robot.game.util;

import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.interactiveObjects.MovingSpike;
import com.robot.game.screens.PlayScreen;

public class JointHandler {

    private PlayScreen playScreen;
    private ObjectParser objectParser;
    private Array<PrismaticJoint> joints;
    private Array<MovingSpike> movingSpikes;

    private ObjectMap<PrismaticJoint, MovingSpike> jointSpikeMap;
    private float jointStartTime;
    private float jointElapsed;

    public JointHandler(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.objectParser = playScreen.getObjectParser();
        this.joints = playScreen.getJoints();
        this.movingSpikes = playScreen.getMovingSpikes();
        this.jointSpikeMap = new ObjectMap<>();
        this.jointStartTime = TimeUtils.nanoTime();

        // pair joints with moving spikes by using the id's
        for(PrismaticJoint joint: joints) {
            for(MovingSpike movingSpike: movingSpikes) {

                if((int) joint.getUserData() == movingSpike.getId()) {

                    // set upper limit to pointA
                    joint.setLimits(0, movingSpike.getUpperTranslationA());

                    // add joint-spike pair to HashMap
                    jointSpikeMap.put(joint, movingSpike);
                }
            }
        }
    }

    public void update(float delta) {

        for(PrismaticJoint jointKey: jointSpikeMap.keys()) {

            MovingSpike movingSpike = jointSpikeMap.get(jointKey);
            movingSpike.setTimeElapsed(movingSpike.getTimeElapsed() + delta);

            //System.out.println(jointKey.getJointTranslation());

            // if spike is at pointA, set spike in balance position
            if(!movingSpike.isInBalancePosition() && Math.abs(jointKey.getJointTranslation() - movingSpike.getUpperTranslationA()) <= 0.1f) {
                movingSpike.setInBalancePosition(true);
            }
            // else if spike is at pointB, set balance position off, and stop attacking (it will start moving upwards)
            else if(movingSpike.isInBalancePosition() && Math.abs(jointKey.getJointTranslation() - movingSpike.getUpperTranslationB()) <= 0.1f) {
                movingSpike.setInBalancePosition(false);
                movingSpike.setAttacking(false);
            }

            //System.out.println(movingSpike.getTimeElapsed());

            // if spike is in balance position for more than 3 seconds and is not attacking, reset timer and start attacking
            if(movingSpike.isInBalancePosition() && movingSpike.getTimeElapsed() >= 3f && !movingSpike.isAttacking()) {
                jointKey.setLimits(0, movingSpike.getUpperTranslationB());
                movingSpike.setTimeElapsed(0);
                movingSpike.setAttacking(true);
            }
            // else if spike is not attacking (meaning it should be moving upwards) lerp upper limit towards pointA
            else if(!movingSpike.isAttacking()){
                jointKey.setLimits(0, Math.max(movingSpike.getUpperTranslationA(), jointKey.getUpperLimit() - 0.1f));
            }

        }
    }
}
