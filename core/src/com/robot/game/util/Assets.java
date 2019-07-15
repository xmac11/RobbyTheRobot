package com.robot.game.util;

import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class Assets implements Disposable{

    private static final Assets instance =  new Assets();

    public AssetManager assetManager = new AssetManager();

    public RobotAssets robotAssets;

    private Assets() {
    }

    public static Assets getInstance() {
        return instance;
    }

    public void load() {
        assetManager.load("sprites.pack", TextureAtlas.class);
        assetManager.finishLoading();

        TextureAtlas atlas = new TextureAtlas("sprites.pack");

        this.robotAssets = new RobotAssets(atlas);
    }

    @Override
    public void dispose() {
        assetManager.dispose();
        System.out.println("AssetManager was disposed");
    }

    public class RobotAssets {

        public final TextureAtlas.AtlasRegion region;

        public RobotAssets(TextureAtlas atlas) {
            this.region = atlas.findRegion("robot");
        }
    }
}
