package com.robot.game.interactiveObjects.platforms;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;

import static com.robot.game.util.constants.Constants.PPM;

public abstract class InteractivePlatform {

    private Sprite sprite;
    protected PlayScreen playScreen;
    protected Assets assets;
    protected World world;
    protected Body body;
    protected float vX;
    protected float vY;
    protected  float width;
    protected  float height;

    protected InteractivePlatform(PlayScreen playScreen, Body body, MapObject object) {
        this.playScreen = playScreen;
        this.assets = playScreen.getAssets();
        this.world = playScreen.getWorld();
        this.body = body;
        this.vX = (float) object.getProperties().get("vX");
        this.vY = (float) object.getProperties().get("vY");
        //body.setActive(false);

        this.width = (float) object.getProperties().get("width");
        this.height = (float) object.getProperties().get("height");

        this.sprite = new Sprite(assets.interactivePlatformAssets.interactivePlatform);

        sprite.setSize(width / PPM, height / PPM);
        sprite.setPosition(body.getPosition().x - width / 2 / PPM, body.getPosition().y - height / 2 / PPM);
    }

    public abstract void update(float delta);

    public void draw(SpriteBatch batch) {
        // attach platform sprite to body
        sprite.setPosition(body.getPosition().x - width / 2 / PPM, body.getPosition().y - height / 2 / PPM);
        sprite.draw(batch);
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

    public void setToNull() {
        sprite = null;
        playScreen = null;
        Gdx.app.log("InteractivePlatform", "Objects were set to null");
    }
}
