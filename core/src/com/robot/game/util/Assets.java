package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.utils.Array;

import static com.robot.game.util.Constants.LEVEL_1_TMX;

public class Assets {

    private static final Assets instance =  new Assets();

    public AssetManager assetManager = new AssetManager();

    public TiledMapAssets tiledMapAssets;
    public LoadingBarAssets loadingBarAssets;
    public FontAssets fontAssets;
    public RobotAssets robotAssets;
    public BatAssets batAssets;
    public CrabAssets crabAssets;
    public InteractivePlatformAssets interactivePlatformAssets;
    public ParallaxAssets parallaxAssets;
    public HudAssets hudAssets;
    public CollectableAssets collectableAssets;

    private Assets() {
    }

    public static Assets getInstance() {
        return instance;
    }

    public void load() {

        //// LOAD SYNCHRONOUSLY ////

        assetManager.load("loading_bar.pack", TextureAtlas.class);

        /* Load fonts following the procedure described in the LibGDX documentation:
         * https://github.com/libgdx/libgdx/wiki/Managing-your-assets */
        FileHandleResolver resolver = new InternalFileHandleResolver();
        assetManager.setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
        assetManager.setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));

        FreetypeFontLoader.FreeTypeFontLoaderParameter font = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        font.fontFileName = "blow.ttf";
        font.fontParameters.size = 64;
        font.fontParameters.color = Color.WHITE;
        assetManager.load(font.fontFileName, BitmapFont.class, font);

        assetManager.finishLoading(); // blocking statement
        createLoadingScreenAssets();

        //// LOAD ASYNCHRONOUSLY ////

        /* Load tiled map following the procedure described in the LibGDX documentation:
         * https://github.com/libgdx/libgdx/wiki/Tile-maps */
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load(LEVEL_1_TMX, TiledMap.class);

        assetManager.load("sprites.pack", TextureAtlas.class);
        assetManager.load("background.png", Texture.class);
        assetManager.load("barrels.png", Texture.class);
    }

    // creates assets for loading screen
    public void createLoadingScreenAssets() {

        // create texture atlas
        TextureAtlas atlas = assetManager.get("loading_bar.pack");

        // create assets
        this.loadingBarAssets = new LoadingBarAssets(atlas);
        this.fontAssets = new FontAssets();
    }

    // creates all assets needed
    public void createGameAssets() {

        // create texture atlas
        TextureAtlas atlas = assetManager.get("sprites.pack");

        // create assets
        this.tiledMapAssets = new TiledMapAssets();
        this.robotAssets = new RobotAssets(atlas);
        this.batAssets = new BatAssets(atlas);
        this.crabAssets = new CrabAssets(atlas);
        this.interactivePlatformAssets = new InteractivePlatformAssets(atlas);
        this.parallaxAssets = new ParallaxAssets();
        this.hudAssets = new HudAssets(atlas);
        this.collectableAssets = new CollectableAssets();
    }

    public void dispose() {
        Gdx.app.log("Assets", "AssetManager was disposed");
        assetManager.dispose();
    }

    public class TiledMapAssets {
        public TiledMap tiledMap;

        public TiledMapAssets() {
            this.tiledMap = assetManager.get(LEVEL_1_TMX);
        }
    }

    // Robot assets
    public class RobotAssets {

        public final TextureAtlas.AtlasRegion atlasRegion;

        private RobotAssets(TextureAtlas atlas) {
            this.atlasRegion = atlas.findRegion("robot");
        }
    }

    // Bat assets
    public class BatAssets {

        public final Animation<TextureRegion> batFlyAnimation;
        public final Animation<TextureRegion> batDeadAnimation;

        private BatAssets(TextureAtlas atlas) {

            Array<TextureAtlas.AtlasRegion> framesArray = new Array<>();
            for(int i = 1; i <= 5; i++) {
                framesArray.add(atlas.findRegion("bat" + i));
            }
            this.batFlyAnimation = new Animation<>(0.05f, framesArray, Animation.PlayMode.LOOP_PINGPONG);

            framesArray.clear();

            for(int i = 1; i <= 2; i++) {
                framesArray.add(atlas.findRegion("bat_dead" + i));
            }
            this.batDeadAnimation = new Animation<>(0.3f, framesArray, Animation.PlayMode.LOOP_PINGPONG);
        }
    }

    // Crab assets
    public class CrabAssets {

        public final Animation<TextureRegion> crabWalkAnimation;
        public final Animation<TextureRegion> crabDeadAnimation;

        private CrabAssets(TextureAtlas atlas) {

            Array<TextureAtlas.AtlasRegion> framesArray = new Array<>();
            for(int i = 1; i <= 4; i++) {
                framesArray.add(atlas.findRegion("crab" + i));
            }
            this.crabWalkAnimation = new Animation<>(0.05f, framesArray, Animation.PlayMode.LOOP_PINGPONG);

            framesArray.clear();

            for(int i = 1; i <= 2; i++) {
                framesArray.add(atlas.findRegion("crab_dead" + i));
            }
            this.crabDeadAnimation = new Animation<>(0.3f, framesArray, Animation.PlayMode.LOOP_PINGPONG);
        }
    }

    public class InteractivePlatformAssets {

        public final TextureAtlas.AtlasRegion atlasRegion;

        public InteractivePlatformAssets(TextureAtlas atlas) {
            this.atlasRegion = atlas.findRegion("movingplatform");
        }
    }

    // Parallax assets
    public class ParallaxAssets {
        public final Texture backgroundTexture;
        public final Texture barrelsTexture;

        private ParallaxAssets() {
            this.backgroundTexture = assetManager.get("background.png");
            this.barrelsTexture = assetManager.get("barrels.png");
        }
    }

    // Hud assets
    public class HudAssets {

        public final TextureAtlas.AtlasRegion frame;
        public final TextureAtlas.AtlasRegion greenBar;
        public final TextureAtlas.AtlasRegion redBar;
        public final Texture lives; // add this to atlas when finalized
        //        public BitmapFont scoreFont;
        public GlyphLayout scoreGlyphLayout;

        private HudAssets(TextureAtlas atlas) {
            this.frame = atlas.findRegion("frame");
            this.greenBar = atlas.findRegion("green");
            this.redBar = atlas.findRegion("red");
            this.lives = new Texture("lives.png"); // add this to atlas when finalized

            // GlyphLayout for alignment
            this.scoreGlyphLayout = new GlyphLayout();
            String text = "SCORE";
            scoreGlyphLayout.setText(fontAssets.font, text);
        }
    }

    // Loading bar assets
    public class LoadingBarAssets {
        public final TextureAtlas.AtlasRegion frame;
        public final TextureAtlas.AtlasRegion bar;

        private  LoadingBarAssets(TextureAtlas atlas) {
            this.frame = atlas.findRegion("loading");
            this.bar = atlas.findRegion("loading_green");
        }
    }

    public class FontAssets {
        public BitmapFont font;
        public GlyphLayout glyphLayout;

        private FontAssets() {
            this.font = assetManager.get("blow.ttf", BitmapFont.class);
            font.getData().setScale(1 / 64f);
            font.setUseIntegerPositions(false);

            // GlyphLayout for alignment
            this.glyphLayout = new GlyphLayout();
            String text = "x3";
            glyphLayout.setText(font, text);
        }
    }

    public class CollectableAssets {

        public Texture burgerTexture;

        private CollectableAssets() {
            this.burgerTexture = new Texture("burger.png"); // add this to atlas when finalized
        }
    }
}
