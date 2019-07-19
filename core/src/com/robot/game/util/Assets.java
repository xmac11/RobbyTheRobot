package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.utils.Array;

public class Assets {

    private static final Assets instance =  new Assets();

    public AssetManager assetManager = new AssetManager();

    public RobotAssets robotAssets;
    public BatAssets batAssets;
    public CrabAssets crabAssets;
    public InteractivePlatformAssets interactivePlatformAssets;
    public ParallaxAssets parallaxAssets;
    public HudAssets hudAssets;

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

        // create texture atlas
        TextureAtlas atlas = new TextureAtlas("sprites.pack");

        // create assets
        this.robotAssets = new RobotAssets(atlas);
        this.batAssets = new BatAssets(atlas);
        this.crabAssets = new CrabAssets(atlas);
        this.interactivePlatformAssets = new InteractivePlatformAssets(atlas);
        this.parallaxAssets = new ParallaxAssets();
        this.hudAssets = new HudAssets(atlas);
    }

    public void dispose() {
        assetManager.dispose();
        Gdx.app.log("Assets", "AssetManager was disposed");
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
            this.backgroundTexture = new Texture(Gdx.files.internal("background.png"));
            this.barrelsTexture = new Texture(Gdx.files.internal("barrels.png"));
        }
    }

    public class HudAssets {

        public final TextureAtlas.AtlasRegion frame;
        public final TextureAtlas.AtlasRegion greenBar;
        public final TextureAtlas.AtlasRegion redBar;
        public final Texture lives;
        public BitmapFont font;
        public GlyphLayout glyphLayout;

        private HudAssets(TextureAtlas atlas) {
            this.frame = atlas.findRegion("frame");
            this.greenBar = atlas.findRegion("green");
            this.redBar = atlas.findRegion("red");
            this.lives = new Texture("lives.png"); // add this to atlas when finalized

            generateFont();
        }

        private void generateFont() {
            FreeTypeFontGenerator fontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("blow.ttf"));
            FreeTypeFontGenerator.FreeTypeFontParameter fontParameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            fontParameter.size = 64;
            fontParameter.color = Color.WHITE;
            this.font = fontGenerator.generateFont(fontParameter);

            //        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
            font.getData().setScale(1 / 64f);
            font.setUseIntegerPositions(false);

            String text = "x3";
            this.glyphLayout = new GlyphLayout();
            glyphLayout.setText(font, text);


            fontGenerator.dispose();
        }


    }
}
