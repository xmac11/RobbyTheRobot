package com.robot.game.screens.lostscreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.RunnableAction;
import com.badlogic.gdx.scenes.scene2d.actions.SequenceAction;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.interactiveObjects.collectables.CollectableHandler;
import com.robot.game.screens.playscreens.PlayScreen;
import com.robot.game.Assets;
import com.robot.game.checkpoints.FileSaver;
import org.json.simple.JSONArray;

import static com.robot.game.util.constants.Constants.*;

public class LostLifeScreen extends ScreenAdapter {

    private RobotGame game;
    private PlayScreen playScreen;
    private Assets assets;
    private CollectableHandler collectableHandler;
    private JSONArray collectedItems;
    private Viewport viewport;
    private Stage stage;
    private BitmapFont font;
    private boolean doNotSaveInHide;

    public LostLifeScreen(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.game = playScreen.getGame();
        this.assets = game.getAssets();
        this.collectableHandler = playScreen.getCollectableHandler();
        this.collectedItems = collectableHandler.getCollectedItems();
        this.font = assets.panelBigFontAssets.panelBigFont;
    }

    @Override
    public void show() {
        Gdx.app.log("LostLifeScreen", "show");
        this.viewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.stage = new Stage(viewport, game.getBatch());

        // robot image
        Image robot = new Image(assets.hudAssets.lives);
        robot.setSize(robot.getWidth() / 2 / PPM, robot.getHeight() / 2 / PPM);
        robot.setPosition(viewport.getWorldWidth() / 2 - 24 / PPM, viewport.getWorldHeight() / 2, Align.center);

        // label style
        Label.LabelStyle style = new Label.LabelStyle(font, new Color(238f / 255, 232f / 255, 170f / 255, 1));

        // label
        Label lives = new Label("x" + (game.getCheckpointData().getLives() + 1), style);
        lives.setPosition(viewport.getWorldWidth() / 2 + 32 / PPM, viewport.getWorldHeight() / 2 - 4 / PPM, Align.center);

        // add actors
        stage.addActor(robot);
        stage.addActor(lives);

        // add actions
        RunnableAction changeText = new RunnableAction();
        changeText.setRunnable(new Runnable() {
            @Override
            public void run() {
                lives.setText("x" + game.getCheckpointData().getLives());
            }
        });

        // disable spawning of collected items
        RunnableAction handleCollectablesRun = new RunnableAction();
        handleCollectablesRun.setRunnable(new Runnable() {
            @Override
            public void run() {
                doNotSaveInHide = true;
                handleCollectables();
            }
        });

        RunnableAction respawn = new RunnableAction();
        respawn.setRunnable(new Runnable() {
            @Override
            public void run() {
                dispose();
                game.respawn(playScreen, game.getCheckpointData(), playScreen.getLevelID());
            }
        });

        SequenceAction sequenceAction = new SequenceAction();

        sequenceAction.addAction(Actions.fadeOut(1f, Interpolation.fade));
        sequenceAction.addAction(changeText);
        sequenceAction.addAction(Actions.fadeIn(1f, Interpolation.fade));
        sequenceAction.addAction(handleCollectablesRun);
        sequenceAction.addAction(respawn);

        lives.addAction(sequenceAction);
    }

    public void handleCollectables() {
        // if a new item has been collected in this session, save the file with collected items and disable saving from the hide() method
        if(playScreen.isNewItemCollected()) {
            // loop through all items that have been collected and disable their spawning
            for(int collectableID: collectableHandler.getItemsToDisableSpawning()) {
                collectableHandler.setSpawn(collectableID, false);
            }
            FileSaver.saveCollectedItems(collectedItems);
        }
    }

    @Override
    public void render(float delta) {
        // clear game screen
        Gdx.gl.glClearColor(0f / 255, 139f / 255, 139f / 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        Gdx.app.log("LostLifeScreen", "resize");
        viewport.update(width, height, true);
    }

    @Override
    public void hide() {
        Gdx.app.log("LostLifeScreen", "hide");
        if(!doNotSaveInHide) {
            handleCollectables();
        }
    }

    @Override
    public void dispose() {
        Gdx.app.log("LostLifeScreen", "dispose");
        stage.dispose();
        setToNull();
    }

    private void setToNull() {
        //collectableHandler.setToNull();
        viewport = null;
        font = null;
        collectableHandler = null;
        collectedItems = null;
        Gdx.app.log("LostLifeScreen", "Objects were set to null");
    }
}
