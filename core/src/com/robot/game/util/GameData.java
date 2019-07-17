package com.robot.game.util;

import com.badlogic.gdx.math.Vector2;

import static com.robot.game.util.Constants.SPAWN_LOCATION;

public class GameData {

    private int health;
    private int lives;
    private Vector2 position;
    private boolean checkPoint1Activated;

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        this.lives = lives;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void decreaseLives() {
        lives--;
    }

    public void increaseLives() {
        lives++;
    }

    public boolean isCheckPoint1Activated() {
        return checkPoint1Activated;
    }

    public void setCheckPoint1Activated(boolean checkPoint1Activated) {
        this.checkPoint1Activated = checkPoint1Activated;
    }

    public void decreaseHealth(int damage) {
        health -= damage;
    }

    public void setDefaultData() {
        health = 100;
        lives = 3;
        position = SPAWN_LOCATION;
        checkPoint1Activated = false;
    }
}
