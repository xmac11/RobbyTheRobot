package com.robot.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.interactiveObjects.InteractivePlatform;
import com.robot.game.interactiveObjects.MovingPlatform;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.*;

import static com.robot.game.util.Constants.*;

public class Robot extends Sprite /*extends InputAdapter*/ {

    private Sprite robotSprite;
    private PlayScreen playScreen;
    private World world;
    private Body body;

    // state booleans
    private boolean onLadder;
    private boolean fallingOffLadder;
    private boolean walkingOnSpikes;
    private boolean dead;

    // invulnerability
    private boolean invulnerable;
    private float invulnerableStartTime;
    private float invulnerableElapsed;

    // jump timers
    private float jumpTimeout;
    private float jumpTimer;
    private float coyoteTimer;

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
    private GameData gameData;

    public Robot(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.world = playScreen.getWorld();
        this.gameData = playScreen.getGameData();
        createRobotB2d();

        this.robotSprite = new Sprite(Assets.getInstance().robotAssets.atlasRegion);
        robotSprite.setSize(ROBOT_SPRITE_WIDTH / PPM, ROBOT_SPRITE_HEIGHT / PPM);

        //Gdx.input.setInputProcessor(this);
    }

    private void createRobotB2d() {
        // create body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        //2520, 200 before second ladder // 2840, 160 on second ladder // 2790, 400 for multiple plats
        bodyDef.position.set(gameData.getSpawnLocation()); // 32, 160 for starting // 532, 160 for ladder // 800, 384 after ladder //1092, 384 or 1500, 390 for moving platform
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.0f;
        this.body = world.createBody(bodyDef);

        // create fixture
        FixtureDef fixtureDef = new FixtureDef();

        /*CircleShape circleShape = new CircleShape();
        circleShape.setRadius(ROBOT_RADIUS / PPM);
        fixtureDef.shape = circleShape;*/

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
        //        circleShape.dispose();
    }

    public void update(float delta) {

        // first handle input
        handleInput(delta);

        if(isOnInteractivePlatform) {

            // platform moving vertically, set robot's vY to platform's vY
            if(interactivePlatform.getBody().getLinearVelocity().y != 0)
                body.setLinearVelocity(body.getLinearVelocity().x, interactivePlatform.getBody().getLinearVelocity().y);

                // platform moving horizontally to the right, apply force to robot so it moves with it
            else if(interactivePlatform.getBody().getLinearVelocity().x != 0 ) {
                body.applyForceToCenter(-0.6f * body.getMass() * world.getGravity().y, 0, true);
            }

        }

        // apply additional force when object is falling in order to land faster
        if(body.getLinearVelocity().y < 0 && !isOnInteractivePlatform && !onLadder) {
            body.applyForceToCenter(0, -7.5f, true);
        }

        // attach robot sprite to body
        robotSprite.setPosition(body.getPosition().x - (ROBOT_BODY_WIDTH / 2 + 2.5f) / PPM, body.getPosition().y - ROBOT_BODY_HEIGHT / 2 / PPM); // for rectangle

        // First checkpoint
        if(!gameData.isFirstCheckpointActivated()) {
            checkFirstCheckpoint();
        }
        // Second checkpoint
        else if(!gameData.isSecondCheckpointActivated()) {
            checkSecondCheckpoint();
        }
        else if(!gameData.isThirdCheckpointActivated()) {
            checkThirdCheckpoint();
        }

        // if robot is invulnerable
        if(isInvulnerable()) {
            // check how much time has passed
            invulnerableElapsed = (TimeUtils.nanoTime() - invulnerableStartTime) * MathUtils.nanoToSec;
            // if more than 1 second, disable it
            if(invulnerableElapsed >= 1) {
                setInvulnerable(false);
                invulnerableElapsed = 0;
            }
        }

        // conditions for player to die
        if((body.getPosition().y < 0 || gameData.getHealth() <= 0 || walkingOnSpikes) && !flicker ) {
            dead = true;
            // decrease lives by one
            gameData.decreaseLives();
            // reset health
            gameData.setHealth(100);
        }
    }

    private void handleInput(float delta) {

        // CONSTANT SPEED
        //        temp.x = ROBOT_IMPULSE.x; // reset every frame
        //        temp.y = ROBOT_IMPULSE.y;

        // CONSTANT SPEED OR GRADUAL ACCELERATION
        float currentVelocity = body.getLinearVelocity().x;

        // Moving right
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            // GRADUAL ACCELERATION
            float targetVelocity = Math.min(body.getLinearVelocity().x + 0.1f, ROBOT_MAX_HOR_SPEED);
            temp.x = body.getMass() * (targetVelocity - currentVelocity);

            // CONSTANT SPEED OR GRADUAL ACCELERATION
            body.applyLinearImpulse(temp, body.getWorldCenter(), true);
            //            body.applyLinearImpulse(new Vector2(body.getMass() * (ROBOT_MAX_HOR_SPEED - currentVelocity), 0), body.getWorldCenter(), true); // slow
        }

        // Moving left
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            // this is for the case of the horizontally moving platform that will stop under the ladder
            // since the normal impulse applied is not sufficient to move the player when the platform is moving to the right, so a special case is included
            // this will be used at most once, so a new Vector is created instead of keeping a variable in the Constant class
            if(isOnInteractivePlatform && interactivePlatform instanceof MovingPlatform && ((MovingPlatform)interactivePlatform).shouldStop()) {
                body.applyLinearImpulse(new Vector2(-0.25f, 0), body.getWorldCenter(), true);
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

        // left-right keys released -> if body is moving, break
        else if(body.getLinearVelocity().x != 0) {
            float targetVelocity = body.getLinearVelocity().x * 0.96f;
            temp.x = body.getMass() * (targetVelocity - currentVelocity);
            body.applyLinearImpulse(temp, body.getWorldCenter(), true);
        }

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
         *  It is reduced by "delta" at every frame.
         *  When the player is grounded, coyoteTimer is set to a value, e.g. 0.2 seconds.
         *  When the player presses SPACE, in addition to the previous checks, it is checked whether the player
         *  was grounded within the last 0.2 seconds.
         *  If yes, the player jumps evey though it is not grounded right now. At this point the timer is reset to zero.
         *
         *  jumpTimeout is used to disable jumping immediately after jumping
         *  Since the player can jump while not fully grounded, it would be able to jump immediately after jumping.
         *  jumpTimeout is increased by "delta" at every frame.
         *  In addition to the previous checks, it is checked whether the time out period has expired, in particular,
         *  whether jumpTimeout is higher than a certain value, e.g. 0.3 seconds.
         *  If yes the player jumps and the timeout is reset to zero */
        jumpTimer -= delta;
        coyoteTimer -= delta;
        jumpTimeout += delta;

        // when the robot is grounded, start coyote timer
        if(ContactManager.getFootContactCounter() > 0)
            coyoteTimer = ROBOT_COYOTE_TIMER;

        // when space is pressed, start jump timer
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !onLadder) {

            jumpTimer = ROBOT_JUMP_TIMER; // start jumping timer
            Gdx.app.log("Robot","space pressed -> " + ContactManager.getFootContactCounter() + " contacts");
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
            // robot jumps from the ground
            else {
                body.setLinearVelocity(body.getLinearVelocity().x, ROBOT_JUMP_SPEED); // here I set the velocity since the impulse did not have impact when the player was falling
                Gdx.app.log("Robot","Just jumped: " + ContactManager.getFootContactCounter() + " contacts");
                //                body.applyLinearImpulse(ROBOT_JUMP_IMPULSE, body.getWorldCenter(), true);
            }
        }

        //// Debug keys for checkpoints ////
        toggleDebugCheckpoints();

    }

    public void draw(SpriteBatch batch, float delta) {
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

    // getter for the Body
    public Body getBody() {
        return body;
    }

    public void setOnLadder(boolean onLadder) {
        this.onLadder = onLadder;
        Gdx.input.setInputProcessor(onLadder ? new LadderClimbHandler(this) : null/*this*/);
        body.setGravityScale(onLadder ? 0 : 1);
        if(onLadder) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            Gdx.app.log("Robot", "On ladder, velocity in y set to zero");
        }
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

    public GameData getGameData() {
        return gameData;
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

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;

        if(invulnerable) {
            Gdx.app.log("Robot", "Invulnerability started");
            this.invulnerableStartTime = TimeUtils.nanoTime();
        }
        else {
            Gdx.app.log("Robot", "Invulnerability ended");
        }
    }

    public boolean isWalkingOnSpikes() {
        return walkingOnSpikes;
    }

    public void setWalkingOnSpikes(boolean walkingOnSpikes) {
        this.walkingOnSpikes = walkingOnSpikes;
    }

    // Checkpoints
    private void checkFirstCheckpoint() {
        if( Math.abs( (body.getPosition().x - FIRST_CHECKPOINT_LOCATION.x) * PPM )  <= CHECKPOINT_TOLERANCE) {
            Gdx.app.log("Robot","First checkpoint activated!");
            gameData.setSpawnLocation(FIRST_CHECKPOINT_LOCATION);
            gameData.setFirstCheckpointActivated(true);
            FileSaver.saveData(gameData);
        }
    }

    private void checkSecondCheckpoint() {
        if( Math.abs( (body.getPosition().x - SECOND_CHECKPOINT_LOCATION.x) * PPM )  <= CHECKPOINT_TOLERANCE
                && Math.abs( (body.getPosition().y - SECOND_CHECKPOINT_LOCATION.y) * PPM )  <= CHECKPOINT_TOLERANCE) {

            Gdx.app.log("Robot","Second checkpoint activated!");
            gameData.setSpawnLocation(SECOND_CHECKPOINT_LOCATION);
            gameData.setSecondCheckpointActivated(true);
            FileSaver.saveData(gameData);
        }
    }

    private void checkThirdCheckpoint() {
        if( Math.abs( (body.getPosition().x - THIRD_CHECKPOINT_LOCATION.x) * PPM )  <= CHECKPOINT_TOLERANCE) {
            Gdx.app.log("Robot","Third checkpoint activated!");
            gameData.setSpawnLocation(THIRD_CHECKPOINT_LOCATION);
            gameData.setThirdCheckpointActivated(true);
            FileSaver.saveData(gameData);
        }
    }

    private void toggleDebugCheckpoints() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_0) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_0)) {
            Gdx.app.log("Robot", "Checkpoints deleted");
            gameData.setSpawnLocation(SPAWN_LOCATION);
            gameData.setFirstCheckpointActivated(false);
            gameData.setSecondCheckpointActivated(false);
            gameData.setThirdCheckpointActivated(false);
            FileSaver.saveData(gameData);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) {
            Gdx.app.log("Robot", "First checkpoint set");
            gameData.setSpawnLocation(FIRST_CHECKPOINT_LOCATION);
            gameData.setFirstCheckpointActivated(true);
            gameData.setSecondCheckpointActivated(false);
            gameData.setThirdCheckpointActivated(false);
            FileSaver.saveData(gameData);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            Gdx.app.log("Robot", "Second checkpoint set");
            gameData.setSpawnLocation(SECOND_CHECKPOINT_LOCATION);
            gameData.setFirstCheckpointActivated(true);
            gameData.setSecondCheckpointActivated(true);
            gameData.setThirdCheckpointActivated(false);
            FileSaver.saveData(gameData);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_3) || Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) {
            Gdx.app.log("Robot", "Third checkpoint set");
            gameData.setSpawnLocation(THIRD_CHECKPOINT_LOCATION);
            gameData.setFirstCheckpointActivated(true);
            gameData.setSecondCheckpointActivated(true);
            gameData.setThirdCheckpointActivated(true);
            FileSaver.saveData(gameData);
        }
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