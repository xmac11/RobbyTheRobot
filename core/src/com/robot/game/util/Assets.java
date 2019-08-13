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

import static com.robot.game.util.Constants.*;

public class Assets {

    private AssetManager assetManager;

    public TiledMapAssets tiledMapAssets;
    public LoadingScreenAssets loadingScreenAssets;
    public RobotAssets robotAssets;
    public BatAssets batAssets;
    public CrabAssets crabAssets;
    public InteractivePlatformAssets interactivePlatformAssets;
    public ParallaxAssets parallaxAssets;
    public HudAssets hudAssets;
    public CollectableAssets collectableAssets;
    public PipeAssets pipeAssets;
    // fonts
    public FontAssets fontAssets;
    public SmallFontAssets smallFontAssets;

    // level 2
    public TrampolineAssets trampolineAssets;
    public TankBallAssets tankBallAssets;
    public LaserAssets laserAssets;
    public FishAssets fishAssets;
    public SplashAssets splashAssets;
    public MonsterAssets monsterAssets;
    public SnakeAssets snakeAssets;
    public TrapAssets trapAssets;

    public Assets() {
        this.assetManager = new AssetManager();
    }

    public AssetManager getAssetManager() {
        return assetManager;
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
        font.fontParameters.size = 86;
        font.fontParameters.color = Color.WHITE;
        assetManager.load("font.ttf", BitmapFont.class, font);

        assetManager.finishLoading(); // blocking statement
        createLoadingScreenAssets();

        //// LOAD ASYNCHRONOUSLY ////

        FreetypeFontLoader.FreeTypeFontLoaderParameter smallFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        smallFont.fontFileName = "blow.ttf";
        smallFont.fontParameters.size = 64;
        smallFont.fontParameters.color = Color.WHITE;
        assetManager.load("smallFont.ttf", BitmapFont.class, smallFont);

        /* Load tiled map following the procedure described in the LibGDX documentation:
         * https://github.com/libgdx/libgdx/wiki/Tile-maps */
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load(LEVEL_1_TMX, TiledMap.class);
        assetManager.load(LEVEL_2_TMX, TiledMap.class);
        assetManager.load(LEVEL_3_TMX, TiledMap.class);

        assetManager.load("sprites.pack", TextureAtlas.class);
        assetManager.load("background.png", Texture.class);
        assetManager.load("barrels.png", Texture.class);

        // load water (level 2)
        assetManager.load("level2/waterAnimation.png", Texture.class);
        assetManager.load("level2/waterAnimationBig.png", Texture.class);
    }

    // creates assets for loading screen
    private void createLoadingScreenAssets() {

        // create texture atlas
        TextureAtlas atlas = assetManager.get("loading_bar.pack");

        // create assets
        this.loadingScreenAssets = new LoadingScreenAssets(atlas);
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
        this.collectableAssets = new CollectableAssets(atlas);
        this.pipeAssets = new PipeAssets(atlas);
        this.smallFontAssets = new SmallFontAssets();

        // level2
        this.trampolineAssets = new TrampolineAssets(atlas);
        this.tankBallAssets = new TankBallAssets(atlas);
        this.laserAssets = new LaserAssets(atlas);
        this.fishAssets = new FishAssets(atlas);
        this.splashAssets = new SplashAssets(atlas);
        this.monsterAssets = new MonsterAssets(atlas);
        this.snakeAssets = new SnakeAssets(atlas);
        this.trapAssets = new TrapAssets(atlas);
    }

    public void dispose() {
        Gdx.app.log("Assets", "AssetManager was disposed");
        assetManager.dispose();
    }

    public class TiledMapAssets {
        public TiledMap tiledMapLevel1;
        public TiledMap tiledMapLevel2;
        public TiledMap tiledMapLevel3;

        public TiledMapAssets() {
            this.tiledMapLevel1 = assetManager.get(LEVEL_1_TMX);
            this.tiledMapLevel2 = assetManager.get(LEVEL_2_TMX);
            this.tiledMapLevel3 = assetManager.get(LEVEL_3_TMX);
        }
    }

    // Robot assets
    public class RobotAssets {

//        public final TextureAtlas.AtlasRegion atlasRegion;
        public final Texture robotTexture;

        public final Animation<Texture> shootAnimation;
        public final Animation<Texture> punchAnimation;

        private RobotAssets(TextureAtlas atlas) {
//            this.atlasRegion = atlas.findRegion("robot");
            this.robotTexture = new Texture("robot.png");

            Array<Texture> framesArray = new Array<>();
            /*for(int i = 1; i <= 2; i ++) {
                framesArray.add(new Texture("level2/shoot" + i + ".png"));
            }*/
            framesArray.add(new Texture("level2/shoot.png"));
            this.shootAnimation = new Animation<>(0.3f, framesArray);

            framesArray.clear();

            for(int i = 1; i <= 2; i++) {
                framesArray.add(new Texture("level2/punch" + i + ".png"));
            }
            this.punchAnimation = new Animation<>(0.05f, framesArray);
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

            framesArray.clear();
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

            framesArray.clear();
        }
    }

    public class FishAssets {
        public final Animation<TextureRegion> fishAnimation;
        public final TextureRegion deadFish;

        private FishAssets(TextureAtlas atlas) {
            Array<TextureAtlas.AtlasRegion> framesArray = new Array<>();
            for(int i = 1; i <= 2; i++) {
                framesArray.add(atlas.findRegion("fish" + i));
            }
            this.fishAnimation = new Animation<>(0.15f, framesArray);

            framesArray.clear();

            this.deadFish = atlas.findRegion("fish_dead");
        }
    }

    public class MonsterAssets {
        public final Animation<TextureRegion> monsterWalkAnim;
        public final Animation<TextureRegion> monsterAttackAnim;
        public final Animation<TextureRegion> monsterDeadAnim;

        private MonsterAssets(TextureAtlas atlas) {

            // walk
            Array<TextureAtlas.AtlasRegion> framesArray = new Array<>();
            for(int i = 1; i <= 7; i++) {
                framesArray.add(atlas.findRegion("monster_walk" + i));
            }
            this.monsterWalkAnim = new Animation<>(0.1f, framesArray, Animation.PlayMode.LOOP_PINGPONG);

            framesArray.clear();

            // attack
            for(int i = 1; i <= 7; i++) {
                framesArray.add(atlas.findRegion("monster_attack" + i));
            }
            this.monsterAttackAnim = new Animation<>(0.1f, framesArray, Animation.PlayMode.LOOP);

            framesArray.clear();

            // dead
            for(int i = 1; i <=5; i++) {
                framesArray.add(atlas.findRegion("monster_dead" + i));
            }
            this.monsterDeadAnim = new Animation<>(0.15f, framesArray);

            framesArray.clear();
        }
    }

    public class SnakeAssets {
        public final Animation<TextureRegion> slitherAnimation;
        public final Animation<TextureRegion> biteAnimation;
        public final Animation<TextureRegion> deadAnimation;

        private SnakeAssets(TextureAtlas atlas) {

            // slither
            Array<TextureAtlas.AtlasRegion> framesArray = new Array<>();
            for(int i = 1; i <= 5; i++) {
                framesArray.add(atlas.findRegion("snake_slither" + i));
            }
            this.slitherAnimation = new Animation<>(0.1f, framesArray, Animation.PlayMode.LOOP_PINGPONG);

            framesArray.clear();

            // bite
            for(int i = 1; i <= 5; i++) {
                framesArray.add(atlas.findRegion("snake_bite" + i));
            }
            this.biteAnimation = new Animation<>(0.1f, framesArray, Animation.PlayMode.LOOP);

            framesArray.clear();

            // dead
            for(int i = 1; i <= 2; i++) {
                framesArray.add(atlas.findRegion("snake_dead" + i));
            }
            this.deadAnimation = new Animation<>(0.1f, framesArray);

            framesArray.clear();
        }
    }

    public class InteractivePlatformAssets {

        public final TextureAtlas.AtlasRegion interactivePlatform;

        public InteractivePlatformAssets(TextureAtlas atlas) {
            this.interactivePlatform = atlas.findRegion("movingplatform");
        }
    }

    // Parallax assets
    public class ParallaxAssets {
        public final Texture backgroundTexture;
        public final Texture barrelsTexture;
        public final Texture waterTexture;
        public final Texture waterTextureBig;

        private ParallaxAssets() {
            this.backgroundTexture = assetManager.get("background.png");
            this.barrelsTexture = assetManager.get("barrels.png");
            this.waterTexture = assetManager.get("level2/waterAnimation.png");
            this.waterTextureBig = assetManager.get("level2/waterAnimationBig.png");
        }
    }

    // Hud assets
    public class HudAssets {

        public final TextureAtlas.AtlasRegion frame;
        public final TextureAtlas.AtlasRegion greenBar;
        public final TextureAtlas.AtlasRegion redBar;
        public final TextureAtlas.AtlasRegion lives;
        public GlyphLayout scoreGlyphLayout;
        public GlyphLayout livesGlyphLayout;

        private HudAssets(TextureAtlas atlas) {
            this.frame = atlas.findRegion("frame");
            this.greenBar = atlas.findRegion("green");
            this.redBar = atlas.findRegion("red");
            this.lives = atlas.findRegion("lives");

            // GlyphLayout for alignment
            this.scoreGlyphLayout = new GlyphLayout();
            String text = "SCORE";
            scoreGlyphLayout.setText(fontAssets.font, text);

            // GlyphLayout for alignment
            this.livesGlyphLayout = new GlyphLayout();
            String text2 = "x3";
            livesGlyphLayout.setText(fontAssets.font, text2);
        }
    }

    // Loading bar assets
    public class LoadingScreenAssets {
        public final TextureAtlas.AtlasRegion frame;
        public final TextureAtlas.AtlasRegion bar;

        private LoadingScreenAssets(TextureAtlas atlas) {
            this.frame = atlas.findRegion("loading");
            this.bar = atlas.findRegion("loading_green");
        }
    }

    public class CollectableAssets {

        public final TextureAtlas.AtlasRegion burger;
        public final TextureAtlas.AtlasRegion powerup;
        public final TextureAtlas.AtlasRegion donut_pink;
        public final TextureAtlas.AtlasRegion donut_red;
        public final TextureAtlas.AtlasRegion torch;

        private CollectableAssets(TextureAtlas atlas) {
            this.burger = atlas.findRegion("burger");
            this.powerup = atlas.findRegion("powerup");
            this.donut_pink = atlas.findRegion("donut_pink");
            this.donut_red = atlas.findRegion("donut_red");
            this.torch = atlas.findRegion("torch");
        }
    }

    public class PipeAssets {
        public final TextureAtlas.AtlasRegion debris;

        private PipeAssets(TextureAtlas atlas) {
            this.debris = atlas.findRegion("debris");
        }
    }

    public class TrampolineAssets {
        public final TextureAtlas.AtlasRegion trampolineFull;
        public final TextureAtlas.AtlasRegion trampolineHalf;

        private TrampolineAssets(TextureAtlas atlas) {
            this.trampolineFull = atlas.findRegion("trampoline1");
            this.trampolineHalf = atlas.findRegion("trampoline2");
        }
    }

    public class TankBallAssets {
        public final TextureAtlas.AtlasRegion tankFire;
        public final Animation<TextureRegion> tankExplosionAnimation;

        private TankBallAssets(TextureAtlas atlas) {
            this.tankFire = atlas.findRegion("tankFire");

            Array<TextureAtlas.AtlasRegion> framesArray = new Array<>();
            for(int i = 1; i <= 6; i++) {
                framesArray.add(atlas.findRegion("tankExplosion" + i));
            }
            this.tankExplosionAnimation = new Animation<>(0.1f, framesArray);

            framesArray.clear();
        }
    }

    public class LaserAssets {

        public final Animation<TextureRegion> laserExplosionAnimation;

        private LaserAssets(TextureAtlas atlas) {

            Array<TextureAtlas.AtlasRegion> framesArray = new Array<>();
            for(int i = 1; i <= 4; i++) {
                framesArray.add(atlas.findRegion("laserExplosion" + i));
            }
            this.laserExplosionAnimation = new Animation<>(0.05f, framesArray);

            framesArray.clear();
        }
    }

    public class SplashAssets {
        public final Animation<TextureRegion> splashAnimation;

        private SplashAssets(TextureAtlas atlas) {
            Array<TextureAtlas.AtlasRegion> framesArray = new Array<>();
            for(int i = 1; i <= 7; i++) {
                framesArray.add(atlas.findRegion("splash" + i));
            }
            this.splashAnimation = new Animation<>(0.05f, framesArray);

            framesArray.clear();
        }
    }

    public class TrapAssets {
        public final TextureRegion trapSpikes;
        public final TextureRegion trapBase;
        public final TextureRegion trapStick;

        private TrapAssets(TextureAtlas atlas) {
            this.trapSpikes = atlas.findRegion("trap_vertical");
            this.trapBase = atlas.findRegion("trap_vertical_base");
            this.trapStick = atlas.findRegion("trap_vertical_stick");
        }
    }

    // FONTS

    public class FontAssets {
        public BitmapFont font;

        private FontAssets() {
            this.font = assetManager.get("font.ttf", BitmapFont.class);
            font.getData().setScale(1 / 86f);
            font.setUseIntegerPositions(false);
        }
    }

    public class SmallFontAssets {
        public BitmapFont smallFont;

        private SmallFontAssets() {
            this.smallFont = assetManager.get("smallFont.ttf", BitmapFont.class);
            smallFont.getData().setScale(1 / 64f / 2);
            smallFont.setUseIntegerPositions(false);
        }
    }
}
