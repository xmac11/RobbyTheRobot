package com.robot.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class Robot extends InputAdapter {

    private Sprite robotSprite;
    private World world;
    private Body body;
    // ladder
    private boolean onLadder;

    public Robot(World world) {
        this.world = world;
        this.onLadder = false;
        createRobotB2d();


        Texture texture = new Texture("robot.png");
//        Texture texture = new Texture("robot2164.png");
//        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.robotSprite = new Sprite(texture);

        this.robotSprite.setSize(robotSprite.getWidth() / PPM, robotSprite.getHeight() / PPM);
//        this.robotSprite.setSize(32 / PPM, 64 / PPM);

//        this.robotSprite.setOrigin(robotSprite.getWidth() / 2, robotSprite.getHeight() / 2);
    }

    public void createRobotB2d() {
        // create body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(32 / PPM, 160 / PPM);
        bodyDef.fixedRotation = true;
        this.body = world.createBody(bodyDef);
        this.body.getPosition().set(bodyDef.position);

        // create fixture
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(ROBOT_RADIUS / PPM);
        fixtureDef.shape = circleShape;
        fixtureDef.friction = 0.4f;
        fixtureDef.density = 1.0f;
        fixtureDef.filter.categoryBits = ROBOT_CATEGORY;
        fixtureDef.filter.maskBits = ROBOT_MASK;
        this.body.createFixture(fixtureDef).setUserData(this);

        circleShape.dispose();
    }

    public void update(float delta) {
        // first handle input
        handleInput(delta);

        robotSprite.setPosition(body.getPosition().x - ROBOT_RADIUS / PPM, body.getPosition().y - ROBOT_RADIUS / PPM);


    }

    public void handleInput(float delta) {
        float vX = 0; // reset every time
        float vY = 0;

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
//            vX = 4;
            body.applyForceToCenter(6.0f, 0, true);
//            body.applyLinearImpulse(new Vector2(0.1f, 0), body.getWorldCenter(), true); //NOTE: shouldn't create vector here
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
//            vX = -4;
            body.applyForceToCenter(-6.0f, 0, true);
            //            body.applyLinearImpulse(new Vector2(-0.1f, 0), body.getWorldCenter(), true);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
//            body.applyForceToCenter(0, 350, true);
            body.applyLinearImpulse(new Vector2(0, 5.0f), body.getWorldCenter(), true);
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

    public void setOnLadder(boolean onLadder) {
        this.onLadder = onLadder;
        body.setGravityScale(onLadder ? 0 : 1);
        Gdx.input.setInputProcessor(onLadder ? this : null);
    }

    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.UP)
            body.setLinearVelocity(0, 2);
        if(keycode == Input.Keys.DOWN)
            body.setLinearVelocity(0, -2);
        if(keycode == Input.Keys.SPACE) {
            setOnLadder(false);
            body.setLinearVelocity(0, -2);
        }

        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        if(keycode == Input.Keys.UP || keycode == Input.Keys.DOWN)
            body.setLinearVelocity(0, 0);

        return true;
    }
}
