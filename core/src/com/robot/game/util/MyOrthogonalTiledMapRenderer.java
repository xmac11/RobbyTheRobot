package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.tiles.AnimatedTiledMapTile;

/* This class is used for pausing any animations handled internally by Tiled. Adapted from:
 * https://gamedev.stackexchange.com/questions/174325/pause-tiled-animation-during-game-in-libgdx */

public class MyOrthogonalTiledMapRenderer extends OrthogonalTiledMapRenderer {

    private boolean mapAnimationActive;

    public MyOrthogonalTiledMapRenderer(TiledMap map) {
        super(map);
        this.mapAnimationActive = true;
    }

    public MyOrthogonalTiledMapRenderer(TiledMap map, Batch batch) {
        super(map, batch);
        this.mapAnimationActive = true;
    }

    public MyOrthogonalTiledMapRenderer(TiledMap map, float unitScale) {
        super(map, unitScale);
        this.mapAnimationActive = true;
    }

    public MyOrthogonalTiledMapRenderer(TiledMap map, float unitScale, Batch batch) {
        super(map, unitScale, batch);
        this.mapAnimationActive = true;
    }

    @Override
    protected void beginRender() {
        if(mapAnimationActive) {
            AnimatedTiledMapTile.updateAnimationBaseTime();
        }
        batch.begin();
    }

    public boolean isMapAnimationActive() {
        return mapAnimationActive;
    }

    public void setMapAnimationActive(boolean mapAnimationActive) {
        this.mapAnimationActive = mapAnimationActive;
        Gdx.app.log("MyOrthogonalTiledMapRenderer", "animationActive  = " + mapAnimationActive);
    }
}
