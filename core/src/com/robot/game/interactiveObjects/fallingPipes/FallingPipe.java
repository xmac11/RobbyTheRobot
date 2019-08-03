package com.robot.game.interactiveObjects.fallingPipes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.*;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;
import com.robot.game.util.Damaging;

import static com.robot.game.util.Constants.*;

public class FallingPipe extends Sprite implements Damaging {

    private PlayScreen playScreen;
    private Assets assets;
    private World world;
    private Body body;
    private Sprite pipeSprite;
    private boolean flagToSleep;
    private boolean cache;
    private boolean flagToCancelVelocity;

    public FallingPipe(PlayScreen playScreen, boolean cache) {
        this.playScreen = playScreen;
        this.assets = playScreen.getAssets();
        this.world = playScreen.getWorld();
        this.cache = cache;
        createPipeB2d();

        this.pipeSprite = new Sprite(assets.pipeAssets.debris);
        pipeSprite.setSize(PIPE_WIDTH / PPM, PIPE_HEIGHT / PPM);
        pipeSprite.setOrigin(PIPE_WIDTH / 2 / PPM, PIPE_HEIGHT / 2 / PPM);
    }

    private void createPipeB2d() {
        // create body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;

        if(cache) {
            bodyDef.position.set(5112 / PPM + MathUtils.random(-128 / PPM, 128 / PPM),
                                 (playScreen.getMapHeight() - 24) / PPM);
            bodyDef.gravityScale = 0;
            bodyDef.awake = false;
        }
        // if robot is almost still, create pipe on top of it
        else if(Math.abs(playScreen.getRobot().getBody().getLinearVelocity().x) < 2f) {
            bodyDef.position.set(playScreen.getRobot().getBody().getPosition().x,
                    playScreen.getViewport().getWorldHeight());
        }
        // else create pipe somewhere in front of it
        else {
            bodyDef.position.set(playScreen.getRobot().getBody().getPosition().x + MathUtils.random(96f / PPM, 192 / PPM),
                                 playScreen.getViewport().getWorldHeight());
        }

        bodyDef.angle =  (float) Math.PI / 3 * MathUtils.random(-1f, 1f);
        this.body = world.createBody(bodyDef);

        // create fixture
        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape recShape = new PolygonShape();
//        recShape.setAsBox(PIPE_WIDTH / 2 / PPM, PIPE_HEIGHT / 2 / PPM);
        recShape.set(PIPE_VERTICES);
        fixtureDef.shape = recShape;

        fixtureDef.friction = 0.4f;
        fixtureDef.density = 1.0f;
        fixtureDef.restitution = 0.15f;
        fixtureDef.filter.categoryBits = PIPE_CATEGORY;
        fixtureDef.filter.maskBits = PIPE_MASK;

        this.body.createFixture(fixtureDef).setUserData(this);

        recShape.dispose();
    }

    public void update(float delta) {
        if(flagToSleep && body.getLinearVelocity().isZero()) {

            // change parameters so that robot can walk on pipe
            if(body.getFixtureList().size != 0) {
                body.getFixtureList().first().setRestitution(0);
                body.getFixtureList().first().setFriction(0.1f);
            }

            body.setAwake(false);
            flagToSleep = false;
            Gdx.app.log("FallingPipe","Pipe on ground stopped. Parameters changed, sleep activated");
        }

        // robot is on pipe; set pipe's velocity to zero so as not to move with the robot
        if(flagToCancelVelocity) {
            body.setLinearVelocity( 0, 0);
        }
    }

    @Override
    public void draw(Batch batch) {
        // attach sprite to body
        pipeSprite.setPosition(body.getPosition().x - PIPE_WIDTH / 2 / PPM, body.getPosition().y - PIPE_HEIGHT / 2 / PPM);

        // rotate sprite with body
        pipeSprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);

        pipeSprite.draw(batch);
    }

    @Override
    public int getDamage() {
        return DAMAGE_FROM_PIPE;
    }

    public Body getBody() {
        return body;
    }

    public void setFlagToSleep(boolean flagToSleep) {
        this.flagToSleep = flagToSleep;
    }

    public void setFlagToCancelVelocity(boolean flagToCancelVelocity) {
        this.flagToCancelVelocity = flagToCancelVelocity;
    }
}
