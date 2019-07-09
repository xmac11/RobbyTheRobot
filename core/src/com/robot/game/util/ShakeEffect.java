package com.robot.game.util;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

public class ShakeEffect {

    private static float startTime;
    private static float timeLeft;
    private static float elapsed;
    private static float intensity;
    private static float currentIntensity;
    private static Random random;
    private static Vector3 position;

    public static void shake(float shakeIntensity, float shakeTime) {
        startTime = TimeUtils.nanoTime();
        random = new Random();
        intensity = shakeIntensity;
        timeLeft = shakeTime;
        elapsed = 0;
        position = new Vector3();
    }

    public static void update() {
        if(elapsed <= timeLeft) {
            currentIntensity = intensity/* * (timeLeft - elapsed) / timeLeft*/;

            position.x = (random.nextFloat() - 0.5f) * currentIntensity;
            //position.y = (random.nextFloat() - 0.5f) * currentPower;

            elapsed = (TimeUtils.nanoTime() - startTime) * MathUtils.nanoToSec;
        }
        else
            timeLeft = 0;
    }

    public static float randomRotation() {
        return (new Random().nextFloat() - 0.5f) * 0.25f;
    }

    public static float getTimeLeft() {
        return timeLeft;
    }

    public static Vector3 getPosition() {
        return position;
    }
}
