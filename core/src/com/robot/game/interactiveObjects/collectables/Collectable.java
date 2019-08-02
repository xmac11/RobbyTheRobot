package com.robot.game.interactiveObjects.collectables;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;
import org.json.simple.JSONObject;

public abstract class Collectable extends Sprite {

    private PlayScreen playScreen;
    protected Assets assets;
    private CollectableHandler collectableHandler;
    private World world;
    private Body body;
    private MapObject object;
    private JSONObject temp;
    private boolean flagToCollect;

    public Collectable(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        this.playScreen = playScreen;
        this.assets = playScreen.getAssets();
        this.collectableHandler = playScreen.getCollectableHandler();
        this.world = playScreen.getWorld();
        this.body = body;
        this.object = object;
        this.temp = new JSONObject();
        body.createFixture(fixtureDef).setUserData(this);

        // put collectables to sleep to reduce CPU cost
        body.setAwake(false);
    }

    public void update(float delta) {
        if(flagToCollect) {
            destroyBody();
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

    public MapObject getObject() {
        return object;
    }

    public void addToDisableSpawning(int collectableID) {
        collectableHandler.getItemsToDisableSpawning().add(collectableID);
    }

    public CollectableHandler getCollectableHandler() {
        return collectableHandler;
    }
}
