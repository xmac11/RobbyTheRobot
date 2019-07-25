package com.robot.game.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class ShakeEffect {

    private static float startTime;
    private static float timeToShake;
    private static float elapsed;
    private static float intensity;
    private static Vector3 cameraDisplacement;
    private static boolean shakeON;

    public static void shake(float shakeIntensity, float shakeTime) {
        Gdx.app.log("ShakeEffect", "Shake started");
        startTime = TimeUtils.nanoTime();
        intensity = shakeIntensity;
        timeToShake = shakeTime;
        elapsed = 0;
        cameraDisplacement = new Vector3();
        shakeON = true;
    }

    public static void update() {

        if(elapsed <= timeToShake) {
            calculateCameraDisplacement();
            elapsed = (TimeUtils.nanoTime() - startTime) * MathUtils.nanoToSec;
        }
        else {
            shakeON = false;
            Gdx.app.log("ShakeEffect", "Shake ended");
        }
    }

    public static void calculateCameraDisplacement() {
        cameraDisplacement.x = MathUtils.random(-1f, 1f) * intensity;
        cameraDisplacement.y = MathUtils.random(-1f, 1f) * intensity * 4;
    }

    public static Vector3 getCameraDisplacement() {
        return cameraDisplacement;
    }

    public static boolean isShakeON() {
        return shakeON;
    }

}
