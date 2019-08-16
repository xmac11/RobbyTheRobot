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
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.*;

public class MenuScreen extends ScreenAdapter {

    private RobotGame game;
    private Assets assets;
    private int levelID;

    private Stage stage;
    private Viewport menuScreenViewport;
    private BitmapFont font;

    private int selection;
    private int numberOfButtons;

    private Array<TextButton> buttons;
    private int playIndex;
    private int exitIndex;

    public MenuScreen(RobotGame game) {
        this.game = game;
        this.assets = game.getAssets();
        this.levelID = game.getCheckpointData().getLevelID();
        this.buttons = new Array<>();
    }

    @Override
    public void show() {
        Gdx.app.log("MenuScreen", "show");

        this.menuScreenViewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.font = assets.panelFontAssets.panelFont;

        // create stage
        this.stage = new Stage(menuScreenViewport, game.getBatch());

        Image background = new Image(assets.mainMenuAssets.mainMenuBG);
        background.setSize(background.getWidth() / PPM, background.getHeight() / PPM);
        background.setPosition(menuScreenViewport.getWorldWidth() / 2, menuScreenViewport.getWorldHeight() / 2, Align.center);

        // create buttons
        createButtons();

        // add listeners to buttons
        addListeners();

        // add actors
        stage.addActor(background);
        for(TextButton textButton: buttons) {
            stage.addActor(textButton);
        }

        this.numberOfButtons = buttons.size;

        // set InputProcessor
        Gdx.input.setInputProcessor(stage);
    }

    private void update(float delta) {
        // process input
        processInput();

        // update color of buttons
        for(int i = 0; i < buttons.size; i++) {
            if(i == selection) {
                buttons.get(i).getLabel().setColor(255f / 255, 192f / 255, 43f / 255, 1); // orange
            }
            else {
                buttons.get(i).getLabel().setColor(238f / 255, 232f / 255, 170f / 255, 1); // white
            }
        }
    }

    private void processInput() {
        // update selection
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selection = (selection - 1 + numberOfButtons) % numberOfButtons;
            Gdx.app.log("MenuScreen", "selection = " + selection);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selection = (selection + 1 + numberOfButtons) % numberOfButtons;
            Gdx.app.log("MenuScreen", "selection = " + selection);
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            handleSelection();
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

        int index = 0;

        // play button
        TextButton playButton = new TextButton("PLAY", style);
        playButton.setSize(playGlyph.width, playGlyph.height);
        playButton.setPosition(menuScreenViewport.getWorldWidth() / 2, menuScreenViewport.getWorldHeight() / 2, Align.center);
        buttons.add(playButton);
        this.playIndex = index++;
        Gdx.app.log("MenuScreen", "playIndex = " + playIndex);

        // exit button
        TextButton exitButton = new TextButton("EXIT", style);
        exitButton.setSize(exitGlyph.width, exitGlyph.height);
        exitButton.setPosition(menuScreenViewport.getWorldWidth() / 2 - 5 / PPM, playButton.getY() - 32 / PPM, Align.center);
        buttons.add(exitButton);
        this.exitIndex = index;
        Gdx.app.log("MenuScreen", "exitIndex = " + exitIndex);

        Gdx.app.log("MenuScreen", "Buttons were created");
    }

    private void addListeners() {
        // play button
        buttons.get(playIndex).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MenuScreen", "Clicked PLAY button");
                selection = playIndex;
                handleSelection();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("MenuScreen", "Entered PLAY button");
                selection = playIndex;
            }
        });


        // exit button
        buttons.get(exitIndex).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MenuScreen", "Clicked EXIT button");
                selection = exitIndex;
                handleSelection();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("MenuScreen", "Entered EXIT button");
                selection = exitIndex;
            }
        });

        Gdx.app.log("MenuScreen", "Listeners were added to buttons");
    }

    private void handleSelection() {
        // first dispose
        this.dispose();

        // then handle selection
        if(selection == playIndex) {
            Gdx.app.log("MenuScreen", "PLAY was selected");
            loadLevel();
        }
        else if(selection == exitIndex) {
            Gdx.app.log("MenuScreen", "EXIT was selected");
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
        Gdx.app.log("MenuScreen", "resize");
        menuScreenViewport.update(width, height, true);
    }

    /*@Override
    public void hide() {
        super.hide();
    }*/

    @Override
    public void dispose() {
        Gdx.app.log("MenuScreen", "dispose");
        stage.dispose();
    }
}