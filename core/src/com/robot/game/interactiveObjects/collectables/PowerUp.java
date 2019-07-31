package com.robot.game.interactiveObjects.collectables;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class PowerUp extends Collectable {

    private Sprite powerupSprite;
    private boolean fullHeal;
    private float width;
    private float height;

    public PowerUp(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);
        this.fullHeal = (boolean) object.getProperties().get("fullHeal");

        if(fullHeal) {
            this.width = FULL_HEAL_WIDTH;
            this.height = FULL_HEAL_HEIGHT;
        }
        else {
            this.width = POWERUP_WIDTH;
            this.height = POWERUP_HEIGHT;
        }

        this.powerupSprite = new Sprite(assets.collectableAssets.powerup);

        // set the size of the bat sprite
        powerupSprite.setSize(width / PPM, height / PPM);

        // attach sprite to body
        powerupSprite.setPosition(body.getPosition().x - width / 2 / PPM, body.getPosition().y - height / 2 / PPM);

    }

    @Override
    public void draw(Batch batch) {
        powerupSprite.draw(batch);
    }

    public boolean isFullHeal() {
        return fullHeal;
    }
}
