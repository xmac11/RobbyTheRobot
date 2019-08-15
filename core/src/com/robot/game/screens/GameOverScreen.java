package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.util.Assets;
import com.robot.game.util.checkpoints.CheckpointData;

import static com.robot.game.util.Constants.*;

public class GameOverScreen extends ScreenAdapter {

    private RobotGame game;
    private PlayScreen playScreen;
    private CheckpointData checkpointData;
    private int levelID;
    private Assets assets;
    private Viewport viewport;
    private Stage stage;
    private BitmapFont bigFont;
    private BitmapFont font;
    private TextButton yesButton;
    private TextButton noButton;

    private int selection;
    private int n;

    public GameOverScreen(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.game = playScreen.getGame();
        this.checkpointData = playScreen.getCheckpointData();
        this.levelID = playScreen.getLevelID();
        this.assets = game.getAssets();
        this.bigFont = assets.panelBigFontAssets.panelBigFont;
        this.font = assets.panelFontAssets.panelFont;
    }

    @Override
    public void show() {
        Gdx.app.log("GameOverScreen", "show");
        this.viewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.stage = new Stage(viewport, game.getBatch());

        // label style
        Label.LabelStyle style = new Label.LabelStyle(bigFont, Color.DARK_GRAY);
        // game over label
        Label gameover = new Label("GAME OVER", style);
        gameover.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 + 128 / PPM, Align.center);

        // sad face image
        Image sad = new Image(assets.gameOverAssets.sadFace);
        sad.setSize(sad.getWidth() / PPM, sad.getHeight() / PPM);
        sad.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 + 32 / PPM, Align.center);

        // label style
        Label.LabelStyle stylePlayAgain = new Label.LabelStyle(font, Color.WHITE);
        // play again label
        Label playAgain = new Label("PLAY AGAIN?", stylePlayAgain);
        playAgain.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 - 64 / PPM , Align.center);

        this.n = 2; // number of buttons

        // create buttons
        createButtons();

        // add actors
        stage.addActor(gameover);
        stage.addActor(sad);
        stage.addActor(playAgain);
        stage.addActor(yesButton);
        stage.addActor(noButton);
    }

    private void update() {
        // process input
        processInput();

        // update color of buttons
        switch(selection) {
            case 0:
                yesButton.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1);
                noButton.getLabel().setColor(Color.WHITE);
                break;
            case 1:
                yesButton.getLabel().setColor(Color.WHITE);
                noButton.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1);
                break;
        }
    }

    private void processInput() {
        // update selection
        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            selection = (selection - 1 + n) % n;
            Gdx.app.log("GameOverScreen", "selection = " + selection);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            selection = (selection + 1 + n) % n;
            Gdx.app.log("GameOverScreen", "selection = " + selection);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            handleSelection();
        }
    }

    @Override
    public void render(float delta) {
        // update
        this.update();

        // clear game screen
        Gdx.gl.glClearColor(0f / 255, 139f / 255, 139f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.draw();
    }

    private void createButtons() {
        // yes GlyphLayout
        GlyphLayout yesGlyph = new GlyphLayout();
        yesGlyph.setText(font, "YES");

        // no GlyphLayout
        GlyphLayout noGlyph = new GlyphLayout();
        noGlyph.setText(font, "NO");

        // add font to style
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;

        // yes button
        this.yesButton = new TextButton("YES", style);
        yesButton.setSize(yesGlyph.width, yesGlyph.height);
        yesButton.setPosition(viewport.getWorldWidth() / 2 - 48 / PPM, viewport.getWorldHeight() / 2 - 96 / PPM , Align.center);

        // no button
        this.noButton = new TextButton("NO", style);
        noButton.setSize(noGlyph.width, noGlyph.height);
        noButton.setPosition(viewport.getWorldWidth() / 2 + 48 / PPM, viewport.getWorldHeight() / 2 - 96 / PPM , Align.center);
    }

    private void handleSelection() {
        // first dispose
        this.dispose();

        // then handle selection
        switch(selection) {
            // yes - restart appropriate level
            case 0:
                Gdx.app.log("GameOverScreen", "YES was selected - Game should restart");
                loadLevel();
                break;
            // no - return to main menu
            case 1:
                Gdx.app.log("GameOverScreen", "NO was selected - Should return to main menu");
                game.setScreen(new MenuScreen(game));
                break;
        }
    }

    private void loadLevel() {
        // if game over happens in level 3 (cave) it restarts in level 2
        if(levelID == 3) {
            game.respawn(playScreen, checkpointData, 2);
        }
        else {
            game.respawn(playScreen, checkpointData, levelID);
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameOverScreen", "resize");
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameOverScreen", "dispose");
        stage.dispose();
    }
}
