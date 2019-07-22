package com.robot.game.util;

import com.badlogic.gdx.math.Vector2;

import static com.robot.game.util.Constants.SPAWN_LOCATION;

public class CheckpointData {

    private int health;
    private int lives;
    private int score;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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

    public void increaseHealth(int powerup) {
        health += powerup;
    }

    public void increaseScore(int points) {
        score += points;
    }

    public void setDefaultData() {
        health = 100;
        lives = 3;
        score = 0;
        spawnLocation = SPAWN_LOCATION;

        firstCheckpointActivated = false;
        secondCheckpointActivated = false;
        thirdCheckpointActivated = false;
    }
}
