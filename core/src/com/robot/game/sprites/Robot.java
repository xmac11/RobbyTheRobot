package com.robot.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.robot.game.interactiveObjects.InteractivePlatform;

import static com.robot.game.util.Constants.*;

public class Robot extends InputAdapter {

    private Sprite robotSprite;
    private World world;
    private Body body;
    private boolean onLadder;

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
    }

    private void createRobotB2d() {
        // create body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(532 / PPM, 160 / PPM); // 32, 160 for starting // 532, 160 for ladder // 1092, 384 for moving platform
        bodyDef.fixedRotation = true;
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

        recShape.dispose();
//        circleShape.dispose();
    }

    public void update(float delta) {
        // first handle input
        handleInput(delta);

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

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            // GRADUAL ACCELERATION
            float targetVelocity = Math.min(body.getLinearVelocity().x + 0.1f, ROBOT_MAX_SPEED);
            temp.x = body.getMass() * (targetVelocity - currentVelocity);

            // CONSTANT SPEED OR GRADUAL ACCELERATION
            body.applyLinearImpulse(temp, body.getWorldCenter(), true);
//            body.applyLinearImpulse(new Vector2(body.getMass() * (ROBOT_MAX_SPEED - currentVelocity), 0), body.getWorldCenter(), true); // slow
        }
        else if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            // GRADUAL ACCELERATION
            float targetVelocity = Math.max(body.getLinearVelocity().x - 0.1f, -ROBOT_MAX_SPEED);
            temp.x = body.getMass() * (targetVelocity - currentVelocity);
            body.applyLinearImpulse(temp, body.getWorldCenter(), true);

            // CONSTANT SPEED
//            body.applyLinearImpulse(temp.scl(-1).sub(body.getMass() * currentVelocity, 0), body.getWorldCenter(), true);
//            body.applyLinearImpulse(new Vector2(body.getMass() * (-ROBOT_MAX_SPEED-currentVelocity), 0), body.getWorldCenter(), true); // slow

        }
        else {
            float targetVelocity = body.getLinearVelocity().x * 0.98f;
            temp.x = body.getMass() * (targetVelocity - currentVelocity);
            body.applyLinearImpulse(temp, body.getWorldCenter(), true);
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // robot jumps off ladder
            if(onLadder) {
                body.setGravityScale(1); // first turn on gravity, then jump
                body.applyLinearImpulse(new Vector2(0, 10.0f), body.getWorldCenter(), true);
            }
            else if(isOnInteractivePlatform) {
                isOnInteractivePlatform = false;
                body.applyLinearImpulse(new Vector2(0, 10.0f), body.getWorldCenter(), true); // make this constant
            }
            else
                body.applyLinearImpulse(new Vector2(0, 10.0f), body.getWorldCenter(), true);
        }
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
        Gdx.input.setInputProcessor(onLadder ? this : null);
        if(onLadder)
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
    }

    public void setOnInteractivePlatform(InteractivePlatform interactivePlatform, boolean isOnInteractivePlatform) {
        this.interactivePlatform = interactivePlatform;
        this.isOnInteractivePlatform = isOnInteractivePlatform;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.UP)
            body.setLinearVelocity(0, ROBOT_CLIMB_SPEED);
        if(keycode == Input.Keys.DOWN)
            body.setLinearVelocity(0, -ROBOT_CLIMB_SPEED);

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.UP || keycode == Input.Keys.DOWN)
            body.setLinearVelocity(0, 0);

        return true;
    }


}
