package com.robot.game.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import com.robot.game.sprites.Burger;
import com.robot.game.sprites.Collectable;
import com.robot.game.sprites.Enemy;
import com.robot.game.sprites.Robot;
import com.robot.game.util.Assets;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.*;

public class PointsRenderer {

    private Robot robot;
    private Assets assets;
    private ObjectMap<Enemy, Float> enemyPointsToDraw;
    private ObjectMap<Collectable, Float> itemPointsToDraw;
    private BitmapFont plusPointsFont;
    private TextureRegion frame;
    private TextureRegion greenBar;
    private TextureRegion redBar;

    public PointsRenderer(Robot robot) {
        this.robot = robot;
        this.assets = robot.getScreenLevel1().getAssets();
        this.enemyPointsToDraw = new ObjectMap<>();
        this.itemPointsToDraw = new ObjectMap<>();
        this.plusPointsFont = assets.smallFontAssets.smallFont;

        this.frame = assets.hudAssets.frame;
        this.greenBar = assets.hudAssets.greenBar;
        this.redBar = assets.hudAssets.redBar;
    }

    public void draw(SpriteBatch batch, float delta) {

        // draw any points gained from killing enemies
        for(Enemy enemyKey: enemyPointsToDraw.keys()) {
            renderPointsFromEnemy(batch, delta, enemyKey);
        }

        // draw any points gained from collecting items
        for(Collectable collectableKey: itemPointsToDraw.keys()) {

            // if collectable is a burger
            if(collectableKey instanceof Burger) {
                renderPointsFromBurger(batch, delta, collectableKey);
            }

            // else if collectable is a powerup
            else {
                renderPowerupHealthbar(batch, delta, collectableKey);
            }
        }
    }

    private void renderPointsFromEnemy(SpriteBatch batch, float delta, Enemy enemyKey) {
        float alpha = enemyPointsToDraw.get(enemyKey);
        plusPointsFont.setColor(1, 1, 1, alpha);

        if(alpha > 0) {
            plusPointsFont.draw(batch,
                    String.valueOf( StaticMethods.getPointsForEnemy(enemyKey) ),
                    enemyKey.getBody().getPosition().x,
                    enemyKey.getBody().getPosition().y + 24 / PPM + (1 - alpha) / 1.5f,
                    0,
                    Align.center,
                    false);

            enemyPointsToDraw.put(enemyKey, alpha - /*1.5f **/ delta);
        }
        else {
            enemyPointsToDraw.remove(enemyKey);
        }
    }

    private void renderPointsFromBurger(SpriteBatch batch, float delta, Collectable collectableKey) {
        float alpha = itemPointsToDraw.get(collectableKey);
        plusPointsFont.setColor(1, 1, 1, alpha);

        if(alpha > 0) {
            plusPointsFont.draw(batch,
                    String.valueOf(POINTS_FOR_BURGER),
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
            if(initialHelath + delta > 100.75f) { // so it will play for 1 seconds
                itemPointsToDraw.remove(collectableKey);
            }
        }
        else if(initialHelath < 100) {
            // if initial health increases higher than the actual health, stop the animation
            if(initialHelath > robot.getCheckpointData().getHealth()) {
                itemPointsToDraw.remove(collectableKey);
            }
            else {
                batch.draw(initialHelath >= 40 ? greenBar : redBar,
                        robot.getBody().getPosition().x - POWERUP_BAR_OFFSET_X / PPM,
                        robot.getBody().getPosition().y + 32 / PPM + POWERUP_BAR_OFFSET_Y / PPM,
                        (POWERUP_BAR_WIDTH * initialHelath / 100) / PPM,
                        POWERUP_BAR_HEIGHT / PPM);

                itemPointsToDraw.put(collectableKey, initialHelath + 30f * delta);
            }
        }
    }

    public ObjectMap<Enemy, Float> getEnemyPointsToDraw() {
        return enemyPointsToDraw;
    }

    public ObjectMap<Collectable, Float> getItemPointsToDraw() {
        return itemPointsToDraw;
    }
}
