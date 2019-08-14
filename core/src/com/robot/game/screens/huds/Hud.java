package com.robot.game.screens.huds;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.screens.PlayScreen;
import com.robot.game.util.Assets;
import com.robot.game.util.checkpoints.CheckpointData;

import static com.robot.game.util.Constants.*;

public class Hud {

    private PlayScreen playScreen;
    private CheckpointData checkpointData;
    private Viewport hudViewport;
    private TextureRegion frame;
    private TextureRegion greenBar;
    private TextureRegion redBar;

    private TextureRegion lives;
    private BitmapFont font;
    private BitmapFont hpFont;
    private GlyphLayout scoreGlyphLayout;
    private GlyphLayout livesGlyphLayout;

    // Pause panel
    protected Stage stage;
    protected Image pausePanel;

    public Hud(PlayScreen playScreen) {
        this.playScreen = playScreen;
        Assets assets = playScreen.getAssets();
        this.checkpointData = playScreen.getCheckpointData();
        this.hudViewport = new ExtendViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT * 1.35f / PPM);

        this.frame = assets.hudAssets.frame;
        this.greenBar = assets.hudAssets.greenBar;
        this.redBar = assets.hudAssets.redBar;
        this.lives = assets.hudAssets.lives;
        this.font = assets.fontAssets.font;
        this.hpFont = assets.smallFontAssets.smallFont;
        this.scoreGlyphLayout = assets.hudAssets.scoreGlyphLayout;
        this.livesGlyphLayout = assets.hudAssets.livesGlyphLayout;


        this.stage = new Stage(hudViewport, playScreen.getGame().getBatch());
        this.pausePanel = new Image(assets.pausePanelAssets.pausePanel);
        pausePanel.setSize(hudViewport.getWorldWidth() / 2, hudViewport.getWorldHeight() / 2);
        pausePanel.setPosition(hudViewport.getWorldWidth() / 2 - pausePanel.getWidth() / 2, hudViewport.getWorldHeight() / 2 - pausePanel.getHeight() / 2);
        stage.addActor(pausePanel);
    }

    public void draw(SpriteBatch batch) {

        batch.setProjectionMatrix(hudViewport.getCamera().combined);
        batch.begin();

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

        // draw HP font within the health bar
        hpFont.setColor(/*233f / 255, 233f / 255, 233f / 255, 1*/ Color.WHITE); // 70 / 233 / WHITE
        hpFont.draw(batch,
                "HP: " + checkpointData.getHealth(),
                (PADDING + BAR_OFFSET_X + BAR_WIDTH / 2) / PPM,
                    hudViewport.getWorldHeight() - (PADDING + BAR_OFFSET_Y / 2.2f) / PPM,
                0,
                Align.center,
                false);

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
        batch.end();

        if(playScreen.isPaused()) {
            stage.draw();
        }
    }

    public Viewport getHudViewport() {
        return hudViewport;
    }

}