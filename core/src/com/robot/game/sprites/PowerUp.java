package com.robot.game.sprites;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.ScreenLevel1;
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.*;
import static com.robot.game.util.Constants.PPM;

public class PowerUp extends Collectable {

    private Sprite powerupSprite;
    private boolean fullHeal;
    private float width;
    private float height;

    public PowerUp(ScreenLevel1 screenLevel1, Body body, FixtureDef fixtureDef, MapObject object) {
        super(screenLevel1, body, fixtureDef, object);
        this.fullHeal = (boolean) object.getProperties().get("fullHeal");

        if(fullHeal) {
            this.width = FULL_HEAL_WIDTH;
            this.height = FULL_HEAL_HEIGHT;
        }
        else {
            this.width = POWERUP_WIDTH;
            this.height = POWERUP_HEIGHT;
        }

        this.powerupSprite = new Sprite(Assets.getInstance().collectableAssets.powerup);

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
