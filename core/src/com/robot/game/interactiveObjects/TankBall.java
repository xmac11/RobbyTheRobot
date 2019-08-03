package com.robot.game.interactiveObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Damaging;

import static com.robot.game.util.Constants.*;

public class TankBall extends Sprite implements Damaging, Pool.Poolable {

    private PlayScreen playScreen;
    private World world;
    private Body body;
    private boolean exploded;
    private Sprite tankBallSprite;

    private float explosionStartTime;
    private float explosionElapsed;

    private Vector2 explosionPosition = new Vector2();

    public TankBall(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.world = playScreen.getWorld();

        this.tankBallSprite = new Sprite(playScreen.getAssets().tankBallAssets.tankBallTexture);
        tankBallSprite.setSize(16f / PPM, 24f / PPM);
    }

    public void createTankBallB2d() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody; /////////
        bodyDef.position.set(4520 / PPM, 96 / PPM);
        this.body = world.createBody(bodyDef);

        // create fixture
        FixtureDef fixtureDef = new FixtureDef();

        PolygonShape recShape = new PolygonShape();
        recShape.setAsBox(16f / 2 / PPM, 24f / 2 / PPM);
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

            playScreen.tankBalls.removeValue(this, false); // false in order to use .equals()
            Gdx.app.log("TankBall", "TankBall was removed from array");

            playScreen.tankBallPool.free(this);
            Gdx.app.log("TankBall", "free");

        }
    }

    @Override
    public void draw(Batch batch) {
        if(exploded) {
            tankBallSprite.setTexture(playScreen.getAssets().tankBallAssets.textureAnimation.getKeyFrame(explosionElapsed));
            tankBallSprite.setPosition(explosionPosition.x, explosionPosition.y);
            tankBallSprite.setSize(16f / PPM, 24f / PPM);
        }

        else {
            tankBallSprite.setPosition(body.getPosition().x - (16f / 2) / PPM, body.getPosition().y - 24f / 2 / PPM);
        }

        tankBallSprite.draw(batch);
    }

    public Body getBody() {
        return body;
    }

    public void setExploded(boolean exploded) {
        this.exploded = exploded;
        this.explosionStartTime = TimeUtils.nanoTime();
        this.explosionPosition.set(playScreen.getRobot().getBody().getPosition().x, playScreen.getRobot().getBody().getPosition().y);
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
        tankBallSprite.setTexture(playScreen.getAssets().tankBallAssets.tankBallTexture);
        exploded = false;
        explosionStartTime = 0;
        explosionElapsed = 0;
    }
}
