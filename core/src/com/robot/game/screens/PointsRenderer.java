package com.robot.game.screens;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ObjectMap;
import com.robot.game.sprites.Collectable;
import com.robot.game.sprites.Enemy;
import com.robot.game.util.Assets;
import com.robot.game.util.StaticMethods;

import static com.robot.game.util.Constants.POINTS_FOR_COLLECTABLE;
import static com.robot.game.util.Constants.PPM;

public class PointsRenderer {

    private ObjectMap<Enemy, Float> enemyPointsToDraw;
    private ObjectMap<Collectable, Float> itemPointsToDraw;
    private BitmapFont plusPointsFont;

    public PointsRenderer() {
        this.enemyPointsToDraw = new ObjectMap<>();
        this.itemPointsToDraw = new ObjectMap<>();
        this.plusPointsFont = Assets.getInstance().smallFontAssets.smallFont;
    }

    public void draw(SpriteBatch batch, float delta) {

        for(Enemy enemyKey: enemyPointsToDraw.keys()) {

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

        ///////////
        for(Collectable collectableKey: itemPointsToDraw.keys()) {

            float alpha = itemPointsToDraw.get(collectableKey);
            plusPointsFont.setColor(1, 1, 1, alpha);

            if(alpha > 0) {
                plusPointsFont.draw(batch,
                        String.valueOf(POINTS_FOR_COLLECTABLE),
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


    }

    public ObjectMap<Enemy, Float> getEnemyPointsToDraw() {
        return enemyPointsToDraw;
    }

    public ObjectMap<Collectable, Float> getItemPointsToDraw() {
        return itemPointsToDraw;
    }
}
