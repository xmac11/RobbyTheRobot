package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.util.Assets;

import static com.robot.game.util.constants.Constants.*;

public class MenuScreen extends ScreenAdapter {

    private RobotGame game;
    private Assets assets;
    private boolean gameCompleted;

    private Stage stage;
    private Viewport viewport;
    private BitmapFont font;

    private int selection;
    private int numberOfButtons;

    private Array<TextButton> buttons;
    private int playIndex;
    private int selectLevelIndex;
    private int storyIndex;
    private int tutorialIndex;
    private int exitIndex;

    public MenuScreen(RobotGame game) {
        this.game = game;
        this.assets = game.getAssets();
        this.gameCompleted = game.getCheckpointData().isGameCompleted();
        this.buttons = new Array<>();
    }

    @Override
    public void show() {
        Gdx.app.log("MenuScreen", "show");

        this.viewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        BitmapFont titleFont = assets.panelBigFontAssets.panelBigFont;

        this.font = assets.panelFontAssets.panelFont;

        // create stage
        this.stage = new Stage(viewport, game.getBatch());

        // title label
        Label.LabelStyle styleTitle = new Label.LabelStyle(titleFont, new Color(0f / 255, 94f / 255, 94f / 255, 1)); // title color
        Label title = new Label("Robby the Robot", styleTitle);
        title.setPosition(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2 + 160 / PPM, Align.center);

        // background image
        Image background = new Image(assets.mainMenuAssets.mainMenuBG);
        background.setSize(background.getWidth() / PPM, background.getHeight() / PPM);
        background.setPosition(viewport.getWorldWidth() / 4, viewport.getWorldHeight() / 2, Align.left);

        // create buttons
        createButtons(background);

        // add listeners to buttons
        addListeners();

        // add actors
        stage.addActor(title);
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
        else if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selection = (selection + 1 + numberOfButtons) % numberOfButtons;
            Gdx.app.log("MenuScreen", "selection = " + selection);
        }

        // handle selection
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

    private void createButtons(Image background) {
        // play GlyphLayout
        GlyphLayout playGlyph = new GlyphLayout();
        playGlyph.setText(font, gameCompleted ? "RESUME GAME" : "PLAY");

        // story GlyphLayout
        GlyphLayout storyGlyph = new GlyphLayout();
        storyGlyph.setText(font, "STORY");

        // tutorial GlyphLayout
        GlyphLayout tutorialGlyph = new GlyphLayout();
        tutorialGlyph.setText(font, "TUTORIAL");

        // exit GlyphLayout
        GlyphLayout exitGlyph = new GlyphLayout();
        exitGlyph.setText(font, "EXIT");

        // selectLevel GlyphLayout
        GlyphLayout selectLevelGlyph = new GlyphLayout();
        selectLevelGlyph.setText(font, "NEW GAME");

        // add font to style
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;

        int index = 0;

        // play button
        TextButton playButton = new TextButton(gameCompleted ? "RESUME GAME" : "PLAY", style);
        playButton.setSize(playGlyph.width, playGlyph.height);
        playButton.setPosition(background.getX() + 1.2f * background.getWidth(), background.getY() + 0.8f * background.getHeight(), Align.left);
        buttons.add(playButton);
        this.playIndex = index++;
        Gdx.app.log("MenuScreen", "playIndex = " + playIndex);

        // select level button
        if(gameCompleted) {
            TextButton selectLevelButton = new TextButton("NEW GAME", style);
            selectLevelButton.setSize(selectLevelGlyph.width, selectLevelGlyph.height);
            selectLevelButton.setPosition(buttons.get(index-1).getX() + 16 / PPM, buttons.get(index-1).getY() - 32 / PPM, Align.left);
            buttons.add(selectLevelButton);
            this.selectLevelIndex = index++;
            Gdx.app.log("MenuScreen", "selectLevelIndex = " + selectLevelIndex);
        }

        // story button
        TextButton storyButton = new TextButton("STORY", style);
        storyButton.setSize(storyGlyph.width, storyGlyph.height);
        storyButton.setPosition(buttons.get(index-1).getX() + 16 / PPM, buttons.get(index-1).getY() -  32 / PPM, Align.left);
        buttons.add(storyButton);
        this.storyIndex = index++;
        Gdx.app.log("MenuScreen", "storyIndex = " + storyIndex);

        // tutorial button
        TextButton tutorialButton = new TextButton("TUTORIAL", style);
        tutorialButton.setSize(tutorialGlyph.width, tutorialGlyph.height);
        tutorialButton.setPosition(buttons.get(index-1).getX() + 16 / PPM, buttons.get(index-1).getY() -  32 / PPM, Align.left);
        buttons.add(tutorialButton);
        this.tutorialIndex = index++;
        Gdx.app.log("MenuScreen", "tutorialIndex = " + tutorialIndex);

        // exit button
        TextButton exitButton = new TextButton("EXIT", style);
        exitButton.setSize(exitGlyph.width, exitGlyph.height);
        exitButton.setPosition(buttons.get(index-1).getX() + 16 / PPM, buttons.get(index-1).getY() - 32 / PPM, Align.left);
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

        // select level button
        if(gameCompleted) {
            buttons.get(selectLevelIndex).addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    Gdx.app.log("MenuScreen", "Clicked SELECT LEVEL button");
                    selection = selectLevelIndex;
                    handleSelection();
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    Gdx.app.log("MenuScreen", "Entered SELECT LEVEL button");
                    selection = selectLevelIndex;
                }
            });
        }

        // story button
        buttons.get(storyIndex).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MenuScreen", "Clicked STORY button");
                selection = storyIndex;
                handleSelection();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("MenuScreen", "Entered STORY button");
                selection = storyIndex;
            }
        });

        // tutorial button
        buttons.get(tutorialIndex).addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("MenuScreen", "Clicked TUTORIAL button");
                selection = tutorialIndex;
                handleSelection();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                Gdx.app.log("MenuScreen", "Entered TUTORIAL button");
                selection = tutorialIndex;
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
            Gdx.app.log("MenuScreen", "PLAY was selected. Switching to RobCaptcha screen");
            game.setScreen(new RobCaptcha(game));
        }
        else if(selection == selectLevelIndex) {
            Gdx.app.log("MenuScreen", "SELECT LEVEL was selected.");
            game.setScreen(new SelectLevelScreen(game));
        }
        else if(selection == storyIndex) {
            Gdx.app.log("MenuScreen", "STORY was selected.");
            game.setScreen(new StoryScreen(game));
        }
        else if(selection == tutorialIndex) {
            Gdx.app.log("MenuScreen", "TUTORIAL was selected.");
            game.setScreen(new TutorialScreen(game));
        }
        else if(selection == exitIndex) {
            Gdx.app.log("MenuScreen", "EXIT was selected");
            Gdx.app.exit();
        }
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("MenuScreen", "resize");
        viewport.update(width, height, true);
    }

    /*@Override
    public void hide() {
        super.hide();
    }*/

    @Override
    public void dispose() {
        Gdx.app.log("MenuScreen", "dispose");
        stage.dispose();
        setToNull();
    }

    private void setToNull() {
        viewport = null;
        font = null;
        Gdx.app.log("MenuScreen", "Objects were set to null");
    }
}
