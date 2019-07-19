package com.robot.game.screens;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.util.Assets;
import com.robot.game.util.GameData;

import static com.robot.game.util.Constants.*;

public class Hud {

    private PlayScreen playScreen;
    private GameData gameData;
    private Viewport hudViewport;
    private TextureRegion frame;
    private TextureRegion greenBar;
    private TextureRegion redBar;

    public Hud(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.gameData = playScreen.getGameData();
        this.hudViewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);

        this.frame = Assets.getInstance().healthBarAssets.frame;
        this.greenBar = Assets.getInstance().healthBarAssets.greenBar;
        this.redBar = Assets.getInstance().healthBarAssets.redBar;
    }

    public void draw(SpriteBatch batch) {

        batch.setProjectionMatrix(hudViewport.getCamera().combined);

        // draw frame
        batch.draw(frame,
                PADDING / PPM,
                hudViewport.getWorldHeight() - (FRAME_OFFSET + PADDING) / PPM,
                FRAME_WIDTH / PPM,
                FRAME_HEIGHT / PPM);

        // draw bar
        batch.draw(gameData.getHealth() >= 50 ? greenBar : redBar,
                (BAR_OFFSET_X + PADDING) / PPM,
                hudViewport.getWorldHeight() - (BAR_OFFSET_Y + PADDING) / PPM,
                (BAR_WIDTH * gameData.getHealth() / 100) / PPM,
                BAR_HEIGHT / PPM);
    }

    public Viewport getHudViewport() {
        return hudViewport;
    }
}
