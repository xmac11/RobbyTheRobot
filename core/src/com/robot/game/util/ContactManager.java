package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.Fish;
import com.robot.game.entities.Robot;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.entities.abstractEnemies.EnemyArriveAI;
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

        // if platform is waiting and has not been already stopped, move it
        if(movingPlatform.isWaiting() && !movingPlatform.isStopped())
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
            StaticMethods.decreaseHealth(robot, spike);

            //add spike (damaging object) to HashMap in order to render the damage incurred
            robot.getPlayScreen().getFeedbackRenderer().getDamageFromHitToDraw().put(spike, 1f);

            // make it invulnerable for 1 second
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
            StaticMethods.killEnemy(robot, enemy, 0, 0);
        }
        // otherwise it means that the robot was hit by an enemy
        else {
            // set enemy to be a sensor
            if(enemy.getBody().getFixtureList().size != 0) {
                enemy.getBody().getFixtureList().first().setSensor(true);
            }
            Gdx.app.log("ContactManager", "Enemy sensor = TRUE");

            // if enemy is an EnemyArriveAI (dynamic body) turn off gravity, since it is now a sensor
            if(enemy instanceof EnemyArriveAI) {
                enemy.getBody().setGravityScale(0);
                Gdx.app.log("ContactManager", "Gravity was turned off for the " + enemy.getClass());

                // this is used so that the enemy doesn't jump when reaching the robot
                ((EnemyArriveAI) enemy).setInContactWithRobot(true);
            }

            // decrease robot's health
            StaticMethods.decreaseHealth(robot, enemy);

            //add enemy (damaging object) to HashMap in order to render the damage incurred
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
//            StaticMethods.setMaskBit(fixA, NOTHING_MASK);
        }
        else {
            fallingPipe = (FallingPipe) fixB.getUserData();
//            StaticMethods.setMaskBit(fixB, NOTHING_MASK);
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
        EnemyArriveAI arriveAI;

        if(fixA.getUserData() instanceof EnemyArriveAI) {
            arriveAI = (EnemyArriveAI) fixA.getUserData();
        }
        else if(fixB.getUserData() instanceof EnemyArriveAI) {
            arriveAI = (EnemyArriveAI) fixB.getUserData();
        }
        else return;

        Gdx.app.log("ContactManager", "Sensor disabled chasing");
        arriveAI.getArrive().setEnabled(false);
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
        if(enemy instanceof EnemyArriveAI) {
            enemy.getBody().setGravityScale(1);
            Gdx.app.log("ContactManager", "Gravity was turned back on for the" + enemy.getClass());

            // this is used so that the enemy doesn't jump when reaching the robot
            ((EnemyArriveAI) enemy).setInContactWithRobot(false);
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
        Vector2 normal = contact.getWorldManifold().getNormal();

        int collisionID = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(collisionID) {

            // robot - enemy
            case ROBOT_CATEGORY | ENEMY_CATEGORY:
                System.out.println("this");
                Robot robot;
                Enemy enemy;

                if(fixA.getUserData() instanceof Robot) {
                    robot = (Robot) fixA.getUserData();
                    enemy = (Enemy) fixB.getUserData();

                    // robot was hit by enemy
                    if(normal.y >= 1 / Math.sqrt(2) || normal.x <= -1 / Math.sqrt(2) || normal.x >= 1 / Math.sqrt(2)) {
                        contact.setEnabled(false);
                        Gdx.app.log("ContactManager", "Robot-Enemy contact was disabled in presolve()");
                    }
                }
                else {
                    robot = (Robot) fixB.getUserData();
                    enemy = (Enemy) fixA.getUserData();

                    // robot was hit by enemy
                    if(normal.y <= -1/Math.sqrt(2) || normal.x >= 1/Math.sqrt(2) || normal.x <= -1/Math.sqrt(2)) {
                        contact.setEnabled(false);
                        Gdx.app.log("ContactManager", "Robot-Enemy contact was disabled in presolve()");
                    }
                }
                break;
        }
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    public int getFootContactCounter() {
        return footContactCounter;
    }





}
