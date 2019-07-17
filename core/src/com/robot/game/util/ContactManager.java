package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.robot.game.interactiveObjects.FallingPlatform;
import com.robot.game.interactiveObjects.Ladder;
import com.robot.game.interactiveObjects.MovingPlatform;
import com.robot.game.sprites.Enemy;
import com.robot.game.sprites.Robot;

import static com.robot.game.util.Constants.*;

public class ContactManager implements ContactListener {

//    private Robot robot;
    private static int footContactCounter = 0;

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
            Gdx.app.log("ContactManager", "Foot contacts " + footContactCounter);
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
            robot.setFallingOffLadder(false);
            Gdx.input.setInputProcessor(new LadderClimbHandler(robot));
            robot.getBody().setGravityScale(0);
            Gdx.app.log("ContactManager", "On bottom ladder");
        }
        else {
            robot.setOnLadder(true);
            robot.setFallingOffLadder(false);
            Gdx.app.log("ContactManager", "On ladder");
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
        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            Gdx.app.log("ContactManager","Robot died from spikes");
        }
        else {
            robot = (Robot) fixB.getUserData();
            Gdx.app.log("ContactManager","Robot died from spikes");
        }
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
                setMaskBit(/*enemy.getBody()*/ fixB, NOTHING_MASK);
            }
            else {
                if (normal.y >= 1 / Math.sqrt(2))
                    Gdx.app.log("ContactManager", "Robot hit enemy from below");
                else if (normal.x <= -1 / Math.sqrt(2))
                    Gdx.app.log("ContactManager", "Robot hit enemy from the right");
                else if (normal.x >= 1 / Math.sqrt(2))
                    Gdx.app.log("ContactManager", "Robot hit enemy from the left");

                robot.getGameData().decreaseHealth(1);
                Gdx.app.log("ContactManager", "Robot health " + robot.getGameData().getHealth());
                robot.setFlicker(true);
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
                setMaskBit(fixA, NOTHING_MASK);
            }
            else {
                if(normal.y <= -1/Math.sqrt(2))
                    Gdx.app.log("ContactManager","Robot hit enemy from below");
                else if(normal.x >= 1/Math.sqrt(2))
                    Gdx.app.log("ContactManager","Robot hit enemy from the right");
                else if(normal.x <= -1/Math.sqrt(2))
                    Gdx.app.log("ContactManager","Robot hit enemy from the left");

                robot.getGameData().decreaseHealth(1);
                Gdx.app.log("ContactManager","Robot health " + robot.getGameData().getHealth());
                robot.setFlicker(true);
            }
        }
    }



    private void feetOnObject(Fixture fixA, Fixture fixB) {
        /*Robot robot;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();

        }
        else {
            robot = (Robot) fixB.getUserData();
        }*/
        Gdx.app.log("ContactManager","FEET on object");
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

            /*// feet
            case ROBOT_FEET_CATEGORY | GROUND_CATEGORY:
            case ROBOT_FEET_CATEGORY | FALLING_PLATFORM_CATEGORY:
            case ROBOT_FEET_CATEGORY | MOVING_PLATFORM_CATEGORY:
                feetOffObject(fixA, fixB);
                break;*/
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

    private void feetOffObject(Fixture fixA, Fixture fixB) {
        /*Robot robot;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();

        }
        else {
            robot = (Robot) fixB.getUserData();
        }*/
        Gdx.app.log("ContactManager","FEET off object");
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

    public void setMaskBit(/*Body body*/Fixture fixture, short maskBit) {
        Filter filter = new Filter();
        filter.maskBits = maskBit;
        /*for(Fixture fixture: body.getFixtureList())
            fixture.setFilterData(filter);*/
        fixture.setFilterData(filter);
    }

    public static int getFootContactCounter() {
        return footContactCounter;
    }

}
