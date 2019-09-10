package com.robot.game.util;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.robot.game.RobotGame;
import com.robot.game.entities.Robot;
import com.robot.game.interactiveObjects.ladder.LadderClimbHandler;
import com.robot.game.screens.playscreens.PlayScreen;

import static com.robot.game.util.constants.Constants.*;

/*Implementation was inspire from https://www.youtube.com/watch?v=z4Vqkp_ve3I */

public class AndroidController {

    private RobotGame game;
    private Robot robot;
    private LadderClimbHandler ladderClimbHandler;
    private Viewport viewport;
    private Stage stage;

    private Image rightButton;
    private Image leftButton;
    private Image upButton;
    private Image downButton;
    private Image jumpButton;
    private Image shootButton;
    private Image punchButton;
    private Image pauseButton;

    private boolean rightPressed;
    private boolean leftPressed;
    private boolean upPressed;
    private boolean punchClicked;
    private boolean shootClicked;
    private boolean pauseClicked;

    public AndroidController(PlayScreen playScreen) {
        if(playScreen.isOnAndroid()) {
            this.game = playScreen.getGame();
            this.robot = playScreen.getRobot();
            this.ladderClimbHandler = playScreen.getLadderClimbHandler();
            this.viewport = new ExtendViewport(SCREEN_WIDTH / PPM, SCREEN_HEIGHT / PPM);
            this.stage = new Stage(viewport, game.getBatch());

            // right button
            this.rightButton = new Image(game.getAssets().androidAssets.right);
            rightButton.setSize( BUTTON_SIZE, BUTTON_SIZE);
            rightButton.setPosition(8 / PPM + 2 * BUTTON_SIZE, 8 / PPM + BUTTON_SIZE);

            // left button
            this.leftButton = new Image(game.getAssets().androidAssets.left);
            leftButton.setSize( BUTTON_SIZE, BUTTON_SIZE);
            leftButton.setPosition(8 / PPM, 8 / PPM + BUTTON_SIZE);

            // up button
            this.upButton = new Image(game.getAssets().androidAssets.up);
            upButton.setSize( BUTTON_SIZE, BUTTON_SIZE);
            upButton.setPosition(8 / PPM + BUTTON_SIZE, 8 / PPM + 2 * BUTTON_SIZE);

            // down button
            this.downButton = new Image(game.getAssets().androidAssets.down);
            downButton.setSize( BUTTON_SIZE, BUTTON_SIZE);
            downButton.setPosition(8 / PPM + BUTTON_SIZE, 8 / PPM);

            // jump button
            this.jumpButton = new Image(game.getAssets().androidAssets.jump);
            jumpButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
            jumpButton.setPosition(viewport.getWorldWidth() - BUTTON_SIZE - 8 / PPM, 8 / PPM + BUTTON_SIZE);

            if(game.getCheckpointData().getLevelID() > 1) {
                // punch button
                this.punchButton = new Image(game.getAssets().androidAssets.punch);
                punchButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
                punchButton.setPosition(viewport.getWorldWidth() - BUTTON_SIZE - 8 / PPM, 16/ PPM + 2 * BUTTON_SIZE);

                // shoot button
                this.shootButton = new Image(game.getAssets().androidAssets.shoot);
                shootButton.setSize(BUTTON_SIZE, BUTTON_SIZE);
                shootButton.setPosition(viewport.getWorldWidth() - BUTTON_SIZE - 8 / PPM, 24 / PPM + 3 * BUTTON_SIZE);
            }

            // pause button
            this.pauseButton = new Image(game.getAssets().androidAssets.pause);
            pauseButton.setSize(32 / PPM, 32 / PPM);
            pauseButton.setPosition(viewport.getWorldWidth() - PADDING / PPM - pauseButton.getWidth(),
                    PADDING / PPM);

            // add listeners
            addListeners();

            // add actors
            stage.addActor(rightButton);
            stage.addActor(leftButton);
            stage.addActor(upButton);
            stage.addActor(downButton);
            stage.addActor(jumpButton);

            if(game.getCheckpointData().getLevelID() > 1) {
                stage.addActor(punchButton);
                stage.addActor(shootButton);
            }

            stage.addActor(pauseButton);
        }
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

        // shoot and punch buttons
        if(game.getCheckpointData().getLevelID() > 1) {
            addListenersPunchButton();
            addListenersShootButton();
        }

        // pause button
        addListenersPauseButton();
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
                upPressed = true;

                // climb up while on ladder
                if(robot.isOnLadder() && !robot.isFallingOffLadder()) {
                    ladderClimbHandler.climb(1);
                }
                // climb up up while falling off ladder (grabs ladder)
                else if(robot.isOnLadder()) {
                    ladderClimbHandler.grabOnLadder();
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                upPressed = false;

                // stop climbing
                if(robot.isOnLadder() && !robot.isFallingOffLadder()) {
                    ladderClimbHandler.stopClimbing();
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
                    ladderClimbHandler.climb(-1);
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                // handle ladder case
                if(robot.isOnLadder() && !robot.isFallingOffLadder()) {
                    ladderClimbHandler.stopClimbing();
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

    // add listeners to punch button
    private void addListenersPunchButton() {
        punchButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                punchClicked = true;
            }
        });
    }

    // add listeners to shoot button
    private void addListenersShootButton() {
        shootButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                shootClicked = true;
            }
        });
    }

    // add listeners to pause button
    private void addListenersPauseButton() {
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pauseClicked = true;
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

    public boolean isUpPressed() {
        return upPressed;
    }

    public boolean isPunchClicked() {
        return punchClicked;
    }

    public void setPunchClicked(boolean punchClicked) {
        this.punchClicked = punchClicked;
    }

    public boolean isShootClicked() {
        return shootClicked;
    }

    public void setShootClicked(boolean shootClicked) {
        this.shootClicked = shootClicked;
    }

    public boolean isPauseClicked() {
        return pauseClicked;
    }

    public void setPauseClicked(boolean pauseClicked) {
        this.pauseClicked = pauseClicked;
    }
}
