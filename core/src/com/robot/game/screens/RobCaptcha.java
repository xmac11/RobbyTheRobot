package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;

import static com.robot.game.util.Constants.*;
import static com.robot.game.util.Constants.PPM;

public class RobCaptcha extends ScreenAdapter {

    private RobotGame game;
    private Viewport viewport;
    private int levelID;
    private BitmapFont font;

    private Stage stage;
    private Image captchaPanel;
    private Image clickCaptcha;
    private TextButton returnButton;
    private Sprite checkSprite;
    private boolean checkClicked;
    private boolean returnClicked;

    private float checkStartTime;
    private float checkElapsed;

    public RobCaptcha(RobotGame game) {
        this.game = game;
        this.levelID = game.getCheckpointData().getLevelID();
        this.font = game.getAssets().hudFontAssets.hudFont;
    }

    @Override
    public void show() {
        Gdx.app.log("RobCaptcha", "show");
        this.viewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.stage = new Stage(viewport, game.getBatch());

        // image
        this.captchaPanel = new Image(game.getAssets().mainMenuAssets.robCaptcha);
        captchaPanel.setSize(captchaPanel.getWidth() / 2 / PPM, captchaPanel.getHeight() / 2 / PPM); // was drawn twice the actual size for better resolution
        captchaPanel.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, Align.center);

        // create buttons
        createButtons();

        // add listeners
        addListeners();

        // check sprite
        this.checkSprite = new Sprite();
        checkSprite.setSize(60f / 2 / PPM, 80f / 2 / PPM);
        checkSprite.setPosition(clickCaptcha.getX() - 1.5f / PPM, clickCaptcha.getY() - 5 / PPM);

        // add actors
        stage.addActor(captchaPanel);
        stage.addActor(clickCaptcha);
        stage.addActor(returnButton);

        // set InputProcessor
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // clear game screen
        Gdx.gl.glClearColor(0f / 255, 139f / 255, 139f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // draw stage
        stage.act();
        stage.draw();

        // captcha button was clicked, draw check animation and start game
        if(checkClicked) {

            if(checkElapsed <= game.getAssets().mainMenuAssets.checkAnimation.getAnimationDuration()) {

                game.getBatch().begin();
                checkSprite.setRegion(game.getAssets().mainMenuAssets.checkAnimation.getKeyFrame(checkElapsed));
                checkSprite.draw(game.getBatch());
                game.getBatch().end();

                // update time
                checkElapsed = (TimeUtils.nanoTime() - checkStartTime) * MathUtils.nanoToSec;
            }
            else {
                // start game
                this.dispose();
                loadLevel();
            }
        }
        // return button clicked
        else if(returnClicked) {
            this.dispose();
            game.setScreen(new MenuScreen(game));
        }
    }

    private void createButtons() {
        // click button
        this.clickCaptcha = new Image(game.getAssets().mainMenuAssets.clickCaptcha);
        clickCaptcha.setSize(clickCaptcha.getWidth() / 2 / PPM, clickCaptcha.getHeight() / 2 / PPM);
        clickCaptcha.setPosition(captchaPanel.getX() + 42 / PPM, captchaPanel.getY() + 44 / PPM, Align.center);

        // return button

        // return GlyphLayout
        GlyphLayout returnGlyph = new GlyphLayout();
        returnGlyph.setText(font, "RETURN");
        // add font to style
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        this.returnButton = new TextButton("RETURN", style);
        returnButton.setSize(returnGlyph.width, returnGlyph.height);
        returnButton.setPosition(viewport.getWorldWidth() / 2 + captchaPanel.getWidth() / 2.5f, viewport.getWorldHeight() / 2 - captchaPanel.getHeight() / 1.3f, Align.center);
        returnButton.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white

        Gdx.app.log("RobCaptcha", "Buttons were created");
    }

    private void addListeners() {
        // captcha
        clickCaptcha.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("RobCaptcha", "Clicked CAPTCHA button. Game starting...");
                Gdx.input.setInputProcessor(null);
                checkClicked = true;
                checkStartTime = TimeUtils.nanoTime();
            }
        });

        // return button
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("RobCaptcha", "Clicked RETURN button. Returning to MainMenu");
                Gdx.input.setInputProcessor(null);
                returnClicked = true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("RobCaptcha", "Entered RETURN button");
                returnButton.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1); // orange
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.app.log("RobCaptcha", "Exited RETURN button");
                returnButton.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white
            }


        });
        Gdx.app.log("RobCaptcha", "Listeners were added to buttons");
    }

    private void loadLevel() {
        switch(levelID) {
            case 1:
                game.setScreen(new ScreenLevel1(game));
                break;
            case 2:
                game.setScreen(new ScreenLevel2(game));
                break;
            case 3:
                game.setScreen(new ScreenLevel3(game));
                break;
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("RobCaptcha", "resize");
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        Gdx.app.log("RobCaptcha", "dispose");
        stage.dispose();
        setToNull();
    }

    private void setToNull() {
        viewport = null;
        checkSprite = null;
        font = null;
        Gdx.app.log("RobCaptcha", "Objects were set to null");
    }
}
