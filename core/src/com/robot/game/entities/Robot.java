package com.robot.game.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.steer.Steerable;
import com.badlogic.gdx.ai.utils.Location;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.camera.ShakeEffect;
import com.robot.game.interactiveObjects.platforms.InteractivePlatform;
import com.robot.game.interactiveObjects.platforms.MovingPlatform;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;
import com.robot.game.util.ContactManager;
import com.robot.game.util.checkpoints.CheckpointData;
import com.robot.game.util.raycast.MyRayCastCallback;

import static com.robot.game.util.Constants.*;
import static com.robot.game.util.Enums.*;
import static com.robot.game.util.Enums.Facing.*;

public class Robot extends Sprite implements Steerable<Vector2> {

    private Facing facing;

    private Assets assets;
    private Sprite robotSprite;
    private PlayScreen playScreen;
    private World world;
    private ContactManager contactManager;
    private Body body;
    private ShakeEffect shakeEffect;

    // state booleans
    public boolean onLadder;
    private boolean fallingOffLadder;
    private boolean walkingOnSpikes;
    private boolean dead;

    // invulnerability
    private boolean invulnerable;
    private float invulnerableStartTime;
    private float invulnerableElapsed;
    private float invulnerabilityPeriod;

    // jump timers
    private float jumpTimeout;
    private float jumpTimer;
    private float coyoteTimer;
    private float climbTimer;

    //CONSTANT SPEED
    //    private final Vector2 ROBOT_IMPULSE;
    private Vector2 temp = new Vector2();

    // interactive platforms
    private InteractivePlatform interactivePlatform;
    private boolean isOnInteractivePlatform;

    // flicker effect
    private float alpha;
    private boolean flicker;
    private float flickerStartTime;
    private float flickerElapsed;

    // Game data
    private CheckpointData checkpointData;

    // wall climbing
    private boolean isWallClimbing;
    private int direction;
    private Vector2 tempWallJumpingImpulse = new Vector2();

    // ray cast callback
    private MyRayCastCallback callback;

    public Robot(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.assets = playScreen.getAssets();
        this.world = playScreen.getWorld();
        this.contactManager = playScreen.getContactManager();
        this.checkpointData = playScreen.getCheckpointData();
        this.shakeEffect = playScreen.getShakeEffect();
        this.facing = RIGHT;
        createRobotB2d();

        this.robotSprite = new Sprite(assets.robotAssets.atlasRegion);
        robotSprite.setSize(ROBOT_SPRITE_WIDTH / PPM, ROBOT_SPRITE_HEIGHT / PPM);

        // create ray cast callback
        this.callback = new MyRayCastCallback();

        //Gdx.input.setInputProcessor(this);
    }

    private void createRobotB2d() {
        // create body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        //2520, 200 before second ladder // 2840, 160 on second ladder // 2790, 400 for multiple plats
        bodyDef.position.set(/*checkpointData.getSpawnLocation()*//* 80 / PPM, 110 / PPM*/ /*500 / PPM, 110 / PPM*/ 1056 / PPM, 110 / PPM /*1900 / PPM, 110 / PPM*/ /*2410 / PPM, 780 / PPM*/ /*3416 / PPM, 780 / PPM*/
                /*4350 / PPM, 780 / PPM*/ /*4448 / PPM, 130 / PPM*/); // 32, 160 for starting // 532, 160 for ladder // 800, 384 after ladder //1092, 384 or 1500, 390 for moving platform
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.0f;
        this.body = world.createBody(bodyDef);

        // create fixture
        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape recShape = new PolygonShape();
        recShape.setAsBox(ROBOT_BODY_WIDTH / 2 / PPM, ROBOT_BODY_HEIGHT / 2 / PPM);
        fixtureDef.shape = recShape;

        fixtureDef.friction = 0.4f;
        fixtureDef.density = 1.0f;
        fixtureDef.filter.categoryBits = ROBOT_CATEGORY;
        fixtureDef.filter.maskBits = ROBOT_MASK /*NOTHING_MASK*/;
        this.body.createFixture(fixtureDef).setUserData(this);

        // sensor feet
        recShape.setAsBox(ROBOT_FEET_WIDTH / 2 / PPM, ROBOT_FEET_HEIGHT / 2 / PPM, new Vector2(0, -ROBOT_BODY_HEIGHT / 2 / PPM), 0);
        fixtureDef.density = 0;
        fixtureDef.filter.categoryBits = ROBOT_FEET_CATEGORY;
        fixtureDef.filter.maskBits = ROBOT_FEET_MASK;
        fixtureDef.isSensor = true;
        this.body.createFixture(fixtureDef).setUserData("feet");

        recShape.dispose();
    }

    public void update(float delta) {
        // first process input
        processInput(delta);

        // update robot when on interactive platform
        if(isOnInteractivePlatform && interactivePlatform != null) {

            // platform moving vertically, set robot's vY to platform's vY
            if(interactivePlatform.getBody().getLinearVelocity().y != 0) {
                body.setLinearVelocity(body.getLinearVelocity().x * BREAK_GROUND_FACTOR, interactivePlatform.getBody().getLinearVelocity().y);
            }

            // platform moving horizontally to the right, apply force to robot so it moves with it
            else if(interactivePlatform.getBody().getLinearVelocity().x != 0 ) {
                if(body.getFixtureList().size != 0 && interactivePlatform.getBody().getFixtureList().size != 0)
                    body.applyForceToCenter(
                            -(float) Math.sqrt(body.getFixtureList().first().getFriction() * interactivePlatform.getBody().getFixtureList().first().getFriction())
                            * body.getMass() * world.getGravity().y, 0, true);

                // if robot moved on platform, and therefore gained higher speed, lerp its speed towards the platform's speed
                if(body.getLinearVelocity().x != interactivePlatform.getBody().getLinearVelocity().x)
                    body.setLinearVelocity(MathUtils.lerp(body.getLinearVelocity().x, interactivePlatform.getBody().getLinearVelocity().x, 0.05f), body.getLinearVelocity().y);
            }
        }

        // apply additional force when robot is falling in order to land faster
        if(body.getLinearVelocity().y < 0 && !isOnInteractivePlatform && !onLadder) {
            body.applyForceToCenter(0, -7.5f, true);
        }

        // if robot is invulnerable
        if(invulnerable) {

            // check how much time has passed
            invulnerableElapsed = (TimeUtils.nanoTime() - invulnerableStartTime) * MathUtils.nanoToSec;

            // if more than 1 second, disable it
            if(invulnerableElapsed >= invulnerabilityPeriod) {
                invulnerable = false;
                invulnerableElapsed = 0;
                Gdx.app.log("Robot", "Invulnerability ended");
            }
        }

        // keep robot within map
        if(body.getPosition().x < ROBOT_BODY_WIDTH / 2 / PPM)
            body.setTransform(ROBOT_BODY_WIDTH / 2 / PPM, body.getPosition().y, 0);
        if(body.getPosition().x > (playScreen.getMapWidth() - ROBOT_BODY_WIDTH / 2) / PPM)
            body.setTransform((playScreen.getMapWidth() - ROBOT_BODY_WIDTH / 2) / PPM, body.getPosition().y, 0);

        // conditions for robot to die
        if((body.getPosition().y < 0 || checkpointData.getHealth() <= 0 || walkingOnSpikes) && !flicker ) {
            dead = true;
            // decrease lives by one
            checkpointData.decreaseLives();
            // reset health
            if(checkpointData.getHealth() <= 0) {
                checkpointData.setHealth(100); // to decide: should the robot restore its health when falling in the water?
            }
        }
    }

    private void processInput(float delta) {

        // CONSTANT SPEED
        //        temp.x = ROBOT_IMPULSE.x; // reset every frame
        //        temp.y = ROBOT_IMPULSE.y;

        // CONSTANT SPEED OR GRADUAL ACCELERATION
        float currentVelocity = body.getLinearVelocity().x;

        // Moving right
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if(facing != RIGHT) {
                facing = RIGHT;
                Gdx.app.log("Robot", "facing = " + facing);
            }

            // GRADUAL ACCELERATION
            float targetVelocity = Math.min(body.getLinearVelocity().x + 0.1f, ROBOT_MAX_HOR_SPEED);
            temp.x = body.getMass() * (targetVelocity - currentVelocity);

            // CONSTANT SPEED OR GRADUAL ACCELERATION
            body.applyLinearImpulse(temp, body.getWorldCenter(), true);
            //            body.applyLinearImpulse(new Vector2(body.getMass() * (ROBOT_MAX_HOR_SPEED - currentVelocity), 0), body.getWorldCenter(), true); // slow
        }

        // Moving left
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if(facing != LEFT) {
                facing = LEFT;
                Gdx.app.log("Robot", "facing = " + facing);
            }

            // this is for the case of the horizontally moving platform that will stop under the ladder
            // since the normal impulse applied is not sufficient to move the player when the platform is moving to the right, so a special case is included
            if(isOnInteractivePlatform && interactivePlatform instanceof MovingPlatform && ((MovingPlatform)interactivePlatform).shouldStop() && interactivePlatform.getvX() != 0) {
                body.applyLinearImpulse(LEFT_IMPULSE_ON_MOVING_PLATFORM, body.getWorldCenter(), true);
            }
            else {
                // GRADUAL ACCELERATION
                float targetVelocity = Math.max(body.getLinearVelocity().x - 0.1f, -ROBOT_MAX_HOR_SPEED);
                temp.x = body.getMass() * (targetVelocity - currentVelocity);
                body.applyLinearImpulse(temp, body.getWorldCenter(), true);
            }

            // CONSTANT SPEED
            //            body.applyLinearImpulse(temp.scl(-1).sub(body.getMass() * currentVelocity, 0), body.getWorldCenter(), true);
            //            body.applyLinearImpulse(new Vector2(body.getMass() * (-ROBOT_MAX_HOR_SPEED-currentVelocity), 0), body.getWorldCenter(), true); // slow

        }

        // left-right keys released -> if body is moving on the ground, break
        else if(body.getLinearVelocity().x != 0 /*&& contactManager.getFootContactCounter() != 0*/ && !isOnInteractivePlatform) {
            float targetVelocity = body.getLinearVelocity().x * BREAK_GROUND_FACTOR;
            temp.x = body.getMass() * (targetVelocity - currentVelocity);
            body.applyLinearImpulse(temp, body.getWorldCenter(), true);
        }
        // left-right keys released -> if body is moving in the air, break
        /*else if(body.getLinearVelocity().x != 0 && contactManager.getFootContactCounter() == 0 && !isOnInteractivePlatform) {
            float targetVelocity = body.getLinearVelocity().x * BREAK_AIR_FACTOR;
            temp.x = body.getMass() * (targetVelocity - currentVelocity);
            body.applyLinearImpulse(temp, body.getWorldCenter(), true);
        }*/

        // Jumping
        /* I use three timers:
         *  jumpTimer is used to enable jumping if not having landed yet.
         *  It is reduced by "delta" at every frame.
         *  When the player presses SPACE, jumpTimer is set to a value, e.g. 0.2 seconds.
         *  When the player reaches the ground, it is checked if SPACE was pressed within the last 0.2 seconds.
         *  If yes, the player jumps even though it was not grounded when SPACE was pressed. At this point the timer
         *  is reset to zero.
         *
         *  coyoteTimer is used to enable jumping off an edge if not fully grounded
         *  When the player is NOT grounded, it is reduced by "delta" at every frame.
         *  When the player is grounded again (or is on a wall jumping surface), it is set to a value, e.g. 0.2 seconds.
         *  When the player presses SPACE, in addition to the previous checks, it is checked whether the player
         *  was grounded within the last 0.2 seconds.
         *  If yes, the player jumps evey though it is not grounded right now. At this point the timer is reset to zero.
         *
         *  jumpTimeout is used to disable jumping immediately after jumping
         *  Since the player can jump while not fully grounded, it would be able to jump immediately after jumping.
         *  jumpTimeout is increased by "delta" at every frame.
         *  In addition to the previous checks, it is checked whether the timeout period has expired, in particular,
         *  whether jumpTimeout is higher than a certain value, e.g. 0.3 seconds.
         *  If yes the player jumps and the timeout is reset to zero */
        jumpTimer -= delta;
        if(contactManager.getFootContactCounter() == 0) coyoteTimer -= delta;
        jumpTimeout += delta;

        // when the robot is grounded, start coyote timer
        if(contactManager.getFootContactCounter() > 0 && coyoteTimer != ROBOT_COYOTE_TIMER)
            coyoteTimer = ROBOT_COYOTE_TIMER;

        // when space is pressed, start jump timer
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !onLadder) {

            jumpTimer = ROBOT_JUMP_TIMER; // start jumping timer
            Gdx.app.log("Robot","space pressed, jump timer was set -> " + contactManager.getFootContactCounter() + " contacts");
        }

        // if the timers have been set and robot not on ladder, jump
        if(jumpTimer > 0 && coyoteTimer > 0 && jumpTimeout > ROBOT_JUMP_TIMEOUT && !onLadder) {
            Gdx.app.log("Robot","jumpTimeout: " + jumpTimeout);
            // reset timers
            jumpTimer = 0;
            coyoteTimer = 0;
            jumpTimeout = 0;

            // robot jumps off interactive platform
            if(isOnInteractivePlatform) {
                isOnInteractivePlatform = false;
                // when platform is moving upwards, the velocity is not sufficient to make the player jump, so the velocity of the platform is also added
                body.setLinearVelocity(body.getLinearVelocity().x, ROBOT_JUMP_SPEED + interactivePlatform.getBody().getLinearVelocity().y);
            }
            else if(isWallClimbing) {
                tempWallJumpingImpulse.set(WALL_JUMPING_IMPULSE.x * direction, WALL_JUMPING_IMPULSE.y);
                body.applyLinearImpulse(tempWallJumpingImpulse, body.getWorldCenter(), true);
            }

            // robot jumps from the ground
            else {
                body.setLinearVelocity(body.getLinearVelocity().x, ROBOT_JUMP_SPEED); // here I set the velocity since the impulse did not have impact when the player was falling
                Gdx.app.log("Robot","Just jumped: " + contactManager.getFootContactCounter() + " contacts");
                //                body.applyLinearImpulse(ROBOT_JUMP_IMPULSE, body.getWorldCenter(), true);
            }
        }

        /*  Similarly, climbTimer is used to enable climbing if up key is pressed slightly before getting on ladder.
         *  It is reduced by "delta" at every frame.
         *  When the player presses UP, climbTimer is set to a value, e.g. 0.3 seconds.
         *  When the player gets on the ladder, it is checked if UP was pressed within the last 0.3 seconds and is still being pressed.
         *  If yes, the player climbs even though it was not on the ladder when UP was pressed. At this point the timer is reset to zero.*/
        climbTimer -= delta;

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && !onLadder) {
            climbTimer = ROBOT_CLIMB_TIMER;
            Gdx.app.log("Robot","UP key pressed in robot class -> climbTimer was set");
        }

        // laser shot
        if(Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            playScreen.getLaserHandler().startRayCast();
        }

//        System.out.println(body.getLinearVelocity().x);
    }

    public void draw(SpriteBatch batch, float delta) {
        if(facing == RIGHT) {
            if(robotSprite.isFlipX())
                robotSprite.flip(true, false);

            // attach robot sprite to body
            robotSprite.setPosition(body.getPosition().x - (ROBOT_BODY_WIDTH / 2 + 2.5f) / PPM, body.getPosition().y - ROBOT_BODY_HEIGHT / 2 / PPM);
        }
        else if(facing == LEFT) {
            if(!robotSprite.isFlipX())
                robotSprite.flip(true, false);

            // attach robot sprite to body
            robotSprite.setPosition(body.getPosition().x - (ROBOT_BODY_WIDTH / 2 + 8.5f) / PPM, body.getPosition().y - ROBOT_BODY_HEIGHT / 2 / PPM);
        }

        // if not flickering, draw sprite
        if(!flicker)
            robotSprite.draw(batch);
        // else if flickering
        else {
            // interpolate alpha value between 0 and 1 using sin(x)
            robotSprite.draw(batch, (float) Math.abs(Math.sin(alpha)));
            //System.out.println(Math.abs(Math.sin(alpha)));
            alpha += delta * 20;

            // if elapsed flicker time has exceeded 1 second, stop flickering and reset variables to zero
            flickerElapsed = (TimeUtils.nanoTime() - flickerStartTime) * MathUtils.nanoToSec;
            if(flickerElapsed > FLICKER_TIME) {
                flicker = false;
                flickerElapsed = 0;
                alpha = 0;
            }
        }
    }

    // getter for the ScreenLevel1
    public PlayScreen getPlayScreen() {
        return playScreen;
    }

    // getter for the Body
    public Body getBody() {
        return body;
    }

    public void setOnLadder(boolean onLadder) {
        this.onLadder = onLadder;
        Gdx.input.setInputProcessor(onLadder ? playScreen.getLadderClimbHandler() : null);

        // if on ladder, turn off gravity
        body.setGravityScale(onLadder ? 0 : 1);

        if(onLadder) {
            world.clearForces();

            // stop from falling
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            Gdx.app.log("Robot", "On ladder, velocity in y set to zero");

            // check if UP key was pressed right before getting on the ladder
            playScreen.getLadderClimbHandler().checkForClimbTimer();
        }
    }

    public float getClimbTimer() {
        return climbTimer;
    }

    public void resetClimbTimer() {
        this.climbTimer = 0;
    }

    public void setOnInteractivePlatform(InteractivePlatform interactivePlatform, boolean isOnInteractivePlatform) {
        this.interactivePlatform = interactivePlatform;
        this.isOnInteractivePlatform = isOnInteractivePlatform;
    }

    public boolean isFallingOffLadder() {
        return fallingOffLadder;
    }

    public void setFallingOffLadder(boolean fallingOffLadder) {
        this.fallingOffLadder = fallingOffLadder;
    }

    public void setFlicker(boolean flicker) {
        this.flicker = flicker;

        if(flicker)
            this.flickerStartTime = TimeUtils.nanoTime();
    }

    public CheckpointData getCheckpointData() {
        return checkpointData;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerable(float invulnerabilityPeriod) {
        Gdx.app.log("Robot", "Invulnerability started");
        this.invulnerable = true;
        this.invulnerabilityPeriod = invulnerabilityPeriod;
        this.invulnerableStartTime = TimeUtils.nanoTime();
    }

    public boolean isWalkingOnSpikes() {
        return walkingOnSpikes;
    }

    public void setWalkingOnSpikes(boolean walkingOnSpikes) {
        this.walkingOnSpikes = walkingOnSpikes;
    }

    public ShakeEffect getShakeEffect() {
        return shakeEffect;
    }

    public void setCoyoteTimer(float coyoteTimer) {
        this.coyoteTimer = coyoteTimer;
    }

    public void setWallClimbing(boolean wallClimbing) {
        isWallClimbing = wallClimbing;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public MyRayCastCallback getCallback() {
        return callback;
    }

    public Facing getFacing() {
        return facing;
    }

    @Override
    public Vector2 getLinearVelocity() {
        return body.getLinearVelocity();
    }

    @Override
    public float getAngularVelocity() {
        return body.getAngularVelocity();
    }

    @Override
    public float getBoundingRadius() {
        return 64 / PPM;
    }

    @Override
    public boolean isTagged() {
        return false;
    }

    @Override
    public void setTagged(boolean tagged) {

    }

    @Override
    public float getZeroLinearSpeedThreshold() {
        return 0;
    }

    @Override
    public void setZeroLinearSpeedThreshold(float value) {

    }

    @Override
    public float getMaxLinearSpeed() {
        return 0;
    }

    @Override
    public void setMaxLinearSpeed(float maxLinearSpeed) {

    }

    @Override
    public float getMaxLinearAcceleration() {
        return 0;
    }

    @Override
    public void setMaxLinearAcceleration(float maxLinearAcceleration) {

    }

    @Override
    public float getMaxAngularSpeed() {
        return 0;
    }

    @Override
    public void setMaxAngularSpeed(float maxAngularSpeed) {

    }

    @Override
    public float getMaxAngularAcceleration() {
        return 0;
    }

    @Override
    public void setMaxAngularAcceleration(float maxAngularAcceleration) {

    }

    @Override
    public Vector2 getPosition() {
        /*if(facing == RIGHT) {
            return body.getPosition().sub(32f / PPM, 0);
        }
        else {
            return body.getPosition().add(32f / PPM, 0);
        }*/
        return body.getPosition();
    }

    @Override
    public float getOrientation() {
        return 0;
    }

    @Override
    public void setOrientation(float orientation) {

    }

    @Override
    public float vectorToAngle(Vector2 vector) {
        return 0;
    }

    @Override
    public Vector2 angleToVector(Vector2 outVector, float angle) {
        return null;
    }

    @Override
    public Location<Vector2> newLocation() {
        return null;
    }


/*@Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.SPACE) {
            jumpTimer = ROBOT_JUMP_TIMER; // start timer
            System.out.println("space pressed -> " + ContactManager.footContactCounter + " contacts");
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {

        if(keycode == Input.Keys.SPACE) {
            if(body.getLinearVelocity().y > 0)
                body.setLinearVelocity(body.getLinearVelocity().x, body.getLinearVelocity().y * 0.5f);

        }
        return true;
    }*/

}