package com.robot.game.util;

import com.badlogic.gdx.math.Vector2;

import static com.robot.game.util.Constants.SPAWN_LOCATION;

public class GameData {

    private int health;
    private int lives;
    private Vector2 spawnLocation;

    // Checkpoints
    private boolean firstCheckpointActivated;
    private boolean secondCheckpointActivated;
    private boolean thirdCheckpointActivated;

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

    public Vector2 getSpawnLocation() {
        return spawnLocation;
    }

    public void setSpawnLocation(Vector2 spawnLocation) {
        this.spawnLocation = spawnLocation;
    }

    public boolean isFirstCheckpointActivated() {
        return firstCheckpointActivated;
    }

    public void setFirstCheckpointActivated(boolean firstCheckpointActivated) {
        this.firstCheckpointActivated = firstCheckpointActivated;
    }

    public boolean isSecondCheckpointActivated() {
        return secondCheckpointActivated;
    }

    public void setSecondCheckpointActivated(boolean secondCheckpointActivated) {
        this.secondCheckpointActivated = secondCheckpointActivated;
    }

    public boolean isThirdCheckpointActivated() {
        return thirdCheckpointActivated;
    }

    public void setThirdCheckpointActivated(boolean thirdCheckpointActivated) {
        this.thirdCheckpointActivated = thirdCheckpointActivated;
    }

    public void decreaseLives() {
        lives--;
    }

    public void increaseLives() {
        lives++;
    }

    public void decreaseHealth(int damage) {
        health -= damage;
    }

    public void setDefaultData() {
        health = 100;
        lives = 1000;
        spawnLocation = SPAWN_LOCATION;
    }
}
