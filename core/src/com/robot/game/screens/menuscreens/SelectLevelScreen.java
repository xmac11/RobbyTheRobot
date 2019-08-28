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
import com.robot.game.util.checkpoints.CheckpointData;
import com.robot.game.util.checkpoints.FileSaver;

import static com.robot.game.util.constants.Constants.*;

public class SelectLevelScreen extends ScreenAdapter {

    private RobotGame game;
    private CheckpointData checkpointData;
    private int levelID;
    private Stage stage;
    private Viewport viewport;
    private BitmapFont font;

    private TextButton level1Button;
    private TextButton level2Button;
    private TextButton returnButton;

    private boolean level1Clicked;
    private boolean level2Clicked;
    private boolean returnClicked;

    public SelectLevelScreen(RobotGame game) {
        this.game = game;
        this.checkpointData = game.getCheckpointData();
        this.levelID = checkpointData.getLevelID();
        this.font = game.getAssets().panelFontAssets.panelFont;
    }

    @Override
    public void show() {
        Gdx.app.log("SelectLevelScreen", "show");
        this.viewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.stage = new Stage(viewport, game.getBatch());

        // pane
        Image panel = new Image(game.getAssets().mainMenuAssets.selectLevel);
        panel.setSize(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2);
        panel.setPosition(viewport.getWorldWidth() / 2 - panel.getWidth() / 2 , viewport.getWorldHeight() / 2 - panel.getHeight() / 2);

        // create buttons
        createButtons();

        // add listeners
        addListeners();

        // add actors
        stage.addActor(panel);
        stage.addActor(level1Button);
        stage.addActor(level2Button);
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

        // check if any button was clicked
        checkForInput();
    }

    private void checkForInput() {
        if(level1Clicked) {
            handleSelectedLevel(1);
        }
        else if(level2Clicked) {
            handleSelectedLevel(2);
        }
        else if(returnClicked) {
            this.dispose();
            game.setScreen(new MenuScreen(game));
        }
    }

    private void handleSelectedLevel(int selectedID) {
        // set default data
        checkpointData.setDefaultRobotData();
        checkpointData.setDefaultLevelData(selectedID);

        // save game data
        FileSaver.saveCheckpointData(checkpointData);

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
            Gdx.app.log("SelectLevelScreen", "collectedItems.json deleted = " + deleted);
        }

        // switch to RobCaptcha
        dispose();
        game.setScreen(new RobCaptcha(game));
    }

    private void createButtons() {
        // level1 GlyphLayout
        GlyphLayout level1Glyph = new GlyphLayout();
        level1Glyph.setText(font, "LEVEL 1");

        // level2 GlyphLayout
        GlyphLayout level2Glyph = new GlyphLayout();
        level2Glyph.setText(font, "LEVEL 2");

        // return GlyphLayout
        GlyphLayout returnGlyph = new GlyphLayout();
        returnGlyph.setText(font, "RETURN");

        // add font to style
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;

        // level 1 button
        this.level1Button = new TextButton("LEVEL 1", style);
        level1Button.setSize(level1Glyph.width, level1Glyph.height);
        level1Button.setPosition(300 / PPM, viewport.getWorldHeight() / 2 - 16 / PPM, Align.center);
        level1Button.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white

        // level 2 button
        this.level2Button = new TextButton("LEVEL 2", style);
        level2Button.setSize(level2Glyph.width, level2Glyph.height);
        level2Button.setPosition(viewport.getWorldWidth() - 300 / PPM, viewport.getWorldHeight() / 2 - 16 / PPM, Align.center);
        level2Button.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white

        // return button
        this.returnButton = new TextButton("RETURN", style);
        returnButton.setSize(returnGlyph.width, returnGlyph.height);
        returnButton.setPosition(viewport.getWorldWidth() / 2 + 128 / PPM, viewport.getWorldHeight() / 2 - 128 / PPM, Align.center);
        returnButton.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white

        Gdx.app.log("SelectLevelScreen", "Select Level panel buttons were created");
    }

    private void addListeners() {
        // level 1 button
        level1Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("SelectLevelScreen", "Clicked LEVEL 1 button");
                Gdx.input.setInputProcessor(null);
                level1Clicked = true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("SelectLevelScreen", "Entered LEVEL 1 button");
                level1Button.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1); // orange
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.app.log("SelectLevelScreen", "Exited LEVEL 1 button");
                level1Button.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white
            }
        });

        // level 2 button
        level2Button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("SelectLevelScreen", "Clicked LEVEL 2 button");
                Gdx.input.setInputProcessor(null);
                level2Clicked = true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("SelectLevelScreen", "Entered LEVEL 2 button");
                level2Button.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1); // orange
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.app.log("SelectLevelScreen", "Exited LEVEL 2 button");
                level2Button.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white
            }
        });

        // return button
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("SelectLevelScreen", "Clicked RETURN button. Returning to MainMenu");
                Gdx.input.setInputProcessor(null);
                returnClicked = true;
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("SelectLevelScreen", "Entered RETURN button");
                returnButton.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1); // orange
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                Gdx.app.log("SelectLevelScreen", "Exited RETURN button");
                returnButton.getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white
            }
        });

        Gdx.app.log("SelectLevelScreen", "Listeners were added to buttons");
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("SelectLevelScreen", "resize");
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        Gdx.app.log("SelectLevelScreen", "dispoe");
        stage.dispose();
        setToNull();
    }

    private void setToNull() {
        viewport = null;
        font = null;
        Gdx.app.log("SelectLevelScreen", "Objects were set to null");
    }
}
