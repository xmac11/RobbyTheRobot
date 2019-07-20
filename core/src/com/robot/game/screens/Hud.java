package com.robot.game.screens;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
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

    private Texture lives; // this will become a TextureRegion when finalized
    private BitmapFont font;
    private GlyphLayout glyphLayout;

    public Hud(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.gameData = playScreen.getGameData();
        this.hudViewport = new ExtendViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);

        this.frame = Assets.getInstance().hudAssets.frame;
        this.greenBar = Assets.getInstance().hudAssets.greenBar;
        this.redBar = Assets.getInstance().hudAssets.redBar;
        this.lives = Assets.getInstance().hudAssets.lives;
        this.font = Assets.getInstance().fontAssets.font;
        this.glyphLayout = Assets.getInstance().fontAssets.glyphLayout;
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

        // draw lives image
        batch.draw(lives,
                hudViewport.getWorldWidth() - (PADDING + 2.5f * LIVES_WIDTH) / PPM,
                hudViewport.getWorldHeight() - (PADDING + LIVES_HEIGHT) / PPM,
                LIVES_WIDTH / PPM,
                LIVES_HEIGHT / PPM);

        // draw lives font (label)
        font.draw(batch,
                "x" + gameData.getLives(),
                hudViewport.getWorldWidth() - PADDING / PPM - glyphLayout.width / 2,
                hudViewport.getWorldHeight() - PADDING / PPM - glyphLayout.height / 2,
                /*LIVES_WIDTH / PPM*/0,
                Align.center,
                false);

//        System.out.println(glyphLayout.width + " " + glyphLayout.height);
    }

    public Viewport getHudViewport() {
        return hudViewport;
    }
}