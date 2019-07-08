package com.robot.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.robot.game.interactiveObjects.InteractivePlatform;
import com.robot.game.util.ContactManager;
import com.robot.game.util.LadderClimbHandler;

import static com.robot.game.util.Constants.*;

public class Robot /*extends InputAdapter*/ {

    public static final float jumpVelocity = 2 * JUMP_HEIGHT / TIME_UNTIL_JUMP_HEIGHT;
    public static final float jumpGravity = - jumpVelocity / TIME_UNTIL_JUMP_HEIGHT;
    private float temporary;

    public static float IMPULSE;

    int remainingJumpSteps;

    float vY;

    private boolean jumping;

    private Sprite robotSprite;
    private World world;
    private Body body;
    private boolean onLadder;
    private boolean fallingOffLadder;
    private float jumpTimer;

    //CONSTANT SPEED
//    private final Vector2 ROBOT_IMPULSE;
    private Vector2 temp = new Vector2();

    // interactive platforms
    private InteractivePlatform interactivePlatform;
    private boolean isOnInteractivePlatform;

    public Robot(World world) {
        this.world = world;
        createRobotB2d();

//        this.ROBOT_IMPULSE = new Vector2(body.getMass() * ROBOT_MAX_SPEED, 0);

        Texture texture = new Texture("sf.png");
//        Texture texture = new Texture("robot2164.png");
//        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        this.robotSprite = new Sprite(texture);

//        this.robotSprite.setSize(robotSprite.getWidth() / PPM, robotSprite.getHeight() / PPM);
        robotSprite.setSize(ROBOT_WIDTH / PPM, ROBOT_HEIGHT / PPM);
        robotSprite.setPosition(body.getPosition().x - ROBOT_WIDTH / 2 / PPM, body.getPosition().y - ROBOT_HEIGHT / 2 / PPM); // for rectangle (not really needed since it's done by update)

        //        this.robotSprite.setOrigin(robotSprite.getWidth() / 2, robotSprite.getHeight() / 2);

        //Gdx.input.setInputProcessor(this);

        //this.IMPULSE = body.getMass() * (float) Math.sqrt(-2 * world.getGravity().y * 32 / PPM);
    }

    private void createRobotB2d() {
        // create body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(32 / PPM, 160 / PPM); // 32, 160 for starting // 532, 160 for ladder // 1092, 384 or 1500, 390 for moving platform
        bodyDef.fixedRotation = true;
        bodyDef.linearDamping = 0.1f;
        this.body = world.createBody(bodyDef);

        // create fixture
        FixtureDef fixtureDef = new FixtureDef();

        /*CircleShape circleShape = new CircleShape();
        circleShape.setRadius(ROBOT_RADIUS / PPM);
        fixtureDef.shape = circleShape;*/

        PolygonShape recShape = new PolygonShape();
        recShape.setAsBox(ROBOT_WIDTH / 2 / PPM, ROBOT_HEIGHT / 2 / PPM);
        fixtureDef.shape = recShape;

        fixtureDef.friction = 0.4f;
        fixtureDef.density = 1.0f;
        fixtureDef.filter.categoryBits = ROBOT_CATEGORY;
        fixtureDef.filter.maskBits = ROBOT_MASK;
        this.body.createFixture(fixtureDef).setUserData(this);

        // sensor feet
        recShape.setAsBox(ROBOT_FEET_WIDTH / 2 / PPM, ROBOT_FEET_HEIGHT / 2 / PPM, new Vector2(0, -ROBOT_HEIGHT / 2 / PPM), 0);
        fixtureDef.density = 0;
        fixtureDef.filter.categoryBits = ROBOT_FEET_CATEGORY;
        fixtureDef.filter.maskBits = ROBOT_FEET_MASK;
        fixtureDef.isSensor = true;
        this.body.createFixture(fixtureDef).setUserData(this);

        recShape.dispose();
//        circleShape.dispose();
    }

    public void update(float delta) {

        // first handle input
        handleInput(delta);

        //verlet testing
        /*if(!onLadder) {
            float f = body.getPosition().y + body.getLinearVelocity().y * delta + 0.5f * jumpGravity * delta * delta;
            body.getPosition().set(body.getPosition().x, f);
            float v = body.getLinearVelocity().y + jumpGravity * delta;
            body.setLinearVelocity(body.getLinearVelocity().x, v);
        }*/

        if(isOnInteractivePlatform)
            body.setLinearVelocity(body.getLinearVelocity().x, interactivePlatform.getBody().getLinearVelocity().y);

        // attach robot sprite to circle body
//        robotSprite.setPosition(body.getPosition().x - ROBOT_RADIUS / PPM, body.getPosition().y - ROBOT_RADIUS / PPM);
        robotSprite.setPosition(body.getPosition().x - ROBOT_WIDTH / 2 / PPM, body.getPosition().y - ROBOT_HEIGHT / 2 / PPM); // for rectangle

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
            float targetVelocity = Math.min(body.getLinearVelocity().x + 0.1f, ROBOT_MAX_SPEED);
            temp.x = body.getMass() * (targetVelocity - currentVelocity);

            // CONSTANT SPEED OR GRADUAL ACCELERATION
            body.applyLinearImpulse(temp, body.getWorldCenter(), true);
//            body.applyLinearImpulse(new Vector2(body.getMass() * (ROBOT_MAX_SPEED - currentVelocity), 0), body.getWorldCenter(), true); // slow
        }

        // Moving left
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            // GRADUAL ACCELERATION
            float targetVelocity = Math.max(body.getLinearVelocity().x - 0.1f, -ROBOT_MAX_SPEED);
            temp.x = body.getMass() * (targetVelocity - currentVelocity);
            body.applyLinearImpulse(temp, body.getWorldCenter(), true);

            // CONSTANT SPEED
//            body.applyLinearImpulse(temp.scl(-1).sub(body.getMass() * currentVelocity, 0), body.getWorldCenter(), true);
//            body.applyLinearImpulse(new Vector2(body.getMass() * (-ROBOT_MAX_SPEED-currentVelocity), 0), body.getWorldCenter(), true); // slow

        }

        // left-right keys released -> if body is moving, break
        else if(body.getLinearVelocity().x != 0) {
            float targetVelocity = body.getLinearVelocity().x * 0.98f;
            temp.x = body.getMass() * (targetVelocity - currentVelocity);
            body.applyLinearImpulse(temp, body.getWorldCenter(), true);
        }

        // Jumping
        jumpTimer -= delta;

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && !onLadder) {
            //remainingJumpSteps = 6;
//            verletTest(delta);

            jumpTimer = ROBOT_JUMP_TIMER; // start timer
            System.out.println("space pressed -> " + ContactManager.footContactCounter + " contacts");
        }

        // if there has been a timer set and is a foot contact
        if(jumpTimer > 0 && ContactManager.footContactCounter > 0 && !onLadder) {

            jumpTimer = 0; // reset timer

            // robot jumps off interactive platform
            if(isOnInteractivePlatform) {
                isOnInteractivePlatform = false;
                body.applyLinearImpulse(ROBOT_JUMP_IMPULSE, body.getWorldCenter(), true); // make this constant
            }
            // robot jumps from the ground
            else {
                body.setLinearVelocity(body.getLinearVelocity().x, ROBOT_JUMP_SPEED); // here I set the velocity since the impulse did not have impact when the player was falling
//                if(body.getLinearVelocity().y > 0) body.setLinearVelocity(body.getLinearVelocity().x, vY);
//                  body.applyLinearImpulse(new Vector2(0, IMPULSE), body.getWorldCenter(), true);
//                  body.applyLinearImpulse(new Vector2(0, 5f), body.getWorldCenter(), true);
            }
        }

        /*if(remainingJumpSteps > 0) {
            float force = body.getMass() * 5 * 60;
            force /= 6;
            body.applyForceToCenter(0, force, true);
            remainingJumpSteps--;
        }*/
    }

    public void dispose() {
        robotSprite.getTexture().dispose();
    }

    // getter for the Body
    public Body getBody() {
        return body;
    }

    public Sprite getRobotSprite() {
        return robotSprite;
    }

    public void setRobotSprite(Sprite robotSprite) {
        this.robotSprite = robotSprite;
    }

    public void setOnLadder(boolean onLadder) {
        this.onLadder = onLadder;
        body.setGravityScale(onLadder ? 0 : 1);
        Gdx.input.setInputProcessor(onLadder ? new LadderClimbHandler(this) : null/*this*/);
        if(onLadder)
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
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

    private void verletTest(float delta) {
        body.setLinearVelocity(body.getLinearVelocity().x, jumpVelocity);
    }

    public void setJumping(boolean jumping) {
        this.jumping = jumping;
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
