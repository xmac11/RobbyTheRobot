package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.*;
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

            // robot - moving platform
            case ROBOT_CATEGORY | MOVING_PLATFORM_CATEGORY:
                robotMovPlatBegin(fixA, fixB);
                break;
        }



    }

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
        robot.setOnLadder(ladder, true);
        Gdx.app.log("ContactManager", "On ladder");
    }

    private static void robotMovPlatBegin(Fixture fixA, Fixture fixB) {
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

        movingPlatform.movePlatform(0, -7f);
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
            // robot - moving platform
            case ROBOT_CATEGORY | MOVING_PLATFORM_CATEGORY:
                robotMovPlatEnd(fixA, fixB);
                break;
        }


    }

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
        robot.setOnLadder(ladder, false);
    }

    private static void robotMovPlatEnd(Fixture fixA, Fixture fixB) {
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

//        movingPlatform.getBody().setLinearVelocity(0, 0);
        Gdx.app.log("ContactManager", "Off moving");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
