package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
import com.robot.game.interactiveObjects.FallingPlatform;
import com.robot.game.interactiveObjects.Ladder;
import com.robot.game.interactiveObjects.MovingPlatform;
import com.robot.game.sprites.Robot;

import static com.robot.game.util.Constants.*;

public class ContactManager implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        // Get the two fixtures that contact
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(fixA == null || fixB == null) return;
        if(fixA.getUserData() == null || fixB.getUserData() ==  null) return;

        int collisionID = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(collisionID) {

            // robot - ladder
            case ROBOT_CATEGORY | LADDER_CATEGORY:
                robotLadderBegin(fixA, fixB);
                break;

            // robot - falling platform
            case ROBOT_CATEGORY | FALLING_PLATFORM_CATEGORY:
                robotFallPlatBegin(fixA, fixB);
                break;

            // robot - moving platform
            case ROBOT_CATEGORY | MOVING_PLATFORM_CATEGORY:
                robotMovingPlatBegin(fixA, fixB);
                break;
        }


    }

    // robot - ladder collision begins
    private static void robotLadderBegin(Fixture fixA, Fixture fixB) {
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

        robot.setRobotSprite(new Sprite(texture));
        robot.setOnLadder(true);
        Gdx.app.log("ContactManager", "On ladder");
    }

    // robot - falling platform collision begins
    private static void robotFallPlatBegin(Fixture fixA, Fixture fixB) {
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

        // move platform vertically
        fallingPlatform.setFlagToMove(true);


        // make robot stop on platform
        robot.getBody().setLinearVelocity( robot.getBody().getLinearVelocity().x, fallingPlatform.getBody().getLinearVelocity().y );
        Gdx.app.log("ContactManager", "On falling");
    }

    // robot - moving platform collision begins
    private static void robotMovingPlatBegin(Fixture fixA, Fixture fixB) {
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

        // this is used for constantly moving platforms
        robot.setOnMovingPlatform(movingPlatform, true);
        Gdx.app.log("ContactManager", "On moving");
    }

    @Override
    public void endContact(Contact contact) {
        // Get the two fixtures that contact
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        if(fixA == null || fixB == null) return;
        if(fixA.getUserData() == null || fixB.getUserData() ==  null) return;

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
        }


    }

    // robot - ladder collision ends
    private static void robotLadderEnd(Fixture fixA, Fixture fixB) {
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
        Gdx.app.log("ContactManager", "Off ladder");
        robot.setRobotSprite(new Sprite(texture));
        robot.setOnLadder(false);
    }

    // this does nothing right now
    // robot - falling platform collision ends
    private static void robotFallPlatEnd(Fixture fixA, Fixture fixB) {
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

        // this is used for constantly moving platforms
        //robot.setOnMovingPlatform(null, false);
        Gdx.app.log("ContactManager", "Off falling");
    }

    // robot - moving platform collision ends
    private static void robotMovingPlatEnd(Fixture fixA, Fixture fixB) {
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

        robot.setOnMovingPlatform(null, false);
        Gdx.app.log("ContactManager", "Off moving");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
