package com.robot.game.util;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.entities.Robot;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class AndroidController {

    private RobotGame game;
    private PlayScreen playScreen;
    private Robot robot;
    private Viewport viewport;
    private Stage stage;

    private Image rightButton;
    private Image leftButton;
    private Image upButton;
    private Image downButton;
    private Image jumpButton;

    private boolean rightPressed;
    private boolean leftPressed;
    private boolean upPressed;
    private boolean downPressed;
    private boolean jumpPressed;

    public AndroidController(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.game = playScreen.getGame();
        this.robot = playScreen.getRobot();
        this.viewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.stage = new Stage(viewport, game.getBatch());

        // right button
        this.rightButton = new Image(game.getAssets().androidAssets.right);
        rightButton.setSize( 48 / PPM, 48 / PPM);
        rightButton.setPosition(96 / PPM, 48 / PPM);

        // left button
        this.leftButton = new Image(game.getAssets().androidAssets.left);
        leftButton.setSize( 48 / PPM, 48 / PPM);
        leftButton.setPosition(0 / PPM, 48 / PPM);

        // up button
        this.upButton = new Image(game.getAssets().androidAssets.up);
        upButton.setSize( 48 / PPM, 48 / PPM);
        upButton.setPosition(48 / PPM, 96 / PPM);

        // down button
        this.downButton = new Image(game.getAssets().androidAssets.down);
        downButton.setSize( 48 / PPM, 48 / PPM);
        downButton.setPosition(48 / PPM, 0 / PPM);

        // jump button
        this.jumpButton = new Image(game.getAssets().androidAssets.jump);
        jumpButton.setSize(48 / PPM, 48 / PPM);
        jumpButton.setPosition(viewport.getWorldWidth() - jumpButton.getWidth(), 48 / PPM);

        // add listeners
        addListeners();

        // add actors
        stage.addActor(rightButton);
        stage.addActor(leftButton);
        stage.addActor(upButton);
        stage.addActor(downButton);
        stage.addActor(jumpButton);
    }

    public void draw() {
        //stage.act();
        stage.draw();
    }

    private void addListeners() {
        // right button
        addListenersRightButton();

        // left button
        addListenersLeftButton();

        // up button
        addListenersUpButton();

        // down button
        addListenersDownButton();

        // jump button
        addListenersJumpButton();
    }

    // add listeners to right button
    private void addListenersRightButton() {
        rightButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                rightPressed = false;
            }
        });
    }

    // add listeners to left button
    private void addListenersLeftButton() {
        leftButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = true;
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                leftPressed = false;
            }
        });
    }

    // add listeners to up button
    private void addListenersUpButton() {
        upButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // handle ladder case
                if(robot.isOnLadder()) {
                    robot.climb(1);
                }
                else {
                    upPressed = true;
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // handle ladder case
                if(robot.isOnLadder()) {
                    robot.stopClimbing();
                }
                else {
                    upPressed = false;
                }
            }
        });
    }

    // add listeners to down button
    private void addListenersDownButton() {
        downButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // handle ladder case
                if(robot.isOnLadder()) {
                    robot.climb(-1);
                }
                else {
                    downPressed = true;
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // handle ladder case
                if(robot.isOnLadder()) {
                    robot.stopClimbing();
                }
                else {
                    downPressed = false;
                }
            }
        });
    }

    private void addListenersJumpButton() {
        jumpButton.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                // handle ladder case
                if(robot.isOnLadder()) {
                    // TODO
                }
                else {
                    jumpPressed = true;
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // handle ladder case
                if(robot.isOnLadder()) {
                    // TODO
                }
                else {
                    jumpPressed = false;
                }
            }
        });
    }

    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    public Stage getStage() {
        return stage;
    }

    public boolean isRightPressed() {
        return rightPressed;
    }

    public boolean isLeftPressed() {
        return leftPressed;
    }

    public boolean isJumpPressed() {
        return jumpPressed;
    }
}
