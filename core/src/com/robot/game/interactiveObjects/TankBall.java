package com.robot.game.interactiveObjects;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Damaging;

import static com.robot.game.util.Constants.*;

public class TankBall extends Pool<TankBall> implements Damaging {

    private PlayScreen playScreen;
    private World world;
    private Body body;

    public TankBall(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.world = playScreen.getWorld();
        createTankBallB2d();
    }

    private void createTankBallB2d() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; /////////
        bodyDef.position.set(4520 / PPM, 96 / PPM);
        this.body = world.createBody(bodyDef);

        // create fixture
        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape recShape = new PolygonShape();
        recShape.setAsBox(16f / 2 / PPM, 16f / 2 / PPM);
        fixtureDef.shape = recShape;
        fixtureDef.density = 1.0f;
        fixtureDef.filter.categoryBits = PROJECTILE_CATEGORY;
        fixtureDef.filter.maskBits = PROJECTILE_MASK;
        this.body.createFixture(fixtureDef).setUserData(this);

        body.setGravityScale(0);
//        body.applyLinearImpulse(new Vector2(0, 10), body.getWorldCenter(), true);

        recShape.dispose();
    }

    public void update(float delta) {

    }

    @Override
    protected TankBall newObject() {
        return new TankBall(playScreen);
    }

    @Override
    public int getDamage() {
        return 0;
    }
}
