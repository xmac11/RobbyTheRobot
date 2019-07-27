package com.robot.game.interactiveObjects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.robot.game.screens.ScreenLevel1;
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.PPM;

public abstract class InteractivePlatform extends Sprite {

    protected Assets assets;
    protected World world;
    protected Body body;
    protected float vX;
    protected float vY;
    protected  float width;
    protected  float height;
    private Sprite interactivePlatformSprite;

    protected InteractivePlatform(ScreenLevel1 screenLevel1, Body body, MapObject object) {
        this.assets = screenLevel1.getAssets();
        this.world = screenLevel1.getWorld();
        this.body = body;
        this.vX = (float) object.getProperties().get("vX");
        this.vY = (float) object.getProperties().get("vY");
        //body.setActive(false);

        this.width = (float) object.getProperties().get("width");
        this.height = (float) object.getProperties().get("height");

        this.interactivePlatformSprite = new Sprite(assets.interactivePlatformAssets.atlasRegion);

        interactivePlatformSprite.setSize(width / PPM, height / PPM);
        interactivePlatformSprite.setPosition(body.getPosition().x - width / 2 / PPM, body.getPosition().y - height / 2 / PPM);
    }

    public abstract void update(float delta);
    public abstract boolean isDestroyed();

    public void draw(SpriteBatch batch) {
       interactivePlatformSprite.draw(batch);
    }

    protected void attachSprite() {
        interactivePlatformSprite.setPosition(body.getPosition().x - width / 2 / PPM, body.getPosition().y - height / 2 / PPM);
    }

    public void movePlatform() {
        body.setLinearVelocity(vX, vY);
    }

    public Body getBody() {
        return body;
    }

    public float getvX() {
        return vX;
    }

    public float getvY() {
        return vY;
    }
}
