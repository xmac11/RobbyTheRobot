package com.robot.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.robot.game.interactiveObjects.MovingPlatform;

import static com.robot.game.util.Constants.*;

public class Robot extends InputAdapter {

    private Sprite robotSprite;
    private World world;
    private Body body;
    private boolean onLadder;

    // this is used for constantly moving platforms
    private MovingPlatform movingPlatform;
    private boolean isOnMovingPlatform;

    public Robot(World world) {
        this.world = world;
        this.onLadder = false;
        createRobotB2d();


        Texture texture = new Texture("sf.png");
//        Texture texture = new Texture("robot2164.png");
//        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        this.robotSprite = new Sprite(texture);

//        this.robotSprite.setSize(robotSprite.getWidth() / PPM, robotSprite.getHeight() / PPM);
        this.robotSprite.setSize(32 / PPM, 64 / PPM);
        this.robotSprite.setPosition(body.getPosition().x - ROBOT_RADIUS / PPM, body.getPosition().y - ROBOT_RADIUS / PPM);

        //        this.robotSprite.setOrigin(robotSprite.getWidth() / 2, robotSprite.getHeight() / 2);
    }

    public void createRobotB2d() {
        // create body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(1092 / PPM, 400 / PPM); // 32, 160 for starting // 532, 160 for ladder // 1092, 384 for moving platform
        bodyDef.fixedRotation = true;
        this.body = world.createBody(bodyDef);

        // create fixture
        FixtureDef fixtureDef = new FixtureDef();

        /*CircleShape circleShape = new CircleShape();
        circleShape.setRadius(ROBOT_RADIUS / PPM);
        fixtureDef.shape = circleShape;*/

        PolygonShape recShape = new PolygonShape();
        recShape.setAsBox(32f / 2 / PPM, 64f / 2 / PPM);
        fixtureDef.shape = recShape;

        fixtureDef.friction = 0.4f;
        fixtureDef.density = 1.0f;
        fixtureDef.filter.categoryBits = ROBOT_CATEGORY;
        fixtureDef.filter.maskBits = ROBOT_MASK;
        this.body.createFixture(fixtureDef).setUserData(this);

        // create feet
        /*EdgeShape feetShape = new EdgeShape();
        feetShape.set(new Vector2(-40f / 2 / PPM, -64f / 2 / PPM), new Vector2(40f / 2 / PPM, -64f / 2 / PPM));
        fixtureDef.shape = feetShape;
        fixtureDef.filter.categoryBits = ROBOT_FEET_CATEGORY;
        fixtureDef.filter.maskBits = ROBOT_FEET_MASK;
        body.createFixture(fixtureDef).setUserData(this);*/

        recShape.dispose();
//        circleShape.dispose();
//        feetShape.dispose();
    }

    public void update(float delta) {
        // first handle input
        handleInput(delta);

        // if robot is on moving platform, make it move along with  it
        if(isOnMovingPlatform) {
            body.setLinearVelocity(body.getLinearVelocity().x, movingPlatform.getBody().getLinearVelocity().y);
        }

        // attach robot sprite to circle body
//        robotSprite.setPosition(body.getPosition().x - ROBOT_RADIUS / PPM, body.getPosition().y - ROBOT_RADIUS / PPM);
        robotSprite.setPosition(body.getPosition().x - 16 / PPM, body.getPosition().y - 32 / PPM); // for rectangle

    }

    public void handleInput(float delta) {
        float vX = 0; // reset every time
        float vY = 0;

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//            vX = 4;
            body.applyForceToCenter(12.0f, 0, true);
//            body.applyLinearImpulse(new Vector2(0.1f, 0), body.getWorldCenter(), true); //NOTE: shouldn't create vector here
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//            vX = -4;
            body.applyForceToCenter(-12.0f, 0, true);
            //            body.applyLinearImpulse(new Vector2(-0.1f, 0), body.getWorldCenter(), true);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            // robot jumps off ladder
            if(onLadder) {
                body.setTransform(body.getPosition().x, body.getPosition().y * 1.05f, 0);
                body.applyLinearImpulse(new Vector2(0, -10.0f), body.getWorldCenter(), true);
            }
            else if(isOnMovingPlatform) {
                isOnMovingPlatform = false;
                body.applyLinearImpulse(new Vector2(0, 10.0f), body.getWorldCenter(), true);
            }
            else
                body.applyLinearImpulse(new Vector2(0, 10.0f), body.getWorldCenter(), true);
        }
        /*if(Gdx.input.isKeyPressed(Input.Keys.UP) && onLadder) {
            vY = 2;
//            body.applyForceToCenter(0, 0.05f, true);
//            body.applyLinearImpulse(new Vector2(0, 0.1f), body.getWorldCenter(), true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN) && onLadder) {
            vY = -2;
            //            body.applyForceToCenter(0, 0.05f, true);
            //            body.applyLinearImpulse(new Vector2(0, 0.1f), body.getWorldCenter(), true);
        }*/
//        body.setLinearVelocity(vX, vY);
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
        if(onLadder)
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
        Gdx.input.setInputProcessor(onLadder ? this : null);
    }

    public boolean isOnMovingPlatform() {
        return isOnMovingPlatform;
    }

    // this is used for constantly moving platforms
    public void setOnMovingPlatform(MovingPlatform movingPlatform, boolean isOnMovingPlatform) {
        this.movingPlatform = movingPlatform;
        this.isOnMovingPlatform = isOnMovingPlatform;
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.UP)
            body.setLinearVelocity(0, 2);
        if(keycode == Input.Keys.DOWN)
            body.setLinearVelocity(0, -2);

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.UP || keycode == Input.Keys.DOWN)
            body.setLinearVelocity(0, 0);

        return true;
    }


}
