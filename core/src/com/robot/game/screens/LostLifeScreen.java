package com.robot.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Action;
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
import com.robot.game.util.Assets;

import static com.robot.game.util.Constants.*;

public class LostLifeScreen extends ScreenAdapter {

    private RobotGame game;
    private PlayScreen playScreen;
    private Assets assets;
    private Viewport viewport;
    private Stage stage;
    private BitmapFont font;

    public LostLifeScreen(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.game = playScreen.getGame();
        this.assets = game.getAssets();
        this.font = assets.panelFontAssets.panelFont;
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
        Label.LabelStyle style = new Label.LabelStyle(font, Color.WHITE);

        // label
        Label initialLives = new Label(String.valueOf(game.getCheckpointData().getLives() + 1), style);
        initialLives.setFontScale(font.getScaleX() * 1.5f);
        initialLives.setPosition(viewport.getWorldWidth() / 2 + 24 / PPM, viewport.getWorldHeight() / 2 - 4 / PPM, Align.center);

        // add actors
        stage.addActor(robot);
        stage.addActor(initialLives);

        // add actions
        RunnableAction changeText = new RunnableAction();
        changeText.setRunnable(new Runnable() {
            @Override
            public void run() {
                initialLives.setText(String.valueOf(game.getCheckpointData().getLives()));
            }
        });
        RunnableAction respawn = new RunnableAction();
        respawn.setRunnable(new Runnable() {
            @Override
            public void run() {
                dispose();

                game.respawn(playScreen, playScreen.getCheckpointData(), playScreen.getLevelID());
            }
        });

        SequenceAction sequenceAction = new SequenceAction();
        sequenceAction.addAction(Actions.fadeOut(1f));
        sequenceAction.addAction(changeText);
        sequenceAction.addAction(Actions.fadeIn(1f));
        sequenceAction.addAction(respawn);
        initialLives.addAction(sequenceAction);
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
    public void dispose() {
        Gdx.app.log("LostLifeScreen", "dispose");
        stage.dispose();
    }
}
