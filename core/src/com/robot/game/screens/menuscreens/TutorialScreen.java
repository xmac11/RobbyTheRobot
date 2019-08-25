package com.robot.game.screens.menuscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;

import static com.robot.game.util.constants.Constants.*;

public class TutorialScreen extends ScreenAdapter {

    private RobotGame game;
    private Viewport viewport;
    private Stage stage;
    private BitmapFont font;
    private TextButton returnButton;
    private boolean returnClicked;

    public TutorialScreen(RobotGame game) {
        this.game = game;
        this.font = game.getAssets().hudFontAssets.hudFont;
    }

    @Override
    public void show() {
        Gdx.app.log("TutorialScreen", "show");
        this.viewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.stage = new Stage(viewport, game.getBatch());

        // image
        Image tutorialImage = new Image(game.getAssets().mainMenuAssets.tutorial);
        tutorialImage.setSize(tutorialImage.getWidth() / 2 / PPM, tutorialImage.getHeight() / 2 / PPM);
        tutorialImage.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 + 24 / PPM, Align.center);

        // create return button
        createReturnButton();

        // add listeners
        addListeners();

        // add actors
        stage.addActor(tutorialImage);
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

        // if return button was clicked
        if(returnClicked) {
            this.dispose();
            game.setScreen(new MenuScreen(game));
        }
    }

    private void createReturnButton() {
        // return GlyphLayout
        GlyphLayout returnGlyph = new GlyphLayout();
        returnGlyph.setText(font, "RETURN");

        // add font to style
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        this.returnButton = new TextButton("RETURN", style);
        returnButton.setSize(returnGlyph.width, returnGlyph.height);
        returnButton.setPosition(viewport.getWorldWidth() - returnGlyph.width / 2 - 16 / PPM,16 / PPM, Align.center);
        returnButton.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white

        Gdx.app.log("TutorialScreen", "Return button were created");
    }

    private void addListeners() {
        // return button
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("TutorialScreen", "Clicked RETURN button. Returning to MainMenu");
                Gdx.input.setInputProcessor(null);
                returnClicked = true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("TutorialScreen", "Entered RETURN button");
                returnButton.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1); // orange
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.app.log("TutorialScreen", "Exited RETURN button");
                returnButton.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white
            }


        });
        Gdx.app.log("TutorialScreen", "Listeners were added to return button");
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("TutorialScreen", "resize");
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        Gdx.app.log("TutorialScreen", "dispose");
        stage.dispose();
        setToNull();
    }

    private void setToNull() {
        viewport = null;
        font = null;
        Gdx.app.log("TutorialScreen", "Objects were set to null");
    }
}
