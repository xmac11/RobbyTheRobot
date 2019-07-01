package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
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
            case ROBOT_CATEGORY | LADDER_CATEGORY:
                Robot robot;
                if(fixA.getUserData() instanceof Robot)
                    robot = (Robot) fixA.getUserData();
                else
                    robot = (Robot) fixB.getUserData();
                Gdx.app.log("ContactManager", "On ladder");
                robot.setOnLadder(true);
                break;

        }



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
            case ROBOT_CATEGORY | LADDER_CATEGORY:
                Robot robot;
                if(fixA.getUserData() instanceof Robot)
                    robot = (Robot) fixA.getUserData();
                else
                    robot = (Robot) fixB.getUserData();
                Gdx.app.log("ContactManager", "Off ladder");
                robot.setOnLadder(false);
                break;
        }


        }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

}
