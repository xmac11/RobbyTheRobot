package com.robot.game.interactiveObjects.collectables;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class PowerUp extends Collectable {

    private boolean fullHeal;

    public PowerUp(PlayScreen playScreen, Body body, FixtureDef fixtureDef, MapObject object) {
        super(playScreen, body, fixtureDef, object);
        this.fullHeal = (boolean) object.getProperties().get("fullHeal");

        float width;
        float height;
        if(fullHeal) {
            width = FULL_HEAL_WIDTH;
            height = FULL_HEAL_HEIGHT;
        }
        else {
            width = POWERUP_WIDTH;
            height = POWERUP_HEIGHT;
        }

        super.sprite = new Sprite(assets.collectableAssets.powerup);

        // set the size of the bat sprite
        sprite.setSize(width / PPM, height / PPM);

        // attach sprite to body
        sprite.setPosition(body.getPosition().x - width / 2 / PPM, body.getPosition().y - height / 2 / PPM);
    }

    public boolean isFullHeal() {
        return fullHeal;
    }

    @Override
    public void playSoundEffect() {
        if(!playScreen.isMuted()) {
            assets.soundAssets.powerupSound.play(0.4f);
        }
    }
}
