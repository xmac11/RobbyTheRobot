package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;

import static com.robot.game.util.constants.Constants.*;

public class GameCompletedScreen extends ScreenAdapter {

    private PlayScreen playScreen;
    private RobotGame game;
    private Viewport viewport;
    private Stage stage;
    private BitmapFont bigFont;
    private BitmapFont font;
    private BitmapFont smallFont;
    private Music music;

    public GameCompletedScreen(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.game = playScreen.getGame();
        this.bigFont = game.getAssets().panelBigFontAssets.panelBigFont;
        this.font = game.getAssets().panelFontAssets.panelFont;
        this.smallFont = game.getAssets().feedbackFontAssets.feedbackFont;
        this.music = game.getAssets().gameCompletedAssets.gameCompletedMusic;
    }

    @Override
    public void show() {
        Gdx.app.log("GameCompletedScreen", "show");
        this.viewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.stage = new Stage(viewport, game.getBatch());

        // game completed label
        Label.LabelStyle styleGameCompleted = new Label.LabelStyle(bigFont, new Color(0f / 255, 94f / 255, 94f / 255, 1)); // title color
        Label gameCompleted = new Label("GAME COMPLETED!", styleGameCompleted);
        gameCompleted.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 + 176 / PPM, Align.center);

        // game completed image
        Image image = new Image(game.getAssets().gameCompletedAssets.gameCompletedPanel);
        image.setSize(image.getWidth() / 2 / PPM, image.getHeight() / 2 / PPM);
        image.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, Align.center);

        // score label
        Label.LabelStyle styleScore = new Label.LabelStyle(font, new Color(0f / 255, 94f / 255, 94f / 255, 1)); // title color
        Label score = new Label("SCORE: " + playScreen.getScoreOnGameEnd(), styleScore);
        score.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 - 164 / PPM, Align.center);

        // press any key label
        Label.LabelStyle styleAnyKey = new Label.LabelStyle(smallFont, new Color(238f / 255, 232f / 255, 170f / 255, 1)); // white
        Label anyKey;
        if(playScreen.isOnAndroid()) {
            anyKey = new Label("Tap screen to return to menu...", styleAnyKey);
        }
        else {
            anyKey = new Label("Press any key to return to menu...", styleAnyKey);
        }
        anyKey.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 - 192 / PPM, Align.center);

        // add actors
        stage.addActor(gameCompleted);
        stage.addActor(image);
        stage.addActor(score);
        stage.addActor(anyKey);

        // set InputProcessor
        Gdx.input.setInputProcessor(stage);

        if(!playScreen.isMuted()) {
            music.setLooping(true);
            music.play();
        }
    }

    @Override
    public void render(float delta) {
        // process input
        processInput();

        // clear game screen
        Gdx.gl.glClearColor(0f / 255, 139f / 255, 139f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    private void processInput() {
        if(Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.ANY_KEY)) {
            // stop music
            music.stop();

            // set MenuScreen
            this.dispose();
            game.setScreen(new MenuScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameCompletedScreen", "resize");
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameCompletedScreen", "dispose");
        stage.dispose();
    }
}
