package com.robot.game.interactiveObjects.spikes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.*;

public class MovingSpike extends Spike {

    private Sprite sprite;
    private Sprite baseSpirte;
    private Sprite stickSpirte;
    private MapObject mapObject;
    private boolean inBalancePosition;
    private boolean attacking;
    private boolean horizontal;
    private int id;
    private float upperTranslationA;
    private float upperTranslationB;
    private float timeElapsed;
    private float attackPeriod;

    public MovingSpike(Body body, FixtureDef fixtureDef, MapObject object, ObjectMap<Integer, Array<Body>> jointMap, Assets assets) {
        super(body, fixtureDef, object);
        this.mapObject = object;
        this.sprite = new Sprite(assets.trapAssets.trapSpikes);
        this.baseSpirte = new Sprite(assets.trapAssets.trapBase);
        this.stickSpirte = new Sprite(assets.trapAssets.trapStick);
        this.inBalancePosition = true;
        this.horizontal = (object.getProperties().containsKey("horizontal"));

        this.upperTranslationA = (float) object.getProperties().get("upperTranslationA") / PPM;
        this.upperTranslationB = (float) object.getProperties().get("upperTranslationB") / PPM;

        this.attackPeriod = (float) object.getProperties().get("attackPeriod");

        this.id = (int) object.getProperties().get("prismatic");
        Array<Body> bodyArray = jointMap.get(id);

        if(bodyArray == null)
            bodyArray = new Array<>();

        bodyArray.add(body);
        // put body in the corresponding array of the HashMap
        jointMap.put((Integer) object.getProperties().get("prismatic"), bodyArray);

        // moving spikes
        if(horizontal) {
            // set the size as if it were vertical and rotate it when drawn
            sprite.setSize(80 / PPM, 32f / PPM);
            sprite.setOrigin(80f / 2 / PPM, 32f / 2 / PPM);
        }
        else {
            sprite.setSize(64f / PPM, 32f / PPM);
        }

        // base
        baseSpirte.setSize(32 / PPM, 8 / PPM);
        baseSpirte.setOrigin(32 / 2f / PPM, 8 / 2f / PPM);

    }

    public void draw(Batch batch) {
        // draw base
        baseSpirte.draw(batch);

        // attach moving spike sprite to body
        if(horizontal) {
            sprite.setPosition(body.getPosition().x - 80f / 2 / PPM, body.getPosition().y - 32f / 2 / PPM);
            sprite.setRotation(90);
        }
        else {
            sprite.setPosition(body.getPosition().x - 64f / 2 / PPM, body.getPosition().y - 32f / 2 / PPM);
        }

        sprite.draw(batch);
    }

    public boolean isInBalancePosition() {
        return inBalancePosition;
    }

    public void setInBalancePosition(boolean inBalancePosition) {
        this.inBalancePosition = inBalancePosition;
        Gdx.app.log("MovingSpike", "inBalancePosition = " + inBalancePosition + " - " +  this);
    }

    public int getId() {
        return id;
    }

    public float getUpperTranslationA() {
        return upperTranslationA;
    }

    public float getUpperTranslationB() {
        return upperTranslationB;
    }

    public float getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(float timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public boolean isAttacking() {
        return attacking;
    }

    public void setAttacking(boolean attacking) {
        this.attacking = attacking;
    }

    public Sprite getBaseSpirte() {
        return baseSpirte;
    }

    public Sprite getStickSpirte() {
        return stickSpirte;
    }

    public boolean isHorizontal() {
        return horizontal;
    }

    public float getAttackPeriod() {
        return attackPeriod;
    }
}
