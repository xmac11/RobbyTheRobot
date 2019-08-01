package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.interactiveObjects.*;
import com.robot.game.interactiveObjects.collectables.Collectable;
import com.robot.game.interactiveObjects.ladder.Ladder;
import com.robot.game.interactiveObjects.platforms.FallingPlatform;
import com.robot.game.interactiveObjects.platforms.InteractivePlatform;
import com.robot.game.interactiveObjects.platforms.MovingPlatform;
import com.robot.game.entities.Enemy;
import com.robot.game.interactiveObjects.collectables.PowerUp;
import com.robot.game.entities.Robot;

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
            case ROBOT_CATEGORY | INTERACTIVE_PLATFORM_CATEGORY:
                robotInteractivePlatBegin(normal, fixA, fixB);
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
            robotPipeBegin(fixA, fixB);
            break;

            // ground - falling pipe
            case GROUND_CATEGORY | PIPE_CATEGORY:
            groundPipeBegin(fixA, fixB);
            break;

            // feet - pipe on ground
            case ROBOT_FEET_CATEGORY | PIPE_ON_GROUND_CATEGORY:
            feetPipeOnGroundBegin(fixA, fixB);
            break;

            // robot - wall jumping
            case ROBOT_CATEGORY | WALLJUMP_CATEGORY:
            robotWallBegin(normal, fixA, fixB);
            break;

            // robot - trampoline
            case ROBOT_CATEGORY | TRAMPOLINE_CATEGORY:
            robotTrampolineBegin(normal, fixA, fixB);
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

        // every time robot hits the bottom of the ladder, turn off gravity and enable up-down keys
        // this mimics the case where the robot is on the ladder and falls down to the bottom
        if(ladder.getDescription().equals(LADDER_BOTTOM_DESCRIPTION)) {
            Gdx.app.log("ContactManager", "On bottom ladder");
            robot.setFallingOffLadder(false);
            Gdx.input.setInputProcessor(robot.getPlayScreen().getLadderClimbHandler());
            robot.getBody().setGravityScale(0);
        }
        else {
            Gdx.app.log("ContactManager", "On ladder");
            robot.setOnLadder(true);
            robot.setFallingOffLadder(false);
        }
    }

    // robot - interactive platform collision begins
    private void robotInteractivePlatBegin(Vector2 normal, Fixture fixA, Fixture fixB) {
        Robot robot;
        InteractivePlatform interactivePlatform;
        boolean onPlatform = false;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            interactivePlatform = (InteractivePlatform) fixB.getUserData();

            if(normal.y <= -1/Math.sqrt(2)) {
                Gdx.app.log("ContactManager", "On interactive platform");
                onPlatform = true;
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
            interactivePlatform = (InteractivePlatform) fixA.getUserData();

            if(normal.y >= 1/Math.sqrt(2)) {
                Gdx.app.log("ContactManager", "On interactive platform");
                onPlatform = true;
            }
            else if(normal.y <= -1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from below");
            else if(normal.x >= 1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from the right");
            else if(normal.x <= -1/Math.sqrt(2))
                Gdx.app.log("ContactManager","Robot hit platform from the left");
        }

        // if onPlatform flag was set
        if(onPlatform) {
            if(interactivePlatform instanceof FallingPlatform)
                robotFallingPlatBegin(robot, (FallingPlatform) interactivePlatform);

            else if(interactivePlatform instanceof MovingPlatform)
                robotMovingPlatBegin(robot, (MovingPlatform) interactivePlatform);
        }
    }

    // robot - falling platform collision begins
    private void robotFallingPlatBegin(Robot robot, FallingPlatform fallingPlatform) {
        fallingPlatform.setFlagToMove(true);
        robot.setOnInteractivePlatform(fallingPlatform, true);
    }

    // robot - moving platform collision begins
    private void robotMovingPlatBegin(Robot robot, MovingPlatform movingPlatform) {

        robot.setOnInteractivePlatform(movingPlatform, true);

        // if platform is waiting and has not been already activated, move it
        if(movingPlatform.isWaiting() && !movingPlatform.isActivated())
            movingPlatform.movePlatform();
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
            //add spike to HashMap in order to render the damage incurred
            robot.getPlayScreen().getFeedbackRenderer().getDamageFromHitToDraw().put(spike, 1f);
            // make it invulnerable
            robot.setInvulnerable(1f);
            Gdx.app.log("ContactManager", "Robot health " + robot.getCheckpointData().getHealth() + "%");
        }

        // if robot is walking on spikes, it dies (but does not lose health)
        else if(spike.mightBeWalked()) {
            robot.setFlicker(true);
            robot.setWalkingOnSpikes(true);
//            robot.getCheckpointData().setSpawnLocation(spike.getRespawnLocation());
        }

        // make it flicker
        robot.setFlicker(true);
        //shake camera
        robot.getShakeEffect().shake(HIT_SHAKE_INTENSITY, HIT_SHAKE_TIME);
    }

    /* When the robot steps on an enemy, I have to set the enemy’s mask bit to “NOTHING”. As a result, the collision stops at that point, the player
     * bounces off the enemy as normal and the presolve() method does not get called.
     * If the mask bit was not set to “NOTHING”, the collision would continue being detected. Then the presolve() method would be called disabling the
     * collision, so the robot would pass through the enemy and the bounce off the enemy would never take place. */
    private void robotEnemyBegin(Vector2 normal, Fixture fixA, Fixture fixB) {
        Robot robot;
        Enemy enemy;
        boolean steppedOnEnemy = false;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            enemy = (Enemy) fixB.getUserData();

            if(normal.y <= -1/Math.sqrt(2)) {
                Gdx.app.log("ContactManager", "Robot stepped on enemy");
                steppedOnEnemy = true;
            }
            else {
                if (normal.y >= 1 / Math.sqrt(2))
                    Gdx.app.log("ContactManager", "Robot hit enemy from below");
                else if (normal.x <= -1 / Math.sqrt(2))
                    Gdx.app.log("ContactManager", "Robot hit enemy from the right");
                else if (normal.x >= 1 / Math.sqrt(2))
                    Gdx.app.log("ContactManager", "Robot hit enemy from the left");
            }
        }
        else {
            robot = (Robot) fixB.getUserData();
            enemy = (Enemy) fixA.getUserData();

            if(normal.y >= 1/Math.sqrt(2)) {
                Gdx.app.log("ContactManager", "Robot stepped on enemy");
                steppedOnEnemy = true;
            }
            else {
                if(normal.y <= -1/Math.sqrt(2))
                    Gdx.app.log("ContactManager","Robot hit enemy from below");
                else if(normal.x >= 1/Math.sqrt(2))
                    Gdx.app.log("ContactManager","Robot hit enemy from the right");
                else if(normal.x <= -1/Math.sqrt(2))
                    Gdx.app.log("ContactManager","Robot hit enemy from the left");
            }
        }

        // if steppedOnEnemy flag was set
        if(steppedOnEnemy) {
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
            robot.getPlayScreen().getFeedbackRenderer().getPointsForEnemyToDraw().put(enemy, 1f);
        }
        // otherwise it means that the robot was hit by an enemy
        else {
            // decrease robot's health
            StaticMethods.decreaseHealth(robot, enemy);

            //add enemy to HashMap in order to render the damage incurred
            robot.getPlayScreen().getFeedbackRenderer().getDamageFromHitToDraw().put(enemy, 1f);

            // make it flicker
            robot.setFlicker(true);

            // shake camera
            robot.getShakeEffect().shake(HIT_SHAKE_INTENSITY, HIT_SHAKE_TIME);
            Gdx.app.log("ContactManager", "Robot health " + robot.getCheckpointData().getHealth() + "%");
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
        StaticMethods.increaseScore(robot, collectable);

        // add collectable to the HashMap in order to render the points gained or the powerup animation
        StaticMethods.queueForPointsRenderer(robot, collectable);

        if(collectable instanceof PowerUp) {
            StaticMethods.increaseHealth(robot, (PowerUp) collectable);
            Gdx.app.log("ContactManger", "Robot collected powerup, Health " + robot.getCheckpointData().getHealth() + "%");
        }

        // flagToCancelVelocity to collect (in order to destroy the body)
        collectable.setFlagToCollect();

        // add the collectable to the list of collectables to be disabled from beying respawned if robot dies
        collectable.addToDisableSpawning((int) collectable.getObject().getProperties().get("id"));

        // flagToCancelVelocity that a new item was collected
        robot.getPlayScreen().setNewItemCollected(true);
        Gdx.app.log("ContactManager","Robot collected item");
    }

    private void robotPipeBegin(Fixture fixA, Fixture fixB) {
        Robot robot;
        FallingPipe pipe;

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

            //add pipe to HashMap in order to render the damage incurred
            robot.getPlayScreen().getFeedbackRenderer().getDamageFromHitToDraw().put(pipe, 1f);

            // make it invulnerable for 1 second
            robot.setInvulnerable(1f);
            // make it flicker
            robot.setFlicker(true);
            // shake the camera
            robot.getShakeEffect().shake(HIT_SHAKE_INTENSITY, HIT_SHAKE_TIME);
        }

        // finally change the pipe's category bit so it cannot harm the robot again if it stays on its head
        if(pipe.getBody().getFixtureList().size != 0)
            StaticMethods.setCategoryBit(pipe.getBody().getFixtureList().first(), PIPE_ON_GROUND_CATEGORY);
    }

    private void feetPipeOnGroundBegin(Fixture fixA, Fixture fixB) {
        FallingPipe fallingPipe;

        if(fixA.getUserData() instanceof  FallingPipe) {
            fallingPipe = (FallingPipe) fixA.getUserData();
        }
        else {
            fallingPipe = (FallingPipe) fixB.getUserData();
        }

        // robot is on a pipe; turn on flag to set pipe's velocity to zero so as not to move with the robot
        fallingPipe.setFlagToCancelVelocity(true);
        Gdx.app.log("ContactManager", "Robot stepped on pipe. Flag to cancel pipe's velocity activated.");
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
        // set flagToCancelVelocity for pipes that are on the floor to sleep
        fallingPipe.setFlagToSleep(true);
    }

    private void robotWallBegin(Vector2 normal, Fixture fixA, Fixture fixB) {
        Robot robot;
        boolean wallJumpActivated = false;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();

            if (normal.x <= -1 / Math.sqrt(2)) {
                Gdx.app.log("ContactManager", "Robot hit wall from the right");
                wallJumpActivated = true;
                robot.setDirection(1);
            }
            else if(normal.x >= 1 / Math.sqrt(2)) {
                Gdx.app.log("ContactManager", "Robot hit wall from the left");
                wallJumpActivated = true;
                robot.setDirection(-1);
            }
        }
        else {
            robot = (Robot) fixB.getUserData();

            if(normal.x >= 1/Math.sqrt(2)) {
                Gdx.app.log("ContactManager", "Robot hit wall from the right");
                wallJumpActivated = true;
                robot.setDirection(1);
            }
            else if(normal.x <= -1/Math.sqrt(2)) {
                Gdx.app.log("ContactManager", "Robot hit wall from the left");
                wallJumpActivated = true;
                robot.setDirection(-1);
            }
        }

        // if wall jump was activated and the robot is in the air
        if(wallJumpActivated && footContactCounter == 0) {
            robot.setWallClimbing(true);
            robot.setCoyoteTimer(ROBOT_COYOTE_TIMER);
            Gdx.app.log("ContactManager", "WallClimbing = true");
            Gdx.app.log("ContactManager", "coyote timer set");
        }
    }

    private void robotTrampolineBegin(Vector2 normal, Fixture fixA, Fixture fixB) {
        Robot robot;
        Trampoline trampoline;
        boolean onTrampoline = false;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            trampoline = (Trampoline) fixB.getUserData();

            if(normal.y <= -1 / Math.sqrt(2)) {
                Gdx.app.log("ContactManager", "Robot stepped on trampoline");
                onTrampoline = true;
            }
        }
        else {
            robot = (Robot) fixB.getUserData();
            trampoline = (Trampoline) fixA.getUserData();


            if(normal.y >= 1 / Math.sqrt(2)) {
                Gdx.app.log("ContactManager", "Robot stepped on trampoline");
                onTrampoline = true;
            }
        }

        // if robot is on trampoline
        if(onTrampoline) {
//            trampoline.trampolineSprite.setTexture(trampoline.playScreen.getAssets().trampolineAssets.t2);
            trampoline.setStartTimeAnim(TimeUtils.nanoTime());
            trampoline.setActivated(true);
            robot.getBody().setLinearVelocity(0, 0);
            robot.getBody().applyLinearImpulse(new Vector2(0, 9.5f), robot.getBody().getWorldCenter(), true);
        }
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
            Gdx.app.log("ContactManager", "Foot contacts " + footContactCounter + " -> Feet ended contact with " + fixA.getUserData() + " or " + fixB.getUserData());
        }

        int collisionID = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(collisionID) {

            // robot - ladder
            case ROBOT_CATEGORY | LADDER_CATEGORY:
                robotLadderEnd(fixA, fixB);
                break;

            // robot - interactive platform
            case ROBOT_CATEGORY | INTERACTIVE_PLATFORM_CATEGORY:
                robotInteractivePlatEnd(fixA, fixB);
                break;

            // robot - spikes
            case ROBOT_CATEGORY | SPIKE_CATEGORY:
                robotSpikesEnd(fixA, fixB);
                break;

            // feet - pipe on ground
            case ROBOT_FEET_CATEGORY | PIPE_ON_GROUND_CATEGORY:
                feetPipeOnGroundEnd(fixA, fixB);
                break;

            // robot - wall jumping
            case ROBOT_CATEGORY | WALLJUMP_CATEGORY:
            robotWallEnd(fixA, fixB);
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

    // robot - interactive platform collision ends
    private void robotInteractivePlatEnd(Fixture fixA, Fixture fixB) {
        Robot robot;
        InteractivePlatform interactivePlatform;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            interactivePlatform = (InteractivePlatform) fixB.getUserData();
        }
        else {
            robot = (Robot) fixB.getUserData();
            interactivePlatform = (InteractivePlatform) fixA.getUserData();
        }

        robot.setOnInteractivePlatform(interactivePlatform, false);
        Gdx.app.log("ContactManager", "Off interactive platform");
    }

    private void feetPipeOnGroundEnd(Fixture fixA, Fixture fixB) {
        FallingPipe fallingPipe;

        if(fixA.getUserData() instanceof FallingPipe) {
            fallingPipe = (FallingPipe) fixA.getUserData();
        }
        else {
            fallingPipe = (FallingPipe) fixB.getUserData();

        }

        // robot got off the pipe; turn off flag to set pipe's velocity to zero
        fallingPipe.setFlagToCancelVelocity(false);
        Gdx.app.log("ContactManager", "Robot stepped off pipe. Flag to cancel pipe's velocity disabled.");
    }

    // this just prints statements
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

    private void robotWallEnd(Fixture fixA, Fixture fixB) {
        Robot robot;

        if(fixA.getUserData() instanceof Robot)
            robot = (Robot) fixA.getUserData();
        else
            robot = (Robot) fixB.getUserData();

        robot.setWallClimbing(false);
        Gdx.app.log("ContactManager", "WallClimbing = false");
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
