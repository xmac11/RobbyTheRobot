package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.Fish;
import com.robot.game.entities.Robot;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.entities.abstractEnemies.EnemySeekAI;
import com.robot.game.interactiveObjects.platforms.Elevator;
import com.robot.game.interactiveObjects.spikes.Spike;
import com.robot.game.interactiveObjects.Trampoline;
import com.robot.game.interactiveObjects.collectables.Collectable;
import com.robot.game.interactiveObjects.collectables.PowerUp;
import com.robot.game.interactiveObjects.fallingPipes.FallingPipe;
import com.robot.game.interactiveObjects.ladder.Ladder;
import com.robot.game.interactiveObjects.platforms.FallingPlatform;
import com.robot.game.interactiveObjects.platforms.InteractivePlatform;
import com.robot.game.interactiveObjects.platforms.MovingPlatform;
import com.robot.game.interactiveObjects.tankBalls.TankBall;
import com.robot.game.util.staticMethods.StaticMethods;

import static com.robot.game.util.constants.Constants.*;
import static com.robot.game.util.constants.Enums.Facing.*;


public class ContactManager implements ContactListener {

    private int footContactCounter = 0;

    @Override
    public void beginContact(Contact contact) {
        // Get the two fixtures that are in contact
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        // Get the normal (unit) vector
        Vector2 normal = contact.getWorldManifold().getNormal();

        if(fixA == null || fixB == null) return;
        if(fixA.getUserData() == null || fixB.getUserData() ==  null) return;

        if(fixA.getFilterData().categoryBits == ROBOT_FEET_CATEGORY || fixB.getFilterData().categoryBits == ROBOT_FEET_CATEGORY) {
            footContactCounter++;
            Gdx.app.log("ContactManager", "Foot contacts " + footContactCounter + " -> Feet in contact with " + fixA.getUserData() + " or " + fixB.getUserData());
        }

        /* Technique adapted from https://www.youtube.com/playlist?list=PLZm85UZQLd2SXQzsF-a0-pPF6IWDDdrXt */
        // Bitwise OR the category bits of the the two fixtures in contact
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
                break;

            case ROBOT_CATEGORY | ENEMY_PROJECTILE_CATEGORY:
                robotProjectileBegin(fixA, fixB);
                break;

            // enemy - disable chase sensor:
            case ENEMY_CATEGORY | CHASE_SENSOR_CATEGORY:
                enemyChaseSensor(fixA, fixB);
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

        // every time robot hits the bottom of the ladder, turn off gravity and enable up-down keys
        // this mimics the case where the robot is on the ladder and falls down to the bottom
        if(ladder.getDescription().equals(LADDER_BOTTOM_DESCRIPTION)) {
            Gdx.app.log("ContactManager", "On bottom ladder");
            robot.setFallingOffLadder(false);
            // set input processor
            if(!robot.getPlayScreen().isOnAndroid()) {
                Gdx.input.setInputProcessor(robot.getPlayScreen().getLadderClimbHandler());
            }
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

            // set flag that robot is on platform
            robot.setOnInteractivePlatform(interactivePlatform, true);

            if(interactivePlatform instanceof FallingPlatform) {
                ((FallingPlatform) interactivePlatform).setFlagToMove(true);
            }
            else if(interactivePlatform instanceof Elevator) {
                Elevator elevator = (Elevator) interactivePlatform;

                // if platform has not been already stopped, move it
                if(!elevator.isStopped()) {
                    elevator.movePlatform();
                }
            }
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
            // play hurt sound
            robot.playSoundEffect();

            // decrease health
            StaticMethods.decreaseHealth(robot, spike);

            //add spike (damaging object) to HashMap in order to render the damage incurred
            robot.getPlayScreen().getFeedbackRenderer().getDamageFromHitToDraw().put(spike, 1f);

            // make it invulnerable for 1 second
            robot.setInvulnerable(1f);
            Gdx.app.log("ContactManager", "Robot health " + robot.getCheckpointData().getHealth() + "%");
        }

        // if robot is walking on spikes, it dies (but does not lose health)
        else if(spike.mightBeWalked()) {
            // play hurt sound
            robot.playSoundEffect();

            robot.setFlicker(true);
            robot.setWalkingOnSpikes(true);
        }

        // make it flicker
        robot.setFlicker(true);
        //shake camera
        robot.getShakeEffect().shake(HIT_SHAKE_INTENSITY, HIT_SHAKE_TIME);
    }

    private void robotEnemyBegin(Vector2 normal, Fixture fixA, Fixture fixB) {
        Robot robot;
        Enemy enemy;
        boolean steppedOnEnemy = false;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            enemy = (Enemy) fixB.getUserData();

            // fishes don't die when stepped (they are facing upwards)
            if(normal.y <= -1/Math.sqrt(2) && !(enemy instanceof Fish)) {
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

            // fishes don't die when stepped (they are facing upwards)
            if(normal.y >= 1/Math.sqrt(2) && !(enemy instanceof Fish)) {
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
            // play step on enemy sound
            enemy.playSoundEffect();

            // kill enemy
            StaticMethods.killEnemy(robot, enemy, 0, 0);
        }
        // otherwise it means that the robot was hit by an enemy
        else {
            // play hurt sound
            robot.playSoundEffect();

            // robot hit by enemy
            robotHitByEnemy(robot, enemy);
        }
    }

    private void robotHitByEnemy(Robot robot, Enemy enemy) {
        // set enemy to be a sensor
        if(enemy.getBody().getFixtureList().size != 0) {
            enemy.getBody().getFixtureList().first().setSensor(true);
        }
        Gdx.app.log("ContactManager", "Enemy sensor = TRUE");

        // if enemy is an EnemyArriveAI (dynamic body) turn off gravity, since it is now a sensor
        if(enemy instanceof EnemySeekAI) {
            enemy.getBody().setGravityScale(0);
            Gdx.app.log("ContactManager", "Gravity was turned off for the " + enemy.getClass());
        }

        // decrease robot's health
        StaticMethods.decreaseHealth(robot, enemy);

        //add enemy (damaging object) to HashMap in order to render the damage incurred (if robot is not dead)
        if(robot.getCheckpointData().getHealth() > 0) {
            robot.getPlayScreen().getFeedbackRenderer().getDamageFromHitToDraw().put(enemy, 1f);
        }

        // make it flicker
        robot.setFlicker(true);

        // shake camera
        robot.getShakeEffect().shake(HIT_SHAKE_INTENSITY, HIT_SHAKE_TIME);
        Gdx.app.log("ContactManager", "Robot health " + robot.getCheckpointData().getHealth() + "%");
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

        // play appropriate sound effect if applicable
        collectable.playSoundEffect();

        // if torch was collected
        if(collectable.isTorch()) {
            // activate cone light
            robot.getPlayScreen().getConeLight().setActive(true);

            // activate point light for hand (gun)
            robot.getPlayScreen().getPointLightHand().setActive(true);

            robot.getPlayScreen().getPointLightHead().setDistance(16 / PPM);
            robot.setHasTorch(true);
            robot.getCheckpointData().setHasTorch(true);
        }

        // if collectable is not the torch, increase score (method adds zero points for powerup) and queue for feedbackRenderer
        if(!collectable.isTorch()) {
            // increase score for collectable
            StaticMethods.increaseScore(robot, collectable);

            // add collectable to the HashMap in order to render the points gained or the powerup animation
            StaticMethods.queueForPointsRenderer(robot, collectable);
        }

        if(collectable instanceof PowerUp) {
            StaticMethods.increaseHealth(robot, (PowerUp) collectable);
            Gdx.app.log("ContactManger", "Robot collected powerup, Health " + robot.getCheckpointData().getHealth() + "%");
        }

        // flag to collect (in order to destroy the body)
        collectable.setFlagToCollect();

        // add the collectable to the list of collectables to be disabled from being respawned if the robot dies
        collectable.addToDisableSpawning((int) collectable.getMapObject().getProperties().get("id"));

        // flag that a new item was collected
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
            // play hurt sound
            robot.playSoundEffect();

            // decrease health
            StaticMethods.decreaseHealth(robot, pipe);

            //add pipe (damaging object) to HashMap in order to render the damage incurred
            robot.getPlayScreen().getFeedbackRenderer().getDamageFromHitToDraw().put(pipe, 1f);

            // make it invulnerable for 1 second
            robot.setInvulnerable(1f);
            // make it flicker
            robot.setFlicker(true);
            // shake the camera
            robot.getShakeEffect().shake(HIT_SHAKE_INTENSITY, HIT_SHAKE_TIME);
        }

        // finally change the pipe's category bit so it cannot harm the robot again if it stays on its head
        pipe.setFlagToChangeCategory(true);
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
        }
        else {
            fallingPipe = (FallingPipe) fixB.getUserData();
        }
        fallingPipe.setFlagToChangeCategory(true);
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
            robot.setWallJumping(true);
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
            // play trampoline sound
            trampoline.playSoundEffect();

            trampoline.setStartTimeAnim(TimeUtils.nanoTime());
            trampoline.setActivated(true);
            robot.getBody().setLinearVelocity(0, 0);
            robot.getBody().applyLinearImpulse(TRAMPOLINE_IMPULSE, robot.getBody().getWorldCenter(), true);
        }
    }

    private void robotProjectileBegin(Fixture fixA, Fixture fixB) {
        Robot robot;
        TankBall tankBall;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            tankBall = (TankBall) fixB.getUserData();
        }
        else  {
            robot = (Robot) fixB.getUserData();
            tankBall = (TankBall) fixA.getUserData();
        }

        // set flag to play explosion animation
        tankBall.setExploded(true);
        // stop tank ball and set its gravity to zero
        tankBall.getBody().setLinearVelocity(0, 0 );
        tankBall.getBody().setGravityScale(0);

        // play tankball hit robot sound
        tankBall.playSoundEffect();

        // play hurt sound
        robot.playSoundEffect();

        // decrease robot's health
        StaticMethods.decreaseHealth(robot, tankBall);

        //add tank ball (damaging object) to HashMap in order to render the damage incurred
        robot.getPlayScreen().getFeedbackRenderer().getDamageFromHitToDraw().put(tankBall, 1f);

        // make it flicker
        robot.setFlicker(true);

        // shake camera
        robot.getShakeEffect().shake(HIT_SHAKE_INTENSITY, HIT_SHAKE_TIME);
        Gdx.app.log("ContactManager", "Robot health " + robot.getCheckpointData().getHealth() + "%");
    }

    private void enemyChaseSensor(Fixture fixA, Fixture fixB) {
        EnemySeekAI arriveAI;

        if(fixA.getUserData() instanceof EnemySeekAI) {
            arriveAI = (EnemySeekAI) fixA.getUserData();
        }
        else if(fixB.getUserData() instanceof EnemySeekAI) {
            arriveAI = (EnemySeekAI) fixB.getUserData();
        }
        else return;

        Gdx.app.log("ContactManager", "Sensor disabled chasing");
        arriveAI.setActivated(false);
        arriveAI.setLocked(true);
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

        // Bitwise OR the category bits of the the two fixtures in contact
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

            case ROBOT_CATEGORY | ENEMY_CATEGORY:
                robotEnemyEnd(fixA, fixB);
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

    private void robotEnemyEnd(Fixture fixA, Fixture fixB) {
        Robot robot;
        Enemy enemy;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
            enemy = (Enemy) fixB.getUserData();
        }
        else {
            robot = (Robot) fixB.getUserData();
            enemy = (Enemy) fixA.getUserData();
        }

        // disable enemy's sensor when contact ends
        if(enemy.getBody().getFixtureList().size != 0) {
            enemy.getBody().getFixtureList().first().setSensor(false);
        }
        Gdx.app.log("ContactManager", "Enemy sensor = FALSE");

        // if enemy is an EnemyArriveAI (dynamic body) turn gravity back on
        if(enemy instanceof EnemySeekAI) {
            enemy.getBody().setGravityScale(1);
            Gdx.app.log("ContactManager", "Gravity was turned back on for the" + enemy.getClass());
        }
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

    private void robotWallEnd(Fixture fixA, Fixture fixB) {
        Robot robot;

        if(fixA.getUserData() instanceof Robot) {
            robot = (Robot) fixA.getUserData();
        }
        else {
            robot = (Robot) fixB.getUserData();
        }

        // update facing direction
        if(robot.getDirection() == 1) {
            robot.setFacing(RIGHT);
        }
        else {
            robot.setFacing(LEFT);
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

        // Get the two fixtures that contact
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        // Get the normal (unit) vector
        Vector2 normal = contact.getWorldManifold().getNormal();

        // Bitwise OR the category bits of the the two fixtures in contact
        int collisionID = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        // robot - enemy
        if (collisionID == (ROBOT_CATEGORY | ENEMY_CATEGORY)) {
            if (fixA.getUserData() instanceof Robot) {
                // robot was hit by enemy
                if (normal.y >= 1 / Math.sqrt(2) || normal.x <= -1 / Math.sqrt(2) || normal.x >= 1 / Math.sqrt(2)) {
                    contact.setEnabled(false);
                    Gdx.app.log("ContactManager", "Robot-Enemy contact was disabled in presolve()");
                }
            } else {
                // robot was hit by enemy
                if (normal.y <= -1 / Math.sqrt(2) || normal.x >= 1 / Math.sqrt(2) || normal.x <= -1 / Math.sqrt(2)) {
                    contact.setEnabled(false);
                    Gdx.app.log("ContactManager", "Robot-Enemy contact was disabled in presolve()");
                }
            }
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public int getFootContactCounter() {
        return footContactCounter;
    }





}
