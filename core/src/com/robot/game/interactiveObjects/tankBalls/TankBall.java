package com.robot.game.interactiveObjects.tankBalls;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.Robot;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Damaging;

import static com.robot.game.util.Constants.*;

public class TankBall extends Sprite implements Damaging, Pool.Poolable {

    private PlayScreen playScreen;
    private Robot robot;
    private World world;
    private Body body;
    private boolean exploded;

    private float explosionStartTime;
    private float explosionElapsed;
    private Vector2 explosionPosition = new Vector2();

    public TankBall(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.robot = playScreen.getRobot();
        this.world = playScreen.getWorld();

        setRegion(playScreen.getAssets().tankBallAssets.tankFire);
        setSize(TANKBALL_WIDTH / PPM, TANKBALL_HEIGHT / PPM);
    }

    public void createTankBallB2d() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(4520 / PPM, 96 / PPM);
        this.body = world.createBody(bodyDef);

        // create fixture
        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape recShape = new PolygonShape();
        recShape.setAsBox(TANKBALL_WIDTH / 2 / PPM, TANKBALL_HEIGHT / 2 / PPM);
        fixtureDef.shape = recShape;
        fixtureDef.density = 1.0f;
        fixtureDef.filter.categoryBits = ENEMY_PROJECTILE_CATEGORY;
        fixtureDef.filter.maskBits = ENEMY_PROJECTILE_MASK;
        fixtureDef.isSensor = true;
        this.body.createFixture(fixtureDef).setUserData(this);

        recShape.dispose();
    }

    public void update(float delta) {
        if(exploded) {
            explosionElapsed = (TimeUtils.nanoTime() - explosionStartTime) * MathUtils.nanoToSec;
        }

        if(body.getPosition().y > playScreen.getCamera().position.y + playScreen.getViewport().getWorldHeight() / 2 || animationFinished()) {
            world.destroyBody(body);
            Gdx.app.log("TankBall", "Body destroyed");

            playScreen.getTankBalls().removeValue(this, false); // false in order to use .equals()
            Gdx.app.log("TankBall", "TankBall was removed from array");

            playScreen.getTankBallPool().free(this);
            Gdx.app.log("TankBall", "TankBall was freed back into the pool");

        }
    }

    @Override
    public void draw(Batch batch) {
        if(exploded) {
            setRegion(playScreen.getAssets().tankBallAssets.tankExplosionAnimation.getKeyFrame(explosionElapsed));
        }
        else {
            setPosition(body.getPosition().x - TANKBALL_WIDTH / 2 / PPM, body.getPosition().y - TANKBALL_HEIGHT / 2 / PPM);
        }

        super.draw(batch);
    }

    public Body getBody() {
        return body;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
        this.explosionStartTime = TimeUtils.nanoTime();
        this.explosionPosition.set(robot.getBody().getPosition().x - 32f / 2 / PPM, robot.getBody().getPosition().y - 32f / 2 / PPM);
        setBounds(explosionPosition.x, explosionPosition.y, 32 / PPM, 32 / PPM);
    }

    private boolean animationFinished() {
        return explosionElapsed >= 0.6f;
    }

    @Override
    public int getDamage() {
        return DAMAGE_FROM_ENEMY_PROJECTILE;
    }


    @Override
    public void reset() {
        setRegion(playScreen.getAssets().tankBallAssets.tankFire);
        setSize(TANKBALL_WIDTH / PPM, TANKBALL_HEIGHT / PPM);
        exploded = false;
        explosionStartTime = 0;
        explosionElapsed = 0;
    }
}
