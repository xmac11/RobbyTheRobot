package com.robot.game.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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

    public MainMenuAssets mainMenuAssets;
    public PausePanelAssets pausePanelAssets;
    public GameOverAssets gameOverAssets;
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
    public LoadingScreenFontAssets fontAssets;
    public HudFontAssets hudFontAssets;
    public HpFontAssets hpFontAssets;
    public FeedbackFontAssets feedbackFontAssets;
    public PanelFontAssets panelFontAssets;
    public PanelBigFontAssets panelBigFontAssets;

    // level 2
    public TrampolineAssets trampolineAssets;
    public TankBallAssets tankBallAssets;
    public LaserAssets laserAssets;
    public FishAssets fishAssets;
    public SplashAssets splashAssets;
    public MonsterAssets monsterAssets;
    public SnakeAssets snakeAssets;
    public TrapAssets trapAssets;

    // music
    public SoundAssets soundAssets;
    public MusicAssets musicAssets;

    // game completed
    public GameCompletedAssets gameCompletedAssets;

    // Android
    public AndroidAssets androidAssets;

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

        // hp font
        FreetypeFontLoader.FreeTypeFontLoaderParameter hpFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        hpFont.fontFileName = "blow.ttf";
        hpFont.fontParameters.size = 48;
        hpFont.fontParameters.color = Color.WHITE;
        assetManager.load("hpFont.ttf", BitmapFont.class, hpFont);

        // feedback font
        FreetypeFontLoader.FreeTypeFontLoaderParameter feedbackFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        feedbackFont.fontFileName = "blow.ttf";
        feedbackFont.fontParameters.size = 64;
        feedbackFont.fontParameters.color = Color.WHITE;
        assetManager.load("feedbackFont.ttf", BitmapFont.class, feedbackFont);

        // panel font (menu screen options - pause panel)
        FreetypeFontLoader.FreeTypeFontLoaderParameter panelFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        panelFont.fontFileName = "blow.ttf";
        panelFont.fontParameters.size = 86;
        assetManager.load("panelFont.ttf", BitmapFont.class, panelFont);

        // lost life - game over
        FreetypeFontLoader.FreeTypeFontLoaderParameter panelBigFont = new FreetypeFontLoader.FreeTypeFontLoaderParameter();
        panelBigFont.fontFileName = "blow.ttf";
        panelBigFont.fontParameters.size = 86;
        assetManager.load("panelBigFont.ttf", BitmapFont.class, panelBigFont);


        /* Load tiled map following the procedure described in the LibGDX documentation:
         * https://github.com/libgdx/libgdx/wiki/Tile-maps */
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load(LEVEL_1_TMX, TiledMap.class);
        assetManager.load(LEVEL_2_TMX, TiledMap.class);
        assetManager.load(LEVEL_3_TMX, TiledMap.class);

        // load atlas
        assetManager.load("sprites.pack", TextureAtlas.class);

        // load story and tutorial images
        assetManager.load("tutorial.png", Texture.class);
        assetManager.load("story.png", Texture.class);

        // load images of level 1
        assetManager.load("background.png", Texture.class);
        assetManager.load("barrels.png", Texture.class);

        // load water (level 2)
        assetManager.load("level2/waterAnimation.png", Texture.class);
        assetManager.load("level2/waterAnimationBig.png", Texture.class);

        // load sounds
        loadSounds();

        // load music
        loadMusic();
    }

    private void loadSounds() {
        // common
        assetManager.load("sounds/jump.ogg", Sound.class);
        assetManager.load("sounds/step_on_enemy.ogg", Sound.class);
        assetManager.load("sounds/robot_hurt.ogg", Sound.class);
        assetManager.load("sounds/robot_eat.ogg", Sound.class);
        assetManager.load("sounds/powerup.ogg", Sound.class);

        // level 1
        assetManager.load("sounds/falling_many_pipes.ogg", Sound.class);
        assetManager.load("sounds/falling_pipe.ogg", Sound.class);

        // level 2
        assetManager.load("sounds/laser.ogg", Sound.class);
        assetManager.load("sounds/punch_air.ogg", Sound.class);
        assetManager.load("sounds/punch_enemy.ogg", Sound.class);
        assetManager.load("sounds/trampoline.ogg", Sound.class);
        assetManager.load("sounds/tankball_fire.ogg", Sound.class);
        assetManager.load("sounds/tankball_hit_robot.ogg", Sound.class);
    }

    private void loadMusic() {
        assetManager.load("music/level1_music.ogg", Music.class);
        assetManager.load("music/level2_music.ogg", Music.class);
        assetManager.load("music/level3_music.ogg", Music.class);
    }

    // creates assets for loading screen
    private void createLoadingScreenAssets() {

        // create texture atlas
        TextureAtlas atlas = assetManager.get("loading_bar.pack");

        // create assets
        this.loadingScreenAssets = new LoadingScreenAssets(atlas);
        this.fontAssets = new LoadingScreenFontAssets();
    }

    // creates all assets needed
    public void createGameAssets() {

        // create texture atlas
        TextureAtlas atlas = assetManager.get("sprites.pack");

        // create assets
        this.mainMenuAssets = new MainMenuAssets(atlas);
        this.pausePanelAssets = new PausePanelAssets(atlas);
        this.gameOverAssets = new GameOverAssets(atlas);
        this.tiledMapAssets = new TiledMapAssets();
        this.robotAssets = new RobotAssets(atlas);
        this.batAssets = new BatAssets(atlas);
        this.crabAssets = new CrabAssets(atlas);
        this.interactivePlatformAssets = new InteractivePlatformAssets(atlas);
        this.parallaxAssets = new ParallaxAssets();
        this.hudAssets = new HudAssets(atlas);
        this.collectableAssets = new CollectableAssets(atlas);
        this.pipeAssets = new PipeAssets(atlas);

        // fonts
        this.hudFontAssets = new HudFontAssets();
        this.hpFontAssets = new HpFontAssets();
        this.feedbackFontAssets = new FeedbackFontAssets();
        this.panelFontAssets = new PanelFontAssets();
        this.panelBigFontAssets = new PanelBigFontAssets();

        // level2
        this.trampolineAssets = new TrampolineAssets(atlas);
        this.tankBallAssets = new TankBallAssets(atlas);
        this.laserAssets = new LaserAssets(atlas);
        this.fishAssets = new FishAssets(atlas);
        this.splashAssets = new SplashAssets(atlas);
        this.monsterAssets = new MonsterAssets(atlas);
        this.snakeAssets = new SnakeAssets(atlas);
        this.trapAssets = new TrapAssets(atlas);

        // music
        this.soundAssets = new SoundAssets();
        this.musicAssets = new MusicAssets();

        // android
        this.androidAssets = new AndroidAssets(atlas);
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

        public final TextureRegion robotIdleWithGun;
        public final TextureRegion robotIdleNoGun;

        public final Animation<TextureRegion> shootAnimation;
        public final Animation<TextureRegion> punchAnimation;
        public final Animation<TextureRegion> walkAnimationWithGun;
        public final Animation<TextureRegion> walkAnimationNoGun;
        public final Animation<TextureRegion> climbLadderAnimation;
        public final Animation<TextureRegion> climbRopeAnimation;

        private RobotAssets(TextureAtlas atlas) {
            // idle (with gun)
            this.robotIdleWithGun = atlas.findRegion("robot_idle_gun");

            // idle (no gun)
            this.robotIdleNoGun = atlas.findRegion("robot_idle_no_gun");

            // shooting
            Array<TextureRegion> framesArray = new Array<>();
            framesArray.add(atlas.findRegion("shoot"));
            this.shootAnimation = new Animation<>(0.3f, framesArray);

            framesArray.clear();

            // punching
            for(int i = 1; i <= 2; i++) {
                framesArray.add(atlas.findRegion("punch" + i));
            }
            this.punchAnimation = new Animation<>(0.05f, framesArray);

            framesArray.clear();

            // walking (with gun)
            for(int i = 1; i <= 3; i++) {
                framesArray.add(atlas.findRegion("walk" + i));
            }
            this.walkAnimationWithGun = new Animation<>(0.1f, framesArray, Animation.PlayMode.LOOP_PINGPONG);

            framesArray.clear();

            // walking (no gun)
            for(int i = 1; i <= 3; i++) {
                framesArray.add(atlas.findRegion("walk_no_gun" + i));
            }
            this.walkAnimationNoGun = new Animation<>(0.1f, framesArray, Animation.PlayMode.LOOP_PINGPONG);

            framesArray.clear();

            // climbing ladder
            for(int i = 1; i <= 2; i++) {
                framesArray.add(atlas.findRegion("climb_ladder" + i));
            }
            this.climbLadderAnimation = new Animation<>(0.2f, framesArray, Animation.PlayMode.LOOP);

            framesArray.clear();

            // climbing rope
            for(int i = 1; i <= 2; i++) {
                framesArray.add(atlas.findRegion("climb_rope" + i));
            }
            this.climbRopeAnimation = new Animation<>(0.2f, framesArray, Animation.PlayMode.LOOP);

            framesArray.clear();
        }
    }

    // Bat assets
    public class BatAssets {

        public final Animation<TextureRegion> batFlyAnimation;
        public final Animation<TextureRegion> batDeadAnimation;

        private BatAssets(TextureAtlas atlas) {

            Array<TextureRegion> framesArray = new Array<>();
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

            Array<TextureRegion> framesArray = new Array<>();
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
            Array<TextureRegion> framesArray = new Array<>();
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
            Array<TextureRegion> framesArray = new Array<>();
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
            Array<TextureRegion> framesArray = new Array<>();
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

        public final TextureRegion interactivePlatform;

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

        public final TextureRegion frame;
        public final TextureRegion greenBar;
        public final TextureRegion redBar;
        public final TextureRegion lives;

        private HudAssets(TextureAtlas atlas) {
            this.frame = atlas.findRegion("frame");
            this.greenBar = atlas.findRegion("green");
            this.redBar = atlas.findRegion("red");
            this.lives = atlas.findRegion("lives");
        }
    }

    // Loading bar assets
    public class LoadingScreenAssets {
        public final TextureRegion frame;
        public final TextureRegion bar;

        private LoadingScreenAssets(TextureAtlas atlas) {
            this.frame = atlas.findRegion("loading");
            this.bar = atlas.findRegion("loading_green");
        }
    }

    public class CollectableAssets {

        public final TextureRegion burger;
        public final TextureRegion powerup;
        public final TextureRegion donut_pink;
        public final TextureRegion donut_red;
        public final TextureRegion torch;

        private CollectableAssets(TextureAtlas atlas) {
            this.burger = atlas.findRegion("burger");
            this.powerup = atlas.findRegion("powerup");
            this.donut_pink = atlas.findRegion("donut_pink");
            this.donut_red = atlas.findRegion("donut_red");
            this.torch = atlas.findRegion("torch");
        }
    }

    public class PipeAssets {
        public final TextureRegion debris;

        private PipeAssets(TextureAtlas atlas) {
            this.debris = atlas.findRegion("debris");
        }
    }

    public class TrampolineAssets {
        public final TextureRegion trampolineFull;
        public final TextureRegion trampolineHalf;

        private TrampolineAssets(TextureAtlas atlas) {
            this.trampolineFull = atlas.findRegion("trampoline1");
            this.trampolineHalf = atlas.findRegion("trampoline2");
        }
    }

    public class TankBallAssets {
        public final TextureRegion tankFire;
        public final Animation<TextureRegion> tankExplosionAnimation;

        private TankBallAssets(TextureAtlas atlas) {
            this.tankFire = atlas.findRegion("tankFire");

            Array<TextureRegion> framesArray = new Array<>();
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

            Array<TextureRegion> framesArray = new Array<>();
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
            Array<TextureRegion> framesArray = new Array<>();
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

    public class MainMenuAssets {
        // main menu
        public final TextureRegion mainMenuBG;

        // captcha
        public final TextureRegion robCaptcha;
        public final TextureRegion clickCaptcha;
        public final Animation<TextureRegion> checkAnimation;

        // story
        public final Texture story;

        // tutorial
        public final Texture tutorial;

        private MainMenuAssets(TextureAtlas atlas) {
            // main menu
            this.mainMenuBG = atlas.findRegion("robot_hello");

            // captcha
            this.robCaptcha = atlas.findRegion("robCaptcha");
            this.clickCaptcha = atlas.findRegion("click_captcha");

            Array<TextureRegion> framesArray = new Array<>();
            for(int i = 1; i <= 7; i++) {
                framesArray.add(atlas.findRegion("check" + i));
            }
            this.checkAnimation = new Animation<>(0.15f, framesArray);

            framesArray.clear();

            // story
            this.story = assetManager.get("story.png", Texture.class);

            // tutorial
            this.tutorial = assetManager.get("tutorial.png", Texture.class);
        }
    }

    public class PausePanelAssets {
        public final TextureRegion pausePanel;

        private PausePanelAssets(TextureAtlas atlas) {
            this.pausePanel = atlas.findRegion("pause_panel");
        }
    }

    public class GameOverAssets {
        public final TextureRegion sadFace;

        private GameOverAssets(TextureAtlas atlas) {
            this.sadFace = atlas.findRegion("robot_sad");
        }
    }

    // FONTS

    public class LoadingScreenFontAssets {
        public BitmapFont loadingScreenFont;

        private LoadingScreenFontAssets() {
            this.loadingScreenFont = assetManager.get("font.ttf", BitmapFont.class);
            loadingScreenFont.getData().setScale(1 / 86f);
            loadingScreenFont.setUseIntegerPositions(false);
        }
    }

    public class HudFontAssets {
        public BitmapFont hudFont;

        private HudFontAssets() {
            this.hudFont = assetManager.get("font.ttf", BitmapFont.class);
            hudFont.getData().setScale(1 / 86f / 1.35f);
            hudFont.setUseIntegerPositions(false);
        }
    }

    public class HpFontAssets {
        public BitmapFont hpFont;

        private HpFontAssets() {
            this.hpFont = assetManager.get("hpFont.ttf", BitmapFont.class);
            hpFont.getData().setScale(1 / 86f / 1.35f);
            hpFont.setUseIntegerPositions(false);
        }
    }

    public class FeedbackFontAssets {
        public BitmapFont feedbackFont;

        private FeedbackFontAssets() {
            this.feedbackFont = assetManager.get("feedbackFont.ttf", BitmapFont.class);
            feedbackFont.getData().setScale(1 / 64f / 2);
            feedbackFont.setUseIntegerPositions(false);
        }
    }

    public class PanelFontAssets {
        public BitmapFont panelFont;

        private PanelFontAssets() {
            this.panelFont = assetManager.get("panelFont.ttf", BitmapFont.class);
            panelFont.getData().setScale(1 / 86f);
            panelFont.setUseIntegerPositions(false);
        }
    }

    public class PanelBigFontAssets {
        public BitmapFont panelBigFont;

        private PanelBigFontAssets() {
            this.panelBigFont = assetManager.get("panelBigFont.ttf", BitmapFont.class);
            panelBigFont.getData().setScale(1 / 86f * 1.5f);
            panelBigFont.setUseIntegerPositions(false);
        }
    }

    // MUSIC

    public class SoundAssets {
        // common
        public final Sound jumpSound;
        public final Sound stepOnEnemySound;
        public final Sound robotHurtSound;
        public final Sound robotEatSound;
        public final Sound powerupSound;

        // level 1
        public final Sound fallingManyPipesSound;
        public final Sound fallingPipeSound;

        // level 2
        public final Sound laserSound;
        public final Sound punchAirSound;
        public final Sound punchEnemySound;
        public final Sound trampolineSound;
        public final Sound tankballFireSound;
        public final Sound tankballHitRobotSound;

        private SoundAssets() {
            // common
            this.jumpSound = assetManager.get("sounds/jump.ogg", Sound.class);
            this.stepOnEnemySound = assetManager.get("sounds/step_on_enemy.ogg", Sound.class);
            this.robotHurtSound = assetManager.get("sounds/robot_hurt.ogg", Sound.class);
            this.robotEatSound = assetManager.get("sounds/robot_eat.ogg", Sound.class);
            this.powerupSound = assetManager.get("sounds/powerup.ogg", Sound.class);

            // level 1
            this.fallingManyPipesSound = assetManager.get("sounds/falling_many_pipes.ogg", Sound.class);
            this.fallingPipeSound = assetManager.get("sounds/falling_pipe.ogg", Sound.class);

            // level 2
            this.laserSound = assetManager.get("sounds/laser.ogg", Sound.class);
            this.punchAirSound = assetManager.get("sounds/punch_air.ogg", Sound.class);
            this.punchEnemySound = assetManager.get("sounds/punch_enemy.ogg", Sound.class);
            this.trampolineSound = assetManager.get("sounds/trampoline.ogg", Sound.class);
            this.tankballFireSound = assetManager.get("sounds/tankball_fire.ogg", Sound.class);
            this.tankballHitRobotSound = assetManager.get("sounds/tankball_hit_robot.ogg", Sound.class);
        }
    }

    public class MusicAssets {
        public final Music level1Music;
        public final Music level2Music;
        public final Music level3Music;

        private MusicAssets() {
            this.level1Music = assetManager.get("music/level1_music.ogg", Music.class);
            this.level2Music = assetManager.get("music/level2_music.ogg", Music.class);
            this.level3Music = assetManager.get("music/level3_music.ogg", Music.class);
        }
    }


    // Game completed
    public void loadGameCompletedAssets() {
        // load music
        assetManager.load("music/game_completed_music.ogg", Music.class);

        assetManager.finishLoading(); // blocking statement
        createGameCompletedAssets();

        Gdx.app.log("Assets", "Loaded 'game completed' assets");
    }

    private void createGameCompletedAssets() {
        // create texture atlas
        TextureAtlas atlas = assetManager.get("sprites.pack");

        this.gameCompletedAssets = new GameCompletedAssets(atlas);
    }

    public class GameCompletedAssets {
        public final Music gameCompletedMusic;
        public final TextureRegion gameCompletedPanel;

        private GameCompletedAssets(TextureAtlas atlas) {
            this.gameCompletedMusic = assetManager.get("music/game_completed_music.ogg", Music.class);
            this.gameCompletedPanel = atlas.findRegion("game_completed");
        }
    }

    // Android

    public class AndroidAssets {
        public final TextureRegion right;
        public final TextureRegion left;
        public final TextureRegion up;
        public final TextureRegion down;
        public final TextureRegion jump;

        public final TextureRegion shoot;
        public final TextureRegion punch;

        private AndroidAssets(TextureAtlas atlas) {
            this.right = atlas.findRegion("rightButton");
            this.left = atlas.findRegion("leftButton");
            this.up = atlas.findRegion("upButton");
            this.down = atlas.findRegion("downButton");
            this.jump = atlas.findRegion("jumpButton");

            this.shoot = atlas.findRegion("shootButton");
            this.punch = atlas.findRegion("punchButton");
        }
    }
}
