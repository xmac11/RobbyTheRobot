package com.robot.game.interactiveObjects;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.TimeUtils;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class Trampoline extends Sprite {

    private PlayScreen playScreen;
    private boolean activated;
    private float startTimeAnim;
    private float elapsedAnim;


    public Trampoline(PlayScreen playScreen, Body body, FixtureDef fixtureDef) {
        this.playScreen = playScreen;
        body.createFixture(fixtureDef).setUserData(this);

        setRegion(playScreen.getAssets().trampolineAssets.trampolineFull);
        setSize(TRAMPOLINE_WIDTH / PPM, TRAMPOLINE_HEIGHT / PPM);
        setPosition(body.getPosition().x - TRAMPOLINE_WIDTH / 2 / PPM, body.getPosition().y - (TRAMPOLINE_HEIGHT / 2 - 2) / PPM);
    }

    @Override
    public void draw(Batch batch) {

        if(activated) {
            elapsedAnim = (TimeUtils.nanoTime() - startTimeAnim) * MathUtils.nanoToSec;
            setRegion(playScreen.getAssets().trampolineAssets.trampolineHalf);

            if(elapsedAnim > 0.075f) {
                activated = false;
                elapsedAnim = 0;
                setRegion(playScreen.getAssets().trampolineAssets.trampolineFull);
            }
        }

        super.draw(batch);
    }

    public void setStartTimeAnim(float startTimeAnim) {
        this.startTimeAnim = startTimeAnim;
    }

    public void setActivated(boolean activated) {
        this.activated = activated;
    }

    public void playSoundEffect() {
        if(!playScreen.isMuted()) {
            playScreen.getAssets().musicAssets.trampolineSound.play(0.3f);
        }
    }
}
