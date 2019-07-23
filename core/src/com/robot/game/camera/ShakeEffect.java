package com.robot.game.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Random;

public class ShakeEffect {

    private static float startTime;
    private static float timeToShake;
    private static float elapsed;
    private static float intensity;
    private static float currentIntensity;
    private static Random random;
    private static Vector3 cameraDisplacement;
    private static boolean shakeON;
    private static boolean indefiniteShaking;

    public static void shake(float shakeIntensity, float shakeTime, boolean indefinite) {
        Gdx.app.log("ShakeEffect", "shake()");
        startTime = TimeUtils.nanoTime();
        random = new Random();
        intensity = shakeIntensity;
        timeToShake = shakeTime;
        indefiniteShaking = indefinite;
        elapsed = 0;
        cameraDisplacement = new Vector3();
        shakeON = true;
    }

    public static void update() {

        if(indefiniteShaking && shakeON) {
            calculateCameraDisplacement();
        }
        else if(elapsed <= timeToShake) {
            calculateCameraDisplacement();

            elapsed = (TimeUtils.nanoTime() - startTime) * MathUtils.nanoToSec;
        }
        else {
            shakeON = false;
            timeToShake = 0;
        }
    }

    public static void calculateCameraDisplacement() {
        currentIntensity = intensity /** (timeToShake - elapsed) / timeToShake*/;

        cameraDisplacement.x = (random.nextFloat() * (1 - (-1)) - 1) * currentIntensity;
        cameraDisplacement.y = (random.nextFloat() * (1 - (-1)) - 1) * currentIntensity * 4;
    }

    public static float getTimeToShake() {
        return timeToShake;
    }

    public static Vector3 getCameraDisplacement() {
        return cameraDisplacement;
    }

    public static boolean isShakeON() {
        return shakeON;
    }

    public static void setShakeON(boolean shakeON) {
        ShakeEffect.shakeON = shakeON;
    }
}
