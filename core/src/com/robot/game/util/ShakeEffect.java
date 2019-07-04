package com.robot.game.util;

import com.badlogic.gdx.math.Vector3;

import java.util.Random;

public class ShakeEffect {

    private static float timeLeft;
    private static float elapsed;
    private static float intensity;
    private static float currentIntensity;
    private static Random random;
    private static Vector3 position;

    public static void shake(float shakeIntensity, float shakeTime) {
        random = new Random();
        intensity = shakeIntensity;
        timeLeft = shakeTime;
        elapsed = 0;
        position = new Vector3();
    }

    public static void update(float delta) {
        if (elapsed <= timeLeft) {
            currentIntensity = intensity * (timeLeft - elapsed) / timeLeft;

            position.x = (random.nextFloat() - 0.5f) * 4 * currentIntensity;
            //                position.y = (random.nextFloat() - 0.5f) * 4 * currentPower;

            elapsed += delta;
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
