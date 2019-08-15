package com.robot.game.screens.huds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.screens.MenuScreen;
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
    private BitmapFont hudFont;
    private BitmapFont hpFont;
    private GlyphLayout scoreGlyphLayout;
    private GlyphLayout livesGlyphLayout;

    // Pause panel
    private Stage stage;
    private Image pausePanel;
    private TextButton toResumeButton;
    private TextButton toMenuButton;
    private BitmapFont pauseFont;

    private int selection;
    private int n;

    public Hud(PlayScreen playScreen) {
        this.playScreen = playScreen;
        Assets assets = playScreen.getAssets();
        this.checkpointData = playScreen.getCheckpointData();
        this.hudViewport = new ExtendViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);

        this.frame = assets.hudAssets.frame;
        this.greenBar = assets.hudAssets.greenBar;
        this.redBar = assets.hudAssets.redBar;
        this.lives = assets.hudAssets.lives;
        this.hudFont = assets.hudFontAssets.hudFont;
        this.hpFont = assets.hpFontAssets.hpFont;

        // GlyphLayout for alignment
        this.scoreGlyphLayout = new GlyphLayout();
        scoreGlyphLayout.setText(hudFont, "SCORE");

        // GlyphLayout for alignment
        this.livesGlyphLayout = new GlyphLayout();
        String text = "x3";
        livesGlyphLayout.setText(hudFont, text);

        // pause panel
        this.stage = new Stage(hudViewport, playScreen.getGame().getBatch());
        this.pauseFont = assets.panelFontAssets.panelFont;
        this.pausePanel = new Image(assets.pausePanelAssets.pausePanel);
        pausePanel.setSize(hudViewport.getWorldWidth() / 2, hudViewport.getWorldHeight() / 2);
        pausePanel.setPosition(hudViewport.getWorldWidth() / 2 - pausePanel.getWidth() / 2, hudViewport.getWorldHeight() / 2 - pausePanel.getHeight() / 2);

        this.n = 2; // number of buttons

        // create buttons
        createButtons();

        // add listeners to buttons
        addListeners();

        stage.addActor(pausePanel);
        stage.addActor(toMenuButton);
        stage.addActor(toResumeButton);
    }

    private void update() {
        // process input
        processInput();

        // update color of buttons
        switch(selection) {
            case 0:
                toResumeButton.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1); // orange
                toMenuButton.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white
                break;
            case 1:
                toResumeButton.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white
                toMenuButton.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1); // orange
                break;
        }
    }

    private void processInput() {
        // update selection
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selection = (selection - 1 + n) % n;
            Gdx.app.log("Hud", "selection = " + selection);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selection = (selection + 1 + n) % n;
            Gdx.app.log("Hud", "selection = " + selection);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            handleSelection();
        }
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
        hudFont.setColor(255f / 255, 192f / 255, 43f / 255, 1);
        hudFont.draw(batch,
                "SCORE",
                1.5f * PADDING / PPM + scoreGlyphLayout.width / 2,
                hudViewport.getWorldHeight() - (FRAME_OFFSET + 2 * PADDING) / PPM,
                0,
                Align.center,
                false);

        // draw score (value)
        hudFont.setColor(Color.WHITE);
        hudFont.draw(batch,
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
        hudFont.setColor(Color.WHITE);
        hudFont.draw(batch,
                "x" + checkpointData.getLives(),
                hudViewport.getWorldWidth() - PADDING / PPM - livesGlyphLayout.width / 2,
                hudViewport.getWorldHeight() - PADDING / PPM - livesGlyphLayout.height / 2,
                /*LIVES_WIDTH / PPM*/0,
                Align.center,
                false);
        batch.end();

        if(playScreen.isPaused()) {
            this.update();

            stage.act();
            stage.draw();
        }
    }

    private void createButtons() {
        // resume GlyphLayout
        GlyphLayout resumeGlyph = new GlyphLayout();
        resumeGlyph.setText(pauseFont, "RESUME");

        // menu GlyphLayout
        GlyphLayout menuGlyph = new GlyphLayout();
        menuGlyph.setText(pauseFont, "MENU");

        // add font to style
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = pauseFont;

        this.toResumeButton = new TextButton("RESUME", style);
        toResumeButton.setPosition(resumeGlyph.width, resumeGlyph.height);
        toResumeButton.setPosition(hudViewport.getWorldWidth() / 2, hudViewport.getWorldHeight() / 2, Align.center);

        this.toMenuButton = new TextButton("MENU", style);
        toMenuButton.setSize(menuGlyph.width, menuGlyph.height);
        toMenuButton.setPosition(hudViewport.getWorldWidth() / 2, hudViewport.getWorldHeight() / 2 - 48 / PPM, Align.center);

        Gdx.app.log("Hud", "Pause panel buttons were created");
    }

    private void addListeners() {
        // toResumeButton
        toResumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Hud", "Clicked toResumeButton");
                selection = 0;
                handleSelection();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("Hud", "Entered play button");
                selection = 0;
            }
        });


        // toMenuButton
        toMenuButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("Hud", "Clicked toMenuButton");
                selection = 1;
                handleSelection();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("Hud", "Entered toMenuButton");
                selection = 1;
            }
        });

        Gdx.app.log("Hud", "Listeners were added to buttons");
    }

    private void handleSelection() {

        switch(selection) {
            // resume
            case 0:
                Gdx.app.log("Hud", "Game was resumed from pause panel");
                playScreen.setPaused(false);

                // update boolean for tiled animation
                playScreen.getMapRenderer().setMapAnimationActive(true);

                // update input processor
                playScreen.updateInputProcOnPauseOrResume();
                break;

            // menu
            case 1:
                Gdx.app.log("Hud", "MenuScreen was set from pause panel");
                playScreen.dispose();
                playScreen.getGame().setScreen(new MenuScreen(playScreen.getGame()));
                break;
        }
    }

    public Viewport getHudViewport() {
        return hudViewport;
    }

    public Stage getStage() {
        return stage;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }
}