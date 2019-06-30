package com.robot.game.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class Robot {

    private PlayScreen playScreen;
    private World world;
    private Body body;

    public Robot(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.world = playScreen.getWorld();
        createRobotB2d();
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
        body.createFixture(fixtureDef);

        circleShape.dispose();
    }

    public void update(float delta) {
       /* Vector2 position = body.getPosition();
        position.x = MathUtils.clamp( position.x,
                                          0,
                                         MAP_WIDTH / PPM - playScreen.getViewport().getWorldWidth() / 2);

        // clamp position of robot within the map
        if(body.getPosition().x < ROBOT_RADIUS / PPM)
            body.setTransform(body.getFixtureList().first().getShape().getRadius(), body.getPosition().y, 0);
        if(body.getPosition().x > MAP_WIDTH / PPM - ROBOT_RADIUS / PPM)
            body.setTransform(MAP_WIDTH / PPM - ROBOT_RADIUS / PPM, body.getPosition().y, 0);
        if(body.getPosition().y > MAP_HEIGHT / PPM - ROBOT_RADIUS / PPM) {
            //            body.setTransform(body.getPosition().x, MAP_HEIGHT / PPM - ROBOT_RADIUS / PPM , 0);
        }*/

    }

    public void dispose() {
        world.dispose();
    }

    // getter for the Body
    public Body getBody() {
        return body;
    }
}
