package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;

public class Assets {

    private static final Assets instance =  new Assets();

    public AssetManager assetManager = new AssetManager();

    public RobotAssets robotAssets;
    public ParallaxAssets parallaxAssets;

    private Assets() {
    }

    public static Assets getInstance() {
        return instance;
    }

    public void load() {
        assetManager.load("sprites.pack", TextureAtlas.class);
        assetManager.load("background.png", Texture.class);
        assetManager.load("barrels.png", Texture.class);
        assetManager.finishLoading();

        TextureAtlas atlas = new TextureAtlas("sprites.pack");

        this.robotAssets = new RobotAssets(atlas);
        this.parallaxAssets = new ParallaxAssets();
    }

    public void dispose() {
        assetManager.dispose();
        System.out.println("AssetManager was disposed");
    }

    // Robot assets
    public class RobotAssets {

        public final TextureAtlas.AtlasRegion atlasRegion;

        private RobotAssets(TextureAtlas atlas) {
            this.atlasRegion = atlas.findRegion("robot");
        }
    }

    // Parallax assets
    public class ParallaxAssets {
        public final Texture backgroundTexture;
        public final Texture barrelsTexture;

        private ParallaxAssets() {
            this.backgroundTexture = new Texture(Gdx.files.internal("background.png"));
            this.barrelsTexture = new Texture(Gdx.files.internal("barrels.png"));
        }
    }
}
