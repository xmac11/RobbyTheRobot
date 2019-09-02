package com.robot.game.screens.huds;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import com.robot.game.entities.abstractEnemies.Enemy;
import com.robot.game.entities.Robot;
import com.robot.game.interactiveObjects.collectables.Food;
import com.robot.game.interactiveObjects.collectables.Collectable;
import com.robot.game.Assets;
import com.robot.game.interfaces.Damaging;
import com.robot.game.util.staticMethods.StaticMethods;

import static com.robot.game.util.constants.Constants.*;

public class FeedbackRenderer {

    private Robot robot;
    private ObjectMap<Enemy, Float> pointsForEnemyToDraw;
    private ObjectMap<Damaging, Float> damageFromHitToDraw;
    private ObjectMap<Collectable, Float> itemPointsToDraw;
    private BitmapFont feedbackFont;
    private TextureRegion frame;
    private TextureRegion greenBar;
    private TextureRegion redBar;

    public FeedbackRenderer(Robot robot) {
        this.robot = robot;
        this.pointsForEnemyToDraw = new ObjectMap<>();
        this.damageFromHitToDraw = new ObjectMap<>();
        this.itemPointsToDraw = new ObjectMap<>();

        Assets assets = robot.getPlayScreen().getAssets();
        this.feedbackFont = assets.feedbackFontAssets.feedbackFont;
        this.frame = assets.hudAssets.frame;
        this.greenBar = assets.hudAssets.greenBar;
        this.redBar = assets.hudAssets.redBar;
    }

    public void draw(SpriteBatch batch, float delta) {

        // draw any points gained from killing enemies
        for(Enemy enemyKey: pointsForEnemyToDraw.keys()) {
            renderPointsFromEnemy(batch, delta, enemyKey);
        }

        for(Damaging damagingKey: damageFromHitToDraw.keys()) {
            renderDamageFromHit(batch, delta, damagingKey);
        }

        // draw any points gained from collecting items
        for(Collectable collectableKey: itemPointsToDraw.keys()) {

            // if collectable is a burger
            if(collectableKey instanceof Food) {
                renderPointsFromBurger(batch, delta, collectableKey);
            }

            // else if collectable is a powerup
            else {
                renderPowerupHealthbar(batch, delta, collectableKey);
            }
        }
    }

    private void renderPointsFromEnemy(SpriteBatch batch, float delta, Enemy enemyKey) {
        float alpha = pointsForEnemyToDraw.get(enemyKey);
        feedbackFont.setColor(1, 1, 1, alpha);

        if(alpha > 0) {
            feedbackFont.draw(batch,
                    String.valueOf( StaticMethods.getPointsForEnemy(enemyKey) ),
                    enemyKey.getBody().getPosition().x,
                    enemyKey.getBody().getPosition().y + 24 / PPM + (1 - alpha) / 1.5f,
                    0,
                    Align.center,
                    false);

            pointsForEnemyToDraw.put(enemyKey, alpha - /*1.5f **/ delta);
        }
        else {
            pointsForEnemyToDraw.remove(enemyKey);
        }
    }

    private void renderDamageFromHit(SpriteBatch batch, float delta, Damaging damagingtKey) {
        float alpha = damageFromHitToDraw.get(damagingtKey);
        feedbackFont.setColor(153f / 255, 0, 0, 1); // to decide: always keep alpha = 1 or decrease it

        if(alpha > 0) {
            feedbackFont.draw(batch,
                    "-" + damagingtKey.getDamage() + "HP",
                    robot.getBody().getPosition().x,
                    robot.getBody().getPosition().y + 40 / PPM + (1 - alpha) /*/ 1.5f*/,
                    0,
                    Align.center,
                    false);

            damageFromHitToDraw.put(damagingtKey, alpha - 1.5f * delta);
        }
        else {
            damageFromHitToDraw.remove(damagingtKey);
        }
    }

    private void renderPointsFromBurger(SpriteBatch batch, float delta, Collectable collectableKey) {
        float alpha = itemPointsToDraw.get(collectableKey);
        feedbackFont.setColor(1, 1, 1, alpha);

        if(alpha > 0) {
            feedbackFont.draw(batch,
                    String.valueOf(POINTS_FOR_FOOD),
                    collectableKey.getBody().getPosition().x,
                    collectableKey.getBody().getPosition().y + 24 / PPM + (1 - alpha) / 1.5f,
                    0,
                    Align.center,
                    false);

            itemPointsToDraw.put(collectableKey, alpha - 1.5f * delta);
        }
        else {
            itemPointsToDraw.remove(collectableKey);
        }
    }

    private void renderPowerupHealthbar(SpriteBatch batch, float delta, Collectable collectableKey) {
        float initialHelath = itemPointsToDraw.get(collectableKey);

        // draw the frame
        batch.draw(frame,
                robot.getBody().getPosition().x - POWERUP_FRAME_WIDTH / 2 / PPM,
                robot.getBody().getPosition().y + 32 / PPM,
                POWERUP_FRAME_WIDTH / PPM,
                POWERUP_FRAME_HEIGHT / PPM);

        // for the case that the robot has its full health
        if(initialHelath >= 100) {
            batch.draw(greenBar,
                    robot.getBody().getPosition().x - POWERUP_BAR_OFFSET_X / PPM,
                    robot.getBody().getPosition().y + 32 / PPM + POWERUP_BAR_OFFSET_Y / PPM,
                    POWERUP_BAR_WIDTH / PPM,
                    POWERUP_BAR_HEIGHT / PPM);

            itemPointsToDraw.put(collectableKey, initialHelath + delta);
            if(initialHelath + delta > 100.75f) { // so it will play for 0.75 seconds
                itemPointsToDraw.remove(collectableKey);
            }
        }
        else if(initialHelath < 100) {
            // if initial health increases higher than the actual health, stop the animation
            if(initialHelath > robot.getCheckpointData().getHealth()) {
                itemPointsToDraw.remove(collectableKey);
            }
            else {
                batch.draw(initialHelath >= 45 ? greenBar : redBar, // for powerup animation use green bar for >= 45
                        robot.getBody().getPosition().x - POWERUP_BAR_OFFSET_X / PPM,
                        robot.getBody().getPosition().y + 32 / PPM + POWERUP_BAR_OFFSET_Y / PPM,
                        (POWERUP_BAR_WIDTH * initialHelath / 100) / PPM,
                        POWERUP_BAR_HEIGHT / PPM);

                itemPointsToDraw.put(collectableKey, initialHelath + 30f * delta);
            }
        }
    }

    public ObjectMap<Enemy, Float> getPointsForEnemyToDraw() {
        return pointsForEnemyToDraw;
    }

    public ObjectMap<Damaging, Float> getDamageFromHitToDraw() {
        return damageFromHitToDraw;
    }

    public ObjectMap<Collectable, Float> getItemPointsToDraw() {
        return itemPointsToDraw;
    }

    public void setToNull() {
        robot = null;
        pointsForEnemyToDraw = null;
        damageFromHitToDraw = null;
        itemPointsToDraw = null;
        feedbackFont = null;
        frame = null;
        greenBar = null;
        redBar = null;
        Gdx.app.log("FeedbackRenderer", "Objects were set to null");
    }
}
