package com.robot.game.entities.abstractEnemies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.entities.Robot;
import com.robot.game.screens.playscreens.PlayScreen;
import com.robot.game.Assets;
import com.robot.game.interfaces.Damaging;

public abstract class Enemy implements Damaging {

    protected Sprite sprite;
    protected PlayScreen playScreen;
    protected Assets assets;
    protected Robot robot;

    // Box2D
    protected World world;
    protected Body body;
    protected FixtureDef fixtureDef;
    protected MapObject object;

    protected boolean flagToKill;
    protected boolean destroyed;
    protected boolean dead; // only used for bat (otherwise continues to move up and down after dead)

    // animation
    protected float startTimeAnim;
    protected float elapsedAnim;
    protected float deadStartTime;
    protected float deadElapsed;

    // when dead
    protected boolean flagToChangeMask;

    public Enemy(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        this.sprite = new Sprite();
        this.playScreen = playScreen;
        this.assets = playScreen.getAssets();
        this.robot = playScreen.getRobot();
        this.world = playScreen.getWorld();
        this.body = body;
        this.fixtureDef = fixtureDef;
        if(!object.getProperties().containsKey("noRestitution"))
            fixtureDef.restitution = 0.5f;

        // animation
        startTimeAnim = TimeUtils.nanoTime();
    }

    public abstract void update(float delta);
    public abstract void draw(Batch batch);

    @Override
    public abstract int getDamage();

    // reverse velocity of an enemy
    protected void reverseVelocity(boolean reverseVx, boolean reverseVy) {
        if(reverseVx)
            body.setLinearVelocity(-body.getLinearVelocity().x, body.getLinearVelocity().y);
        if(reverseVy)
            body.setLinearVelocity(body.getLinearVelocity().x, -body.getLinearVelocity().y);
    }

    public void setFlagToKill() {
        this.flagToKill = true;
        // keep track of the time enemy was killed
        deadStartTime = TimeUtils.nanoTime();
    }

    protected void destroyBody() {
        world.destroyBody(body);
        Gdx.app.log("Enemy", "Body destroyed");

        playScreen.getEnemies().removeValue(this, false);
        Gdx.app.log("Enemy", "Enemy was removed from array");
    }

    public Body getBody() {
        return body;
    }

    public void setFlagToChangeMask(boolean flagToChangeMask) {
        this.flagToChangeMask = flagToChangeMask;
    }

    public boolean isDead() {
        return dead;
    }

    public void setDead(boolean dead) {
        this.dead = dead;
    }

    public Robot getRobot() {
        return robot;
    }

    public float getElapsedAnim() {
        return elapsedAnim;
    }

    public void playSoundEffect() {
        if(!playScreen.isMuted()) {
            assets.soundAssets.stepOnEnemySound.play(0.3f);
        }
    }

    public void setToNull() {
        sprite = null;
        robot = null;
        object = null;
        playScreen = null;
        Gdx.app.log("Enemy", "Objects were set to null");
    }
}
