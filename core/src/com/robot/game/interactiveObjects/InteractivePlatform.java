package com.robot.game.interactiveObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.XmlReader;
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.MAP_HEIGHT;
import static com.robot.game.util.Constants.PPM;

public abstract class InteractivePlatform extends Sprite {

    protected World world;
    protected Body body;
    protected float vX;
    protected float vY;
    protected  float width;
    protected  float height;
    protected Sprite interactivePlatformSprite;

    protected InteractivePlatform(World world, Body body, MapObject object) {
        this.world = world;
        this.body = body;
        this.vX = (float) object.getProperties().get("vX");
        this.vY = (float) object.getProperties().get("vY");
        //body.setActive(false);

        this.width = (float) object.getProperties().get("width");
        this.height = (float) object.getProperties().get("height");

        this.interactivePlatformSprite = new Sprite(Assets.getInstance().interactivePlatformAssets.atlasRegion);

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
