package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.util.Assets;
import com.robot.game.util.checkpoints.CheckpointData;
import com.robot.game.util.checkpoints.FileSaver;

import static com.robot.game.util.constants.Constants.*;

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

    private boolean doNotSaveInHide;

    public GameOverScreen(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.game = playScreen.getGame();
        this.checkpointData = game.getCheckpointData(); //
        this.levelID = playScreen.getLevelID(); // use the levelID of the last PlayScreen
        this.assets = game.getAssets();
        this.bigFont = assets.panelBigFontAssets.panelBigFont;
        this.font = assets.panelFontAssets.panelFont;
    }

    @Override
    public void show() {
        Gdx.app.log("GameOverScreen", "show");
        this.viewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.stage = new Stage(viewport, game.getBatch());

        // game over label
        Label.LabelStyle styleGameOver = new Label.LabelStyle(bigFont, new Color(0f / 255, 94f / 255, 94f / 255, 1)); // title color
        Label gameover = new Label("GAME OVER", styleGameOver);
        gameover.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 + 128 / PPM, Align.center);

        SequenceAction sequenceAction = new SequenceAction(); // add action to game over label
        sequenceAction.addAction(Actions.fadeOut(0.35f, Interpolation.fade));
        sequenceAction.addAction(Actions.fadeIn(0.35f, Interpolation.fade));
        gameover.addAction(Actions.repeat(RepeatAction.FOREVER, sequenceAction));

        // sad face image
        Image sad = new Image(assets.gameOverAssets.sadFace);
        sad.setSize(sad.getWidth() / PPM, sad.getHeight() / PPM);
        sad.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 + 32 / PPM, Align.center);

        // score label
        Label.LabelStyle styleScore = new Label.LabelStyle(font, new Color(0f / 255, 94f / 255, 94f / 255, 1)); // title color
        Label score = new Label("SCORE: " + playScreen.getScoreOnGameEnd(), styleScore);
        score.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 - 64 / PPM, Align.center);

        // play again label
        Label.LabelStyle stylePlayAgain = new Label.LabelStyle(font, new Color(238f / 255, 232f / 255, 170f / 255, 1)); // white
        Label playAgain = new Label("PLAY AGAIN?", stylePlayAgain);
        playAgain.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 - 112 / PPM, Align.center);

        this.n = 2; // number of buttons

        // create buttons
        createButtons();

        // add listeners
        addListeners();

        // add actors
        stage.addActor(gameover);
        stage.addActor(sad);
        stage.addActor(score);
        stage.addActor(playAgain);
        stage.addActor(yesButton);
        stage.addActor(noButton);

        // set InputProcessor
        Gdx.input.setInputProcessor(stage);
    }

    private void update() {
        // process input
        processInput();

        // update color of buttons
        switch(selection) {
            case 0:
                yesButton.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1); // orange
                noButton.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white
                break;
            case 1:
                yesButton.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white
                noButton.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1); // orange
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

        stage.act();
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
        yesButton.setPosition(viewport.getWorldWidth() / 2 - 48 / PPM, viewport.getWorldHeight() / 2 - 144 / PPM, Align.center);

        // no button
        this.noButton = new TextButton("NO", style);
        noButton.setSize(noGlyph.width, noGlyph.height);
        noButton.setPosition(viewport.getWorldWidth() / 2 + 48 / PPM, viewport.getWorldHeight() / 2 - 144 / PPM, Align.center);

        Gdx.app.log("GameOverScreen", "Buttons were created");
    }

    private void addListeners() {
        // yesButton
        yesButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("GameOverScreen", "Clicked YES button");
                selection = 0;
                handleSelection();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("GameOverScreen", "Entered YES button");
                selection = 0;
            }
        });

        // noButton
        noButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("GameOverScreen", "Clicked NO button");
                selection = 1;
                handleSelection();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("GameOverScreen", "Entered NO button");
                selection = 1;
            }
        });

        Gdx.app.log("GameOverScreen", "Listeners were added to buttons");
    }

    private void handleSelection() {
        doNotSaveInHide = true;
        // reset spawning of collectables
        this.handleCollectables();

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

    private void handleCollectables() {
        /* if the file with collected items exists (meaning that items have been collected, and therefore their spawning has been disabled),
         * reset their spawning in the corresponding level and delete the file */
        if(FileSaver.getCollectedItemsFile().exists()) {
            FileSaver.resetSpawningOfCollectables(levelID);
            boolean deleted = false;
            for(int i = 0; i < 30; i++) {
                deleted = FileSaver.getCollectedItemsFile().delete();
                System.out.println(i);
                if(deleted) break;
                try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }
                System.gc();
            }
            System.out.println(deleted + "!!!!!!!!!");
            Gdx.app.log("GameOverScreen", "collectedItems.json deleted = " + deleted);
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("GameOverScreen", "resize");
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.app.log("GameOverScreen", "hide");
        if(!doNotSaveInHide) {
            handleCollectables();
        }
    }

    @Override
    public void dispose() {
        Gdx.app.log("GameOverScreen", "dispose");
        stage.dispose();
        setToNull();
    }

    private void setToNull() {
        viewport = null;
        bigFont = null;
        font = null;
        Gdx.app.log("GameOverScreen", "Objects were set to null");
    }
}
