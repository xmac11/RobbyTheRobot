package com.robot.game.util.checkpoints;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import static com.robot.game.util.constants.Constants.*;

public class CheckpointData {

    private int levelID;
    private int health;
    private int lives;
    private int score;
    private int ammo;
    private Vector2 spawnLocation;
    private boolean hasTorch;

    // Checkpoints
    private boolean firstCheckpointActivated;
    private boolean secondCheckpointActivated;
    private boolean thirdCheckpointActivated;

    // game completed
    private boolean gameCompleted;

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
        health = Math.max(health - damage, 0);
    }

    public void increaseHealth(int powerup) {
        health = Math.min(health + powerup, 100);
    }

    public void increaseScore(int points) {
        score += points;
    }

    public boolean hasTorch() {
        return hasTorch;
    }

    public void setHasTorch(boolean hasTorch) {
        this.hasTorch = hasTorch;
    }

    public int getLevelID() {
        return levelID;
    }

    public void setLevelID(int levelID) {
        this.levelID = levelID;
    }

    public int getAmmo() {
        return ammo;
    }

    public void setAmmo(int ammo) {
        this.ammo = ammo;
    }

    public void decreaseAmmo() {
        ammo = Math.max(--ammo, 0);
    }

    public boolean isGameCompleted() {
        return gameCompleted;
    }

    public void setGameCompleted(boolean gameCompleted) {
        this.gameCompleted = gameCompleted;
    }

    public void setCheckpointsToFalse() {
        firstCheckpointActivated = false;
        secondCheckpointActivated = false;
        thirdCheckpointActivated = false;
        Gdx.app.log("CheckpointData","All checkpoints set to false");
    }

    public void setDefaultRobotData() {
        health = 100;
        lives = 3;
        score = 0;
        ammo = 6;
        hasTorch = false;
    }

    public void setDefaultLevelData(int levelID) {
        // if levelID is 3, set it to 2, else set it to the levelID passed
        this.levelID = (levelID == 3 ? 2 : levelID);

        switch(this.levelID) {
            case 1:
                spawnLocation = SPAWN_LOCATION_L1;
                break;
            case 2:
                spawnLocation = SPAWN_LOCATION_L2;
                break;
            /*case 3:
                spawnLocation = SPAWN_LOCATION_L3;
                break;*/
        }

        firstCheckpointActivated = false;
        secondCheckpointActivated = false;
        thirdCheckpointActivated = false;
    }
}
