package com.robot.game.interactiveObjects.collectables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;
import org.json.simple.JSONObject;

public abstract class Collectable {

    protected Sprite sprite;
    protected PlayScreen playScreen;
    protected Assets assets;
    private CollectableHandler collectableHandler;
    private World world;
    private Body body;
    private MapObject mapObject;
    private boolean flagToCollect;
    private boolean destroyed;
    protected boolean isTorch;

    public Collectable(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        this.playScreen = playScreen;
        this.assets = playScreen.getAssets();
        this.collectableHandler = playScreen.getCollectableHandler();
        this.world = playScreen.getWorld();
        this.body = body;
        this.mapObject = object;
        this.isTorch = (object.getProperties().containsKey("torch"));
        body.createFixture(fixtureDef).setUserData(this);

        // put collectables to sleep to reduce CPU cost
        body.setAwake(false);
    }

    public abstract void playSoundEffect();

    public void draw(Batch batch) {
        sprite.draw(batch);
    }

    public void update(float delta) {
        if(flagToCollect && !destroyed) {
            destroyBody();
            destroyed = true;
        }
    }

    public void setFlagToCollect() {
        this.flagToCollect = true;
    }

    protected void destroyBody() {
        world.destroyBody(body);
        Gdx.app.log("Collectable", "Body destroyed");

        playScreen.getCollectables().removeValue(this, false);
        Gdx.app.log("Collectable", "Collectable was removed from array");
    }

    public Body getBody() {
        return body;
    }

    public MapObject getMapObject() {
        return mapObject;
    }

    public void addToDisableSpawning(int collectableID) {
        collectableHandler.getItemsToDisableSpawning().add(collectableID);
    }

    public CollectableHandler getCollectableHandler() {
        return collectableHandler;
    }

    public boolean isTorch() {
        return isTorch;
    }

    public void setToNull() {
        sprite = null;
        collectableHandler = null;
        mapObject = null;
        playScreen = null;
        Gdx.app.log("Collectable", "Objects were set to null");
    }
}
