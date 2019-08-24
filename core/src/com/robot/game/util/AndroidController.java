package com.robot.game.util;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.entities.Robot;
import com.robot.game.interactiveObjects.ladder.LadderClimbHandler;
import com.robot.game.screens.PlayScreen;

import static com.robot.game.util.Constants.*;

public class AndroidController {

    private RobotGame game;
    private PlayScreen playScreen;
    private Robot robot;
    private LadderClimbHandler ladderClimbHandler;
    private Viewport viewport;
    private Stage stage;

    private Image rightButton;
    private Image leftButton;
    private Image upButton;
    private Image downButton;
    private Image jumpButton;

    private boolean rightPressed;
    private boolean leftPressed;
    private boolean jumpPressed;

    public AndroidController(PlayScreen playScreen) {
        this.playScreen = playScreen;
        this.game = playScreen.getGame();
        this.robot = playScreen.getRobot();
        this.ladderClimbHandler = playScreen.getLadderClimbHandler();
        this.viewport = new FitViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
        this.stage = new Stage(viewport, game.getBatch());

        // right button
        this.rightButton = new Image(game.getAssets().androidAssets.right);
        rightButton.setSize( BUTTON_SIZE, BUTTON_SIZE);
        rightButton.setPosition(2 * BUTTON_SIZE, BUTTON_SIZE);

        // left button
        this.leftButton = new Image(game.getAssets().androidAssets.left);
        leftButton.setSize( BUTTON_SIZE, BUTTON_SIZE);
        leftButton.setPosition(0, BUTTON_SIZE);

        // up button
        this.upButton = new Image(game.getAssets().androidAssets.up);
        upButton.setSize( BUTTON_SIZE, BUTTON_SIZE);
        upButton.setPosition(BUTTON_SIZE, 2 * BUTTON_SIZE);

        // down button
        this.downButton = new Image(game.getAssets().androidAssets.down);
        downButton.setSize( BUTTON_SIZE, BUTTON_SIZE);
        downButton.setPosition(BUTTON_SIZE, 0);

        // jump button
        this.jumpButton = new Image(game.getAssets().androidAssets.jump);
        jumpButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
        jumpButton.setPosition(viewport.getWorldWidth() - BUTTON_SIZE, BUTTON_SIZE);

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
                // climb up while on ladder
                if(robot.isOnLadder() && !robot.isFallingOffLadder()) {
                    robot.climb(1);
                }
                // climb up up while falling off ladder (grabs ladder)
                else if(robot.isOnLadder()) {
                    ladderClimbHandler.grabOnLadder();
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // stop climbing
                if(robot.isOnLadder() && !robot.isFallingOffLadder()) {
                    robot.stop();
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
                if(robot.isOnLadder() && !robot.isFallingOffLadder()) {
                    robot.climb(-1);
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // handle ladder case
                if(robot.isOnLadder() && !robot.isFallingOffLadder()) {
                    robot.stop();
                }
            }
        });
    }

    // add listeners to jump button
    private void addListenersJumpButton() {
        jumpButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // handle ladder case
                if(robot.isOnLadder() && !robot.isFallingOffLadder()) {
                    ladderClimbHandler.jumpOffLadder();
                }
                else {
                    robot.setJumpTimer(ROBOT_JUMP_TIMER);
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
