package com.robot.game.interactiveObjects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.robot.game.screens.ScreenLevel1;
import com.robot.game.util.Assets;

import java.util.Random;

import static com.robot.game.util.Constants.*;
import static com.robot.game.util.Constants.PPM;

public class FallingPipe extends Sprite{

    private ScreenLevel1 screenLevel1;
    private World world;
    private Body body;
    private Sprite pipeSprite;

    public FallingPipe(ScreenLevel1 screenLevel1) {
        this.screenLevel1 = screenLevel1;
        this.world = screenLevel1.getWorld();
        createPipeB2d();

        this.pipeSprite = new Sprite(Assets.getInstance().pipeAssets.debris);
        pipeSprite.setSize(PIPE_WIDTH / PPM, PIPE_HEIGHT / PPM);

        pipeSprite.setOrigin(PIPE_WIDTH / 2 / PPM, PIPE_HEIGHT / 2 / PPM);

    }

    private void createPipeB2d() {
        Random random = new Random();
        // create body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(randomPipePosition(), screenLevel1.getViewport().getWorldHeight());
//        bodyDef.position.set(screenLevel1.getRobot().getBody().getPosition().x, screenLevel1.getViewport().getWorldHeight());
        bodyDef.angle =  (float) Math.PI / 4 * (random.nextFloat() * (1 - (-1)) - 1);
        bodyDef.linearDamping = 0.0f;
        this.body = world.createBody(bodyDef);

        // create fixture
        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape recShape = new PolygonShape();
        recShape.set(PIPE_VERTICES);
        fixtureDef.shape = recShape;

        fixtureDef.friction = 0.4f;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.25f;
        fixtureDef.filter.categoryBits = PIPE_CATEGORY;
        fixtureDef.filter.maskBits = PIPE_MASK;

        this.body.createFixture(fixtureDef).setUserData(this);

        recShape.dispose();
    }

    public void update(float delta) {
        // attach sprite to body
        pipeSprite.setPosition(body.getPosition().x - PIPE_WIDTH / 2 / PPM, body.getPosition().y - PIPE_HEIGHT / 2 / PPM);

        // rotate sprite with body
        pipeSprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
    }

    @Override
    public void draw(Batch batch) {
        pipeSprite.draw(batch);
    }

    private float randomPipePosition() {
        Random random = new Random();
        float max = screenLevel1.getCamera().position.x + screenLevel1.getViewport().getWorldWidth() / 2;
        float min = screenLevel1.getCamera().position.x - screenLevel1.getViewport().getWorldWidth() / 2;
        return (random.nextFloat() * (max - min) + min);
    }
}
