package com.robot.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.util.Assets;
import com.robot.game.util.CheckpointData;

import static com.robot.game.util.Constants.*;

public class Hud {

    private ScreenLevel1 screenLevel1;
    private Assets assets;
    private CheckpointData checkpointData;
    private Viewport hudViewport;
    private TextureRegion frame;
    private TextureRegion greenBar;
    private TextureRegion redBar;

    private Texture lives; // this will become a TextureRegion when finalized
    private BitmapFont font;
    private GlyphLayout scoreGlyphLayout;
    private GlyphLayout livesGlyphLayout;

    public Hud(ScreenLevel1 screenLevel1) {
        this.screenLevel1 = screenLevel1;
        this.assets = screenLevel1.getAssets();
        this.checkpointData = screenLevel1.getCheckpointData();
        this.hudViewport = new ExtendViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT * 1.35f / PPM);

        this.frame = assets.hudAssets.frame;
        this.greenBar = assets.hudAssets.greenBar;
        this.redBar = assets.hudAssets.redBar;
        this.lives = assets.hudAssets.lives;
        this.font = assets.fontAssets.font;
        this.scoreGlyphLayout = assets.hudAssets.scoreGlyphLayout;
        this.livesGlyphLayout = assets.hudAssets.livesGlyphLayout;

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
        batch.draw(checkpointData.getHealth() >= 50 ? greenBar : redBar,
                (BAR_OFFSET_X + PADDING) / PPM,
                hudViewport.getWorldHeight() - (BAR_OFFSET_Y + PADDING) / PPM,
                (BAR_WIDTH * checkpointData.getHealth() / 100) / PPM,
                BAR_HEIGHT / PPM);

        // draw score label (SCORE)
        font.setColor(255f / 255, 192f / 255, 43f / 255, 1);
        font.draw(batch,
                "SCORE",
                1.5f * PADDING / PPM + scoreGlyphLayout.width / 2,
                hudViewport.getWorldHeight() - (FRAME_OFFSET + 2 * PADDING) / PPM,
                0,
                Align.center,
                false);

        // draw score (value)
        font.setColor(Color.WHITE);
        font.draw(batch,
                String.valueOf(checkpointData.getScore()),
                3 * PADDING / PPM + scoreGlyphLayout.width,
                hudViewport.getWorldHeight() - (FRAME_OFFSET + 2 * PADDING) / PPM,
                0,
                Align.left,
                false);

        // draw lives image
        batch.draw(lives,
                hudViewport.getWorldWidth() - (PADDING + 2.5f * LIVES_WIDTH) / PPM,
                hudViewport.getWorldHeight() - (PADDING + LIVES_HEIGHT) / PPM,
                LIVES_WIDTH / PPM,
                LIVES_HEIGHT / PPM);

        // draw lives loadingScreenFont (label)
        font.setColor(Color.WHITE);
        font.draw(batch,
                "x" + checkpointData.getLives(),
                hudViewport.getWorldWidth() - PADDING / PPM - livesGlyphLayout.width / 2,
                hudViewport.getWorldHeight() - PADDING / PPM - livesGlyphLayout.height / 2,
                /*LIVES_WIDTH / PPM*/0,
                Align.center,
                false);

//        System.out.println(livesGlyphLayout.width + " " + livesGlyphLayout.height);
    }

    public Viewport getHudViewport() {
        return hudViewport;
    }

}