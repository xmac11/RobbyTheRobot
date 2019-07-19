package com.robot.game.screens;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.util.Assets;
import com.robot.game.util.GameData;

import static com.robot.game.util.Constants.*;

public class Hud extends Actor {

    private PlayScreen playScreen;
    private GameData gameData;
    private Viewport hudViewport;

//    private TextureRegion frame;
//    private TextureRegion greenBar;
//    private TextureRegion redBar;

    public Image frame;
    public Image greenBar;
    public Image redBar;

    public Hud(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.gameData = playScreen.getGameData();
        this.hudViewport = new ExtendViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);

        this.frame = Assets.getInstance().healthBarAssets.frame;
        this.greenBar = Assets.getInstance().healthBarAssets.greenBar;
        this.redBar = Assets.getInstance().healthBarAssets.redBar;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {

        batch.setProjectionMatrix(hudViewport.getCamera().combined);

        // draw frame
        /*batch.draw(frame,
                PADDING / PPM,
                hudViewport.getWorldHeight() - (FRAME_OFFSET + PADDING) / PPM,
                FRAME_WIDTH / PPM,
                FRAME_HEIGHT / PPM);

        // draw bar
        batch.draw(gameData.getHealth() >= 50 ? greenBar : redBar,
                (BAR_OFFSET_X + PADDING) / PPM,
                hudViewport.getWorldHeight() - (BAR_OFFSET_Y + PADDING) / PPM,
                (BAR_WIDTH * gameData.getHealth() / 100) / PPM,
                BAR_HEIGHT / PPM);*/

        frame.setBounds(PADDING / PPM,
                hudViewport.getWorldHeight() - (FRAME_OFFSET + PADDING) / PPM,
                FRAME_WIDTH / PPM,
                FRAME_HEIGHT / PPM);
        frame.draw(batch, 1);

        if(gameData.getHealth() >= 50) {
            greenBar.setBounds((BAR_OFFSET_X + PADDING) / PPM,
                    hudViewport.getWorldHeight() - (BAR_OFFSET_Y + PADDING) / PPM,
                    (BAR_WIDTH * gameData.getHealth() / 100) / PPM,
                    BAR_HEIGHT / PPM);
            greenBar.draw(batch, 1);
        }
        else {
            redBar.setBounds((BAR_OFFSET_X + PADDING) / PPM,
                hudViewport.getWorldHeight() - (BAR_OFFSET_Y + PADDING) / PPM,
                (BAR_WIDTH * gameData.getHealth() / 100) / PPM,
                BAR_HEIGHT / PPM);

            redBar.draw(batch, 1);
        }
    }

    public Viewport getHudViewport() {
        return hudViewport;
    }
}
