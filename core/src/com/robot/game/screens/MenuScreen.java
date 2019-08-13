package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.*;

public class MenuScreen extends ScreenAdapter {

    private RobotGame game;
    private SpriteBatch batch;
    private Assets assets;
    private int levelID;

    private Stage stage;
    private Viewport menuScreenViewport;
    private BitmapFont font;

    private int selection;
    private int n;

//    private Array<TextButton> buttons;
    private TextButton playButton;
    private TextButton exitButton;

    public MenuScreen(RobotGame game) {
        this.game = game;
        this.batch = game.getBatch();
        this.assets = game.getAssets();
        this.levelID = game.getCheckpointData().getLevelID();

//        this.buttons = new Array<>();
        this.n = 2; // number of buttons
    }

    @Override
    public void show() {

        this.menuScreenViewport = new ExtendViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.font = assets.fontAssets.font;

        // create stage
        this.stage = new Stage(menuScreenViewport, batch);

        // create buttons
        createButtons();

        // add listeners to buttons
        addListeners();

        // add actors
        stage.addActor(playButton);
        stage.addActor(exitButton);

        // set InputProcessor
        Gdx.input.setInputProcessor(stage);
    }

    private void createButtons() {
        // play GlyphLayout
        GlyphLayout playGlyph = new GlyphLayout();
        playGlyph.setText(font, "PLAY");

        // exit GlyphLayout
        GlyphLayout exitGlyph = new GlyphLayout();
        exitGlyph.setText(font, "EXIT");

        // add font to style
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;

        // play button
        this.playButton = new TextButton("PLAY", style);
        playButton.setPosition(menuScreenViewport.getWorldWidth() / 2, menuScreenViewport.getWorldHeight() / 2, Align.center);
        playButton.setSize(playGlyph.width, playGlyph.height);

        // exit button
        this.exitButton = new TextButton("EXIT", style);
        exitButton.setPosition(menuScreenViewport.getWorldWidth() / 2 - 5 / PPM, playButton.getY() - 32 / PPM, Align.center);
        exitButton.setSize(exitGlyph.width, exitGlyph.height);
    }

    private void update(float delta) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selection = (selection - 1 + n) % n;
            Gdx.app.log("MenuScreen", "selection = " + selection);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selection = (selection + 1 + n) % n;
            Gdx.app.log("MenuScreen", "selection = " + selection);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            handleSelection();
        }

        switch(selection) {
            case 0:
                playButton.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1);
                exitButton.getLabel().setColor(Color.WHITE);
                break;

            case 1:
                playButton.getLabel().setColor(Color.WHITE);
                exitButton.getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1);
                break;
        }
    }

    @Override
    public void render(float delta) {
        // update
        this.update(delta);

        // clear game screen
        Gdx.gl.glClearColor(0f / 255, 139f / 255, 139f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act();
        stage.draw();
    }

    private void addListeners() {

        // start button
        playButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selection = 0;
                handleSelection();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("MenuScreen", "enter start button");
                selection = 0;
            }
        });


        // exit button
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                selection = 1;
                handleSelection();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("MenuScreen", "enter exit button");
                selection = 1;
            }
        });
    }

    private void handleSelection() {
        switch(selection) {
            case 0:
                Gdx.app.log("MenuScreen", "PLAY was selected");
                loadLevel();
                break;
            case 1:
                Gdx.app.log("MenuScreen", "QUIT was selected");
                Gdx.app.exit();
        }
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
        menuScreenViewport.update(width, height, true);
    }

    /*@Override
    public void hide() {
        super.hide();
    }

    @Override
    public void dispose() {
        super.dispose();
    }*/
}
