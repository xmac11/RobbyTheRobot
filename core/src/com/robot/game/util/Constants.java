package com.robot.game.util;

import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Filter;

public final class Constants {

    public static boolean debug_on = true;

    // Screen - Camera
    public static final float SCREEN_WIDTH = 768;
    public static final float SCREEN_HEIGHT = 432;
    public static final float PPM = 32;
    public static final float DEBUG_CAM_SPEED = 32;


    // Tiled map editor
    private static TiledMap tiledMap = new TmxMapLoader().load("level1.1.tmx");
    private static MapProperties mapProperties = tiledMap.getProperties();
    public static final int TILE_SIZE = mapProperties.get("tilewidth", Integer.class);
//    public static final int TILE_SIZE = 16;
    public static final float MAP_WIDTH = mapProperties.get("width", Integer.class) * TILE_SIZE;
    public static final float MAP_HEIGHT = mapProperties.get("height", Integer.class) * TILE_SIZE;
    public static final String GROUND_OBJECT = "Ground obj";
    public static final String LADDER_OBJECT = "Ladder obj";
    public static final String BAT_OBJECT = "Bat obj";
    public static final String CRAB_OBJECT = "Crab obj";
    public static final String SPIKE_OBJECT = "Spike obj";

    // COLLISIONS

    // Box2D filter category bits
    public static final short ROBOT_CATEGORY = 1;
    public static final short GROUND_CATEGORY = 2;
    public static final short LADDER_CATEGORY = 4;
    public static final short FALLING_PLATFORM_CATEGORY = 8;
    public static final short MOVING_PLATFORM_CATEGORY = 16;
    public static final short ROBOT_FEET_CATEGORY = 32;
    public static final short ENEMY_CATEGORY = 64;
    public static final short SPIKE_CATEGORY = 128;

    // Box2D filter mask bits
    public static final short NOTHING_MASK = 0;
    public static final short ROBOT_MASK = GROUND_CATEGORY | LADDER_CATEGORY | FALLING_PLATFORM_CATEGORY | MOVING_PLATFORM_CATEGORY | ENEMY_CATEGORY | SPIKE_CATEGORY;
    public static final short ROBOT_HURT_MASK = GROUND_CATEGORY | LADDER_CATEGORY | FALLING_PLATFORM_CATEGORY | MOVING_PLATFORM_CATEGORY | SPIKE_CATEGORY;
    public static final short GROUND_MASK = ROBOT_CATEGORY | ROBOT_FEET_CATEGORY | ENEMY_CATEGORY;
    public static final short LADDER_MASK = ROBOT_CATEGORY;
    public static final short FALLING_PLATFORM_MASK = ROBOT_CATEGORY | ROBOT_FEET_CATEGORY;
    public static final short MOVING_PLATFORM_MASK = ROBOT_CATEGORY | ROBOT_FEET_CATEGORY;
    public static final short ROBOT_FEET_MASK = GROUND_CATEGORY | FALLING_PLATFORM_CATEGORY | MOVING_PLATFORM_CATEGORY;
    public static final short ENEMY_MASK = ROBOT_CATEGORY | GROUND_CATEGORY;
    public static final short ENEMY_HIT_ROBOT_MASK = GROUND_CATEGORY;
    public static final short SPIKE_MASK = ROBOT_CATEGORY;
    public static final short DEBUG_MASK = GROUND_CATEGORY | LADDER_CATEGORY | FALLING_PLATFORM_CATEGORY | MOVING_PLATFORM_CATEGORY;



    // Robot

        // Dimensions
    public static final float ROBOT_RADIUS = 16;
    public static final float ROBOT_BODY_WIDTH = 18;
    public static final float ROBOT_BODY_HEIGHT = 50;
    public static final float ROBOT_OFFSET_X = 10;
    public static final float ROBOT_OFFSET_Y = 5;
    public static final float ROBOT_SPRITE_WIDTH = ROBOT_BODY_WIDTH + ROBOT_OFFSET_X;
    public static final float ROBOT_SPRITE_HEIGHT = ROBOT_BODY_HEIGHT + ROBOT_OFFSET_Y;

        // Sensor feet
    public static final float ROBOT_FEET_WIDTH = ROBOT_BODY_WIDTH - 0.5f;
    public static final float ROBOT_FEET_HEIGHT = 4;
        // Movement
    public static final float ROBOT_MAX_HOR_SPEED = 5;
    public static final float ROBOT_CLIMB_SPEED = 3;
    public static final float ROBOT_JUMP_TIMER = 0.2f;
    public static final float ROBOT_COYOTE_TIMER = 0.15f;
    public static final float ROBOT_JUMP_TIMEOUT = 0.3f;
    public static final float ROBOT_JUMP_SPEED = 5.0f; // when on ground
    public static final Vector2 ROBOT_JUMP_IMPULSE = new Vector2(0, 5.1f); // when on interactive platform
//    public static final Vector2 ROBOT_JUMP_IMPULSE_FALLING_PLAT = new Vector2(0, 10f); // when platform is falling


    // Interactive platforms
    public static final String LADDER_PROPERTY = "ladder";
    public static final String LADDER_CORE_DESCRIPTION = "core";
    public static final String LADDER_BOTTOM_DESCRIPTION = "bottom";
    public static final String FALLING_PLATFORM_PROPERTY = "falling";
    public static final String MOVING_PLATFORM_PROPERTY = "moving";

    public static final float JUMP_HEIGHT = 34 / PPM;
    public static final float TIME_UNTIL_JUMP_HEIGHT = .5f;

    public static final float DISTANCE_TO_PEAK = 32;

    // Enemies
    public static final String ENEMY_PROPERTY = "enemy";
    public static final String BAT_PROPERTY = "bat";
    public static final String CRAB_PROPERTY = "crab";
    public static final String SPIKE_PROPERTY = "spike";

        // Bat
    public static final float BAT_WIDTH = 28;
    public static final float BAT_HEIGHT = 16;

        // Crab
    public static final float CRAB_WIDTH = 24;
    public static final float CRAB_HEIGHT = 16;

    // Asset files
//    public static final String

}
