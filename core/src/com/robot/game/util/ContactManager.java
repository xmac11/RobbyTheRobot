package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.robot.game.interactiveObjects.FallingPlatform;
import com.robot.game.interactiveObjects.Ladder;
import com.robot.game.interactiveObjects.MovingPlatform;
import com.robot.game.sprites.Robot;

import static com.robot.game.util.Constants.*;

public class ContactManager implements ContactListener {

//    private Robot robot;
    public static int footContactCounter = 0;

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
            System.out.println("Foot contacts " + footContactCounter);
        }

        int collisionID = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(collisionID) {

            // robot - ladder
            case ROBOT_CATEGORY | LADDER_CATEGORY:
                robotLadderBegin(fixA, fixB);
                break;

            // robot - falling platform
            case ROBOT_CATEGORY | FALLING_PLATFORM_CATEGORY:
                robotFallPlatBegin(normal, fixA, fixB);
                break;

            // robot - moving platform
            case ROBOT_CATEGORY | MOVING_PLATFORM_CATEGORY:
                robotMovingPlatBegin(normal, fixA, fixB);
                break;

            // feet
            /*case ROBOT_FEET_CATEGORY | GROUND_CATEGORY:
            case ROBOT_FEET_CATEGORY | FALLING_PLATFORM_CATEGORY:
            case ROBOT_FEET_CATEGORY | MOVING_PLATFORM_CATEGORY:
                feetOnObject(fixA, fixB);
                break;*/
        }


    }

    // robot - ladder collision begins
    private void robotLadderBegin(Fixture fixA, Fixture fixB) {
        Texture texture = new Texture("blue.png");
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
            robot.setRobotSprite(new Sprite(texture));
            robot.setOnLadder(true);
            robot.setFallingOffLadder(false);
            Gdx.app.log("ContactManager", "On ladder");
        }
    }

    // robot - falling platform collision begins
    private void robotFallPlatBegin(Vector2 normal, Fixture fixA, Fixture fixB) {
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
            System.out.println("Foot contacts " + footContactCounter);
        }

        int collisionID = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(collisionID) {

            // robot - ladder
            case ROBOT_CATEGORY | LADDER_CATEGORY:
                robotLadderEnd(fixA, fixB);
                break;
            // robot - falling platform
            case ROBOT_CATEGORY | FALLING_PLATFORM_CATEGORY:
                robotFallPlatEnd(fixA, fixB); // this does nothing right now
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
        Texture texture = new Texture("sf.png");
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
            robot.setRobotSprite(new Sprite(texture));
            robot.setOnLadder(false);
        }
    }

    // this does nothing right now
    // robot - falling platform collision ends
    private void robotFallPlatEnd(Fixture fixA, Fixture fixB) {
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

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
