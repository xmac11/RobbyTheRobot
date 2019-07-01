package com.robot.game.sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class Robot {

    private Sprite robotSprite;
    private World world;
    private Body body;

    public Robot(World world) {
        this.world = world;
        createRobotB2d();


        Texture texture = new Texture("robot.png");
//        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.robotSprite = new Sprite(texture);
        this.robotSprite.setSize(robotSprite.getWidth() / PPM, robotSprite.getHeight() / PPM);
//        this.robotSprite.setSize(32 / PPM, 64 / PPM);
        this.robotSprite.setOrigin(robotSprite.getWidth() / 2, robotSprite.getHeight() / 2);
    }

    public void createRobotB2d() {
        // create body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(32 / PPM, 160 / PPM);
        bodyDef.fixedRotation = true;
        this.body = world.createBody(bodyDef);

        // create fixture
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(ROBOT_RADIUS / PPM);
        fixtureDef.shape = circleShape;
        fixtureDef.filter.categoryBits = ROBOT_CATEGORY;
        fixtureDef.filter.maskBits = ROBOT_MASK;
        body.createFixture(fixtureDef).setUserData(this);

        circleShape.dispose();
    }

    public void update(float delta) {
        // first handle input
        handleInput(delta);

        robotSprite.setPosition(body.getPosition().x - ROBOT_RADIUS / PPM, body.getPosition().y - ROBOT_RADIUS / PPM);


    }

    public void handleInput(float delta) {
        int horizontalForce = 0; // reset every time

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            //            gameCam.position.x += 5 * dt;
            horizontalForce += 2;
            body.applyLinearImpulse(new Vector2(0.1f, 0), body.getWorldCenter(), true);
        }
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            //            gameCam.position.x -= 5 * dt;
            horizontalForce -= 2;
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            //            gameCam.position.y += 5 * dt;
            body.applyForceToCenter(0, 350, true);
        }
        body.setLinearVelocity(horizontalForce * 2.5f, body.getLinearVelocity().y);
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
}
