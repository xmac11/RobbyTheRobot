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

    private Array<TextButton> buttons;
//    private TextButton playButton;
//    private TextButton exitButton;

    public MenuScreen(RobotGame game) {
        this.game = game;
        this.batch = game.getBatch();
        this.assets = game.getAssets();
        this.levelID = game.getCheckpointData().getLevelID();
        this.buttons = new Array<>();
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
        for(TextButton textButton: buttons) {
            stage.addActor(textButton);
        }

        this.n = buttons.size;

        // set InputProcessor
        Gdx.input.setInputProcessor(stage);
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

        // update color of buttons
        for(int i = 0; i < buttons.size; i++) {
            if(i == selection) {
                buttons.get(i).getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1);
            }
            else {
                buttons.get(i).getLabel().setColor(Color.WHITE);
            }
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
        TextButton playButton = new TextButton("PLAY", style);
        playButton.setPosition(menuScreenViewport.getWorldWidth() / 2, menuScreenViewport.getWorldHeight() / 2, Align.center);
        playButton.setSize(playGlyph.width, playGlyph.height);
        buttons.add(playButton);

        // exit button
        TextButton exitButton = new TextButton("EXIT", style);
        exitButton.setPosition(menuScreenViewport.getWorldWidth() / 2 - 5 / PPM, playButton.getY() - 32 / PPM, Align.center);
        exitButton.setSize(exitGlyph.width, exitGlyph.height);
        buttons.add(exitButton);
    }

    private void addListeners() {

        // start button
        buttons.get(0).addListener(new ClickListener() {
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
        buttons.get(1).addListener(new ClickListener() {
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
