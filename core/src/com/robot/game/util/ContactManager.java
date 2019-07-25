package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.robot.game.camera.ShakeEffect;
import com.robot.game.interactiveObjects.*;
import com.robot.game.sprites.Collectable;
import com.robot.game.sprites.Enemy;
import com.robot.game.sprites.Robot;

import static com.robot.game.util.Constants.*;

public class ContactManager implements ContactListener {

    private int footContactCounter = 0;

    @Override
    public void beginContact(Contact contact) {
        // Get the two fixtures that contact
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        Vector2 normal = contact.getWorldManifold().getNormal();

        if(fixA == null || fixB == null) return;
        if(fixA.getUserData() == null || fixB.getUserData() ==  null) return;

        if(fixA.getFilterData().categoryBits == ROBOT_FEET_CATEGORY || fixB.getFilterData().categoryBits == ROBOT_FEET_CATEGORY) {
            footContactCounter++;
            Gdx.app.log("ContactManager", "Foot contacts " + footContactCounter + " -> Feet in contact with " + fixA.getUserData() + " or " + fixB.getUserData());
        }

        int collisionID = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(collisionID) {

            // robot - ladder
            case ROBOT_CATEGORY | LADDER_CATEGORY:
                robotLadderBegin(fixA, fixB);
                break;

            // robot - falling platform
            case ROBOT_CATEGORY | FALLING_PLATFORM_CATEGORY:
                robotFallingPlatBegin(normal, fixA, fixB);
                break;

            // robot - moving platform
            case ROBOT_CATEGORY | MOVING_PLATFORM_CATEGORY:
                robotMovingPlatBegin(normal, fixA, fixB);
                break;

            // robot - spikes
            case ROBOT_CATEGORY | SPIKE_CATEGORY:
                robotSpikesBegin(fixA, fixB);
                break;

            // robot - enemy
            case ROBOT_CATEGORY | ENEMY_CATEGORY:
                robotEnemyBegin(normal, fixA, fixB);
                break;

            // robot - collectable
            case  ROBOT_CATEGORY | COLLECTABLE_CATEGORY:
            robotCollectableBegin(fixA, fixB);
            break;

            // robot - falling pipe
            case ROBOT_CATEGORY | PIPE_CATEGORY:
            robotPipeBegin(contact.getWorldManifold(), fixA, fixB);
            break;

            // ground - falling pipe
            case GROUND_CATEGORY | PIPE_CATEGORY:
            groundPipeBegin(fixA, fixB);
            break;
        }


    }

    // robot - ladder collision begins
    private void robotLadderBegin(Fixture fixA, Fixture fixB) {
        Robot robot;
        Ladder ladder;
        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            ladder = (Ladder) fixB.getUserData();
        }
        else {
            robot = (Robot) fixB.getUserData();
            ladder = (Ladder) fixA.getUserData();
        }

        // every time robot is hits the bottom of the ladder, turn off gravity and enable up-down keys
        // this mimics the case where the robot is on the ladder and falls down to the bottom
        if(ladder.getDescription().equals(LADDER_BOTTOM_DESCRIPTION)) {
            Gdx.app.log("ContactManager", "On bottom ladder");
            robot.setFallingOffLadder(false);
            Gdx.input.setInputProcessor(new LadderClimbHandler(robot));
            robot.getBody().setGravityScale(0);
        }
        else {
            Gdx.app.log("ContactManager", "On ladder");
            robot.setOnLadder(true);
            robot.setFallingOffLadder(false);
        }
    }

    // robot - falling platform collision begins
    private void robotFallingPlatBegin(Vector2 normal, Fixture fixA, Fixture fixB) {
        Robot robot;
        FallingPlatform fallingPlatform;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            fallingPlatform = (FallingPlatform) fixB.getUserData();

            if(normal.y <= -1/Math.sqrt(2)) {
                // move platform vertically
                fallingPlatform.setFlagToMove(true);
                robot.setOnInteractivePlatform(fallingPlatform, true);
                Gdx.app.log("ContactManager", "On falling platform");
            }
            else if(normal.y >= 1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from below");
            else if(normal.x <= -1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from the right");
            else if(normal.x >= 1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from the left");
        }
        else {
            robot = (Robot) fixB.getUserData();
            fallingPlatform = (FallingPlatform) fixA.getUserData();

            if(normal.y >= 1/Math.sqrt(2)) {
                fallingPlatform.setFlagToMove(true);
                robot.setOnInteractivePlatform(fallingPlatform, true);
                Gdx.app.log("ContactManager", "On falling platform");
            }
            else if(normal.y <= -1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from below");
            else if(normal.x >= 1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from the right");
            else if(normal.x <= -1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from the left");
        }

        // make robot stop on platform
//        robot.getBody().setLinearVelocity( robot.getBody().getLinearVelocity().x, fallingPlatform.getBody().getLinearVelocity().y );
    }

    // robot - moving platform collision begins
    private void robotMovingPlatBegin(Vector2 normal, Fixture fixA, Fixture fixB) {
        Robot robot;
        MovingPlatform movingPlatform;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            movingPlatform = (MovingPlatform) fixB.getUserData();
            if(normal.y <= -1/Math.sqrt(2)) {
                robot.setOnInteractivePlatform(movingPlatform, true);
                if(movingPlatform.isWaiting())
                    movingPlatform.movePlatform();

                Gdx.app.log("ContactManager", "On moving platform");
            }
            else if(normal.y >= 1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from below");
            else if(normal.x <= -1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from the right");
            else if(normal.x >= 1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from the left");

        }
        else {
            robot = (Robot) fixB.getUserData();
            movingPlatform = (MovingPlatform) fixA.getUserData();

            if(normal.y >= 1/Math.sqrt(2)) {
                robot.setOnInteractivePlatform(movingPlatform, true);
                if(movingPlatform.isWaiting())
                    movingPlatform.movePlatform();

                Gdx.app.log("ContactManager", "On moving platform");
            }
            else if(normal.y <= -1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from below");
            else if(normal.x >= 1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from the right");
            else if(normal.x <= -1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from the left");
        }
    }

    private void robotSpikesBegin(Fixture fixA, Fixture fixB) {
        Robot robot;
        Spike spike;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            spike = (Spike) fixB.getUserData();
            Gdx.app.log("ContactManager","Robot hurt from spikes");
        }
        else {
            robot = (Robot) fixB.getUserData();
            spike = (Spike) fixA.getUserData();
            Gdx.app.log("ContactManager","Robot hurt from spikes");
        }

        // if robot is not invulnerable and is not walking on spikes
        if(!robot.isInvulnerable() && !spike.mightBeWalked()) {
            // decrease health
            robot.getCheckpointData().decreaseHealth(DAMAGE_FROM_SPIKE);
            // make it invulnerable
            robot.setInvulnerable(1f);
            Gdx.app.log("ContactManager", "Robot health " + robot.getCheckpointData().getHealth() + "%");
        }

        // if robot is walking on spikes, it dies
        if(spike.mightBeWalked()) {
            robot.setFlicker(true);
            robot.setWalkingOnSpikes(true);
//            robot.getCheckpointData().setSpawnLocation(spike.getRespawnLocation());
        }

        // make it flicker
        robot.setFlicker(true);
        //shake camera
        ShakeEffect.shake(HIT_SHAKE_INTENSITY, HIT_SHAKE_TIME);
    }

    /* When the robot steps on an enemy, I have to set the enemy’s mask bit to “NOTHING”. As a result, the collision stops at that point, the player
     * bounces off the enemy as normal and the presolve() method does not get called.
     * If the mask bit was not set to “NOTHING”, the collision would continue being detected. Then the presolve() method would be called disabling the
     * collision, so the robot would pass through the enemy and the bounce off the enemy would never take place. */
    private void robotEnemyBegin(Vector2 normal, Fixture fixA, Fixture fixB) {
        Robot robot;
        Enemy enemy;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            enemy = (Enemy) fixB.getUserData();

            if(normal.y <= -1/Math.sqrt(2)) {
                Gdx.app.log("ContactManager", "Robot stepped on enemy");
                enemy.setFlagToKill();

                // if following a path, disable it
                if(enemy.isAiPathFollowing()) {
                    enemy.getFollowPath().setEnabled(false);
                }

                // stop enemy
                enemy.getBody().setLinearVelocity(0, 0);

                // set enemy's mask bits to "nothing"
                StaticMethods.setMaskBit(fixB, NOTHING_MASK);

                // increase points
                StaticMethods.increaseScore(robot, enemy);

                // add enemy to the HashMap in order to render the points gained
                robot.getScreenLevel1().getPointsRenderer().getEnemyPointsToDraw().put(enemy, 1f);
            }
            else {
                if (normal.y >= 1 / Math.sqrt(2))
                    Gdx.app.log("ContactManager", "Robot hit enemy from below");
                else if (normal.x <= -1 / Math.sqrt(2))
                    Gdx.app.log("ContactManager", "Robot hit enemy from the right");
                else if (normal.x >= 1 / Math.sqrt(2))
                    Gdx.app.log("ContactManager", "Robot hit enemy from the left");

                // decrease robot's health
                StaticMethods.decreaseHealth(robot, enemy);
                // make it flicker
                robot.setFlicker(true);
                // shake camera
                ShakeEffect.shake(HIT_SHAKE_INTENSITY, HIT_SHAKE_TIME);
                Gdx.app.log("ContactManager", "Robot health " + robot.getCheckpointData().getHealth() + "%");
            }
        }
        else {
            robot = (Robot) fixB.getUserData();
            enemy = (Enemy) fixA.getUserData();

            if(normal.y >= 1/Math.sqrt(2)) {
                Gdx.app.log("ContactManager", "Robot stepped on enemy");
                enemy.setFlagToKill();

                // if following a path, disable it
                if(enemy.isAiPathFollowing()) {
                    enemy.getFollowPath().setEnabled(false);
                }

                // stop enemy
                enemy.getBody().setLinearVelocity(0, 0);

                // set enemy's mask bits to "nothing"
                StaticMethods.setMaskBit(fixA, NOTHING_MASK);

                // increase points
                StaticMethods.increaseScore(robot, enemy);

                // add enemy to the HashMap in order to render the points gained
                robot.getScreenLevel1().getPointsRenderer().getEnemyPointsToDraw().put(enemy, 1f);
            }
            else {
                if(normal.y <= -1/Math.sqrt(2))
                    Gdx.app.log("ContactManager","Robot hit enemy from below");
                else if(normal.x >= 1/Math.sqrt(2))
                    Gdx.app.log("ContactManager","Robot hit enemy from the right");
                else if(normal.x <= -1/Math.sqrt(2))
                    Gdx.app.log("ContactManager","Robot hit enemy from the left");

                // decrease robot's health
                StaticMethods.decreaseHealth(robot, enemy);
                // make it flicker
                robot.setFlicker(true);
                // shake camera
                ShakeEffect.shake(HIT_SHAKE_INTENSITY, HIT_SHAKE_TIME);
                Gdx.app.log("ContactManager","Robot health " + robot.getCheckpointData().getHealth() + "%");
            }
        }
    }

    private void robotCollectableBegin(Fixture fixA, Fixture fixB) {
        Robot robot;
        Collectable collectable;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            collectable = (Collectable) fixB.getUserData();
        }
        else {
            robot = (Robot) fixB.getUserData();
            collectable = (Collectable) fixA.getUserData();
        }
        // increase score for collectable
        robot.getCheckpointData().increaseScore(POINTS_FOR_COLLECTABLE);

        // add collectable to the HashMap in order to render the points gained
        robot.getScreenLevel1().getPointsRenderer().getItemPointsToDraw().put(collectable , 1f);

        // flag to collect (in order to destroy the body)
        collectable.setFlagToCollect();

        // add the collectable to the list of collectables to be disabled from beying respawned if robot dies
        collectable.addToDisableSpawning((int) collectable.getObject().getProperties().get("id"));
        // If I use a thread instead of queueing items to disable spawning, remove for-loop in the checkIfDead() method in Playscreen
//        new Thread(() -> collectable.getCollectableHandler().setSpawn((int) collectable.getObject().getProperties().get("id"), false)).start();

        // flag that a new item was collected
        robot.getScreenLevel1().setNewItemCollected(true);
        Gdx.app.log("ContactManager","Robot collected item");
    }

    private void robotPipeBegin(WorldManifold manifold, Fixture fixA, Fixture fixB) {
        Robot robot;
        FallingPipe pipe;
        Vector2 normal = manifold.getNormal();
        Vector2[] points = manifold.getPoints();

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            pipe = (FallingPipe) fixB.getUserData();
        }
        else {
            robot = (Robot) fixB.getUserData();
            pipe = (FallingPipe) fixA.getUserData();
        }

        // if robot is not invulnerable
        if(!robot.isInvulnerable()) {
            // decrease health
            robot.getCheckpointData().decreaseHealth(DAMAGE_FROM_PIPE);
            // make it invulnerable for 1 second
            robot.setInvulnerable(1f);
            // make it flicker
            robot.setFlicker(true);
            // shake the camera
            ShakeEffect.shake(HIT_SHAKE_INTENSITY, HIT_SHAKE_TIME);
        }
    }

    private void groundPipeBegin(Fixture fixA, Fixture fixB) {
        FallingPipe fallingPipe;

        if(fixA.getUserData() instanceof FallingPipe) {
            fallingPipe = (FallingPipe) fixA.getUserData();
            //StaticMethods.setMaskBit(fixA, NOTHING_MASK);

            // change category bits
            StaticMethods.setCategoryBit(fixA, PIPE_ON_GROUND_CATEGORY);
        }
        else {
            fallingPipe = (FallingPipe) fixB.getUserData();
            //StaticMethods.setMaskBit(fixB, NOTHING_MASK);

            // change category bits
            StaticMethods.setCategoryBit(fixB, PIPE_ON_GROUND_CATEGORY);
        }
        // set flag for pipes that are on the floor to sleep
        fallingPipe.setFlagToSleep(true);
    }

    @Override
    public void endContact(Contact contact) {
        // Get the two fixtures that contact
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(fixA == null || fixB == null) return;
        if(fixA.getUserData() == null || fixB.getUserData() ==  null) return;

        if(fixA.getFilterData().categoryBits == ROBOT_FEET_CATEGORY || fixB.getFilterData().categoryBits == ROBOT_FEET_CATEGORY) {
            footContactCounter--;
            Gdx.app.log("ContactManager", "Foot contacts " + footContactCounter);
        }

        int collisionID = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(collisionID) {

            // robot - ladder
            case ROBOT_CATEGORY | LADDER_CATEGORY:
                robotLadderEnd(fixA, fixB);
                break;
            // robot - falling platform
            case ROBOT_CATEGORY | FALLING_PLATFORM_CATEGORY:
                robotFallingPlatEnd(fixA, fixB); // this does nothing right now
                break;
            // robot - moving platform
            case ROBOT_CATEGORY | MOVING_PLATFORM_CATEGORY:
                robotMovingPlatEnd(fixA, fixB);
                break;

            // robot - spikes
            case ROBOT_CATEGORY | SPIKE_CATEGORY:
                robotSpikesEnd(fixA, fixB);
                break;

        }


    }

    // robot - ladder collision ends
    private void robotLadderEnd(Fixture fixA, Fixture fixB) {
        Robot robot;
        Ladder ladder;
        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            ladder = (Ladder) fixB.getUserData();
        }
        else {
            robot = (Robot) fixB.getUserData();
            ladder = (Ladder) fixA.getUserData();
        }
        // remove robot from ladder only if it lets go of the core
        if(ladder.getDescription().equals(LADDER_CORE_DESCRIPTION)) {
            Gdx.app.log("ContactManager", "Off ladder");
            robot.setOnLadder(false);
        }
    }

    // robot - falling platform collision ends
    private void robotFallingPlatEnd(Fixture fixA, Fixture fixB) {
        Robot robot;
        FallingPlatform fallingPlatform;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            fallingPlatform = (FallingPlatform) fixB.getUserData();
        }
        else {
            robot = (Robot) fixB.getUserData();
            fallingPlatform = (FallingPlatform) fixA.getUserData();
        }
        robot.setOnInteractivePlatform(fallingPlatform, false);
        Gdx.app.log("ContactManager", "Off falling platform");
    }

    // robot - moving platform collision ends
    private void robotMovingPlatEnd(Fixture fixA, Fixture fixB) {
        Robot robot;
        MovingPlatform movingPlatform;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            movingPlatform = (MovingPlatform) fixB.getUserData();
        }
        else {
            robot = (Robot) fixB.getUserData();
            movingPlatform = (MovingPlatform) fixA.getUserData();
        }

        // remove the robot from the moving platform
        robot.setOnInteractivePlatform(null, false);
        Gdx.app.log("ContactManager", "Off moving platform");
    }

    private void robotSpikesEnd(Fixture fixA, Fixture fixB) {
        Robot robot;
        Spike spike;
        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            spike = (Spike) fixB.getUserData();
            Gdx.app.log("ContactManager","Spike collision ended");
        }
        else {
            robot = (Robot) fixB.getUserData();
            spike = (Spike) fixA.getUserData();
            Gdx.app.log("ContactManager","Spike collision ended");
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

        // Get the two fixtures that contact
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int collisionID = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(collisionID) {
            // robot - enemy
            case ROBOT_CATEGORY | ENEMY_CATEGORY:
                contact.setEnabled(false);
//                Gdx.app.log("ContactManager","Contact was disabled");
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public int getFootContactCounter() {
        return footContactCounter;
    }





}
