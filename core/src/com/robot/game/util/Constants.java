package com.robot.game.util;

import com.badlogic.gdx.math.Vector2;

public final class Constants {

    public static final boolean DEBUG_ON = false;

    // Screen - Camera
    public static final float SCREEN_WIDTH = 768;
    public static final float SCREEN_HEIGHT = 432;
    public static final float PPM = 32;
    public static final float DEBUG_CAM_SPEED = 32;

    // Tiled map editor
    public static final String LEVEL_1_TMX = "level1.tmx";
    public static final String LEVEL_2_TMX = "level2/level2.tmx";
    public static final String FOLDER_NAME = "files/";

    public static final String GROUND_OBJECT = "Ground obj";
    public static final String LADDER_OBJECT = "Ladder obj";
    public static final String BAT_OBJECT = "Bat obj";
    public static final String CRAB_OBJECT = "Crab obj";
    public static final String FISH_OBJECT = "Fish obj";
    public static final String SPIKE_OBJECT = "Spike obj";
    public static final String COLLECTABLE_OBJECT = "Collectable obj";
    public static final String WALL_OBJECT = "Wall obj";

    // COLLISIONS

    // Box2D filter category bits
    public static final short ROBOT_CATEGORY = 1;
    public static final short GROUND_CATEGORY = 2;
    public static final short LADDER_CATEGORY = 4;
    public static final short INTERACTIVE_PLATFORM_CATEGORY = 8;
    public static final short ROBOT_FEET_CATEGORY = 16;
    public static final short ENEMY_CATEGORY = 32;
    public static final short SPIKE_CATEGORY = 64;
    public static final short COLLECTABLE_CATEGORY = 128;
    public static final short PIPE_CATEGORY = 256;
    public static final short PIPE_ON_GROUND_CATEGORY = 512;
    public static final short WALLJUMP_CATEGORY = 1024;
    public static final short TRAMPOLINE_CATEGORY = 2048;
    public static final short ENEMY_PROJECTILE_CATEGORY = 4096;

    // Box2D filter mask bits
    public static final short NOTHING_MASK = 0;
    public static final short ROBOT_MASK = GROUND_CATEGORY | LADDER_CATEGORY | INTERACTIVE_PLATFORM_CATEGORY | ENEMY_CATEGORY
                                            | SPIKE_CATEGORY | COLLECTABLE_CATEGORY | PIPE_CATEGORY | PIPE_ON_GROUND_CATEGORY
                                            | WALLJUMP_CATEGORY | TRAMPOLINE_CATEGORY | ENEMY_PROJECTILE_CATEGORY;
    public static final short GROUND_MASK = ROBOT_CATEGORY | ROBOT_FEET_CATEGORY /*| ENEMY_CATEGORY*/ | COLLECTABLE_CATEGORY | PIPE_CATEGORY | PIPE_ON_GROUND_CATEGORY;
    public static final short LADDER_MASK = ROBOT_CATEGORY;
    public static final short INTERACTIVE_PLATFORM_MASK = ROBOT_CATEGORY | ROBOT_FEET_CATEGORY;
    public static final short ROBOT_FEET_MASK = GROUND_CATEGORY | INTERACTIVE_PLATFORM_CATEGORY | PIPE_CATEGORY | PIPE_ON_GROUND_CATEGORY;
    public static final short ENEMY_MASK = ROBOT_CATEGORY /*| GROUND_CATEGORY*/;
    public static final short SPIKE_MASK = ROBOT_CATEGORY;
    public static final short COLLECTABLE_MASK = ROBOT_CATEGORY | GROUND_CATEGORY;
    public static final short PIPE_MASK = ROBOT_CATEGORY | GROUND_CATEGORY | PIPE_CATEGORY | ROBOT_FEET_CATEGORY | PIPE_ON_GROUND_CATEGORY;
    public static final short WALLJUMP_MASK = ROBOT_CATEGORY;
    public static final short TRAMPOLINE_MASK = ROBOT_CATEGORY;
    public static final short ENEMY_PROJECTILE_MASK = ROBOT_CATEGORY;
    public static final short DEBUG_MASK = GROUND_CATEGORY | LADDER_CATEGORY | INTERACTIVE_PLATFORM_CATEGORY;

    // Robot

        // Dimensions
    public static final float ROBOT_RADIUS = 16;
    public static final float ROBOT_BODY_WIDTH = 18;
    public static final float ROBOT_BODY_HEIGHT = 50;
    public static final float ROBOT_OFFSET_X = 10;
    public static final float ROBOT_OFFSET_Y = 5;
    public static final float ROBOT_SPRITE_WIDTH = ROBOT_BODY_WIDTH + ROBOT_OFFSET_X;
    public static final float ROBOT_SPRITE_HEIGHT = ROBOT_BODY_HEIGHT + ROBOT_OFFSET_Y;
    public static final Vector2 SPAWN_LOCATION = new Vector2(32 / PPM, 160 / PPM);
    public static final Vector2 FIRST_CHECKPOINT_LOCATION_L1 = new Vector2(2100 / PPM, 160 / PPM);
    public static final Vector2 SECOND_CHECKPOINT_LOCATION_L1 = new Vector2(2880 / PPM, 416 / PPM);
    public static final Vector2 THIRD_CHECKPOINT_LOCATION_L1 = new Vector2(4240 / PPM, 208 / PPM);
    public static final float CHECKPOINT_TOLERANCE = 48;

        // Spike respawn locations
//    public static final Vector2 SPIKE_RESPAWN_1 = new Vector2(1120 / PPM, 336 / PPM);

        // Sensor feet
    public static final float ROBOT_FEET_WIDTH = ROBOT_BODY_WIDTH - 0.5f;
    public static final float ROBOT_FEET_HEIGHT = 4;

        // Movement
    public static final float ROBOT_MAX_HOR_SPEED = 5;
    public static final float BREAK_GROUND_FACTOR = 0.96f;
    public static final float BREAK_AIR_FACTOR = 0.99f;
    public static final float ROBOT_CLIMB_SPEED = 3;
    public static final float ROBOT_JUMP_SPEED = 5;
    public static final float ROBOT_JUMP_TIMER = 0.2f;
    public static final float ROBOT_COYOTE_TIMER = 0.15f;
    public static final float ROBOT_JUMP_TIMEOUT = 0.3f;
    public static final float ROBOT_CLIMB_TIMER = 0.3f;
    public static final Vector2 LEFT_IMPULSE_ON_MOVING_PLATFORM = new Vector2(-0.15f, 0); // this is for the case of the horizontally moving platform to the right
    public static final Vector2 WALL_JUMPING_IMPULSE = new Vector2(6, 4.5f);

        // Flickering
    public static final float FLICKER_TIME = 0.75f;

    // Interactive platforms
    public static final String LADDER_PROPERTY = "ladder";
    public static final String LADDER_CORE_DESCRIPTION = "core";
    public static final String LADDER_BOTTOM_DESCRIPTION = "bottom";
    public static final String INTERACTIVE_PLATFORM_PROPERTY = "interactivePlatform";
    public static final String FALLING_PLATFORM_PROPERTY = "falling";
    public static final String MOVING_PLATFORM_PROPERTY = "moving";
    public static final String WALL_JUMPING_PROPERTY = "wallJumping";
    public static final String TRAMPOLINE_PROPERTY = "trampoline";

    // Enemies
    public static final String ENEMY_PROPERTY = "enemy";
    public static final String BAT_PROPERTY = "bat";
    public static final String CRAB_PROPERTY = "crab";
    public static final String FISH_PROPERTY = "fish";
    public static final String SPIKE_PROPERTY = "spike";
    public static final float DEAD_TIMER = 1;

    // Bat
    public static final float BAT_WIDTH = 28;
    public static final float BAT_HEIGHT = 16;

        // Crab
    public static final float CRAB_WIDTH = 24;
    public static final float CRAB_HEIGHT = 16;

        // Fish
    public static final float FISH_WIDTH = 16;
    public static final float FISH_HEIGHT = 28;
    public static final Vector2 FISH_IMPULSE = new Vector2(0, 8f);

    // Collectables
    public static final String COLLECTABLE_PROPERTY = "collectable";
    public static final String POWERUP_PROPERTY = "powerup";
    public static final String BURGER_PROPERTY = "burger";
    public static final String COLLECTABLE_SPAWNING_PROPERTY = "shouldSpawn";
    public static final float BURGER_WIDTH = 20;
    public static final float BURGER_HEIGHT = 16;
    public static final float POWERUP_WIDTH = 20;
    public static final float POWERUP_HEIGHT = 22;
    public static final float FULL_HEAL_WIDTH = 26;
    public static final float FULL_HEAL_HEIGHT = 28.5f;

    // Hud
    public static final float PADDING = 8;
        // health frame
    public static final float FRAME_OFFSET = 32;
    public static final float FRAME_WIDTH = 137.5f;
    public static final float FRAME_HEIGHT = 32;
        // health bar
    public static final float BAR_OFFSET_X = 38;
    public static final float BAR_OFFSET_Y = 27;
    public static final float BAR_WIDTH = 95;
    public static final float BAR_HEIGHT = 20;
        // lives
    public static final float LIVES_WIDTH = 25.3f;
    public static final float LIVES_HEIGHT = 32;


    // Loading screen
        // frame
    public static final float LOADING_FRAME_WIDTH = 550;
    public static final float LOADING_FRAME_HEIGHT = 53;
        // bar
    public static final float LOADING_BAR_OFFSET_X = 265;
    public static final float LOADING_BAR_OFFSET_Y = 20f;
    public static final float LOADING_BAR_WIDTH = 530;
    public static final float LOADING_BAR_HEIGHT = 40;
        // progress (LoadingScreen)
    public static final float LOADING_FONT_OFFSET_Y = 11.5f;

    // Damage
    public static final int DAMAGE_FROM_BAT = 25;
    public static final int DAMAGE_FROM_CRAB = 20;
    public static final int DAMAGE_FROM_FISH = 20;
    public static final int DAMAGE_FROM_SPIKE = 25;
    public static final int DAMAGE_FROM_PIPE = 25;
    public static final int DAMAGE_FROM_ENEMY_PROJECTILE = 25;

    // Shake when damage
    public static final float HIT_SHAKE_INTENSITY = 0.15f;
    public static final float HIT_SHAKE_TIME = 0.2f;

    // Points - Health
    public static final int POINTS_FOR_BAT = 100;
    public static final int POINTS_FOR_CRAB = 50;
    public static final int POINTS_FOR_FISH = 50;
    public static final int POINTS_FOR_BURGER = 25;
    public static final int HEALTH_FOR_POWERUP = 25;

    // Earthquake
    public static final float EARTH_SHAKE_INTENSITY = 0.25f;
    public static final float EARTH_SHAKE_TIME = 2;

    // Falling pipes
    public static final float PIPE_WIDTH = 8;
    public static final float PIPE_HEIGHT = 32;
    public static final Vector2[] PIPE_VERTICES = new Vector2[]{new Vector2(-4 / PPM, 14 / PPM),
                                                                new Vector2(0 / PPM, 16 / PPM),
                                                                new Vector2(4 / PPM, 14 / PPM),
                                                                new Vector2(4 / PPM, -14 / PPM),
                                                                new Vector2(0 / PPM, -16 / PPM),
                                                                new Vector2(-4 / PPM, -14 / PPM)};
    public static final float PIPES_START_X = 4992;
    public static final float PIPES_END_X = 6528;
    public static final float PIPES_SPAWNING_PERIOD = 1.5f;

    // FeedbackRenderer (health bar for powerup)
    public static final float POWERUP_FRAME_WIDTH = FRAME_WIDTH / 2f;
    public static final float POWERUP_FRAME_HEIGHT = FRAME_HEIGHT / 2f;
    // health bar
    public static final float POWERUP_BAR_OFFSET_X = 16;
    public static final float POWERUP_BAR_OFFSET_Y = 2.5f;
    public static final float POWERUP_BAR_WIDTH = BAR_WIDTH / 2f;
    public static final float POWERUP_BAR_HEIGHT = BAR_HEIGHT / 2f;

    // Trampoline
    public static final float TRAMPOLINE_WIDTH = 32f;
    public static final float TRAMPOLINE_HEIGHT = 32f;
    public static final Vector2 TRAMPOLINE_IMPULSE = new Vector2(0, 9.5f);

    // Tank
    public static final float TANKBALL_WIDTH = 16;
    public static final float TANKBALL_HEIGHT = 24;
    public static final float TANKBALL_SPWANING_PERIOD = 1.5f;
    public static final Vector2 TANKBALL_IMPULSE = new Vector2(0, 10);

}
