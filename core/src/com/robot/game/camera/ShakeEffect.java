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
    private static float currentIntensity;
    private static Vector3 cameraDisplacement;
    private static boolean shakeON;
    private static boolean indefiniteShaking;

    public static void shake(float shakeIntensity, float shakeTime, boolean indefinite) {
        Gdx.app.log("ShakeEffect", "Shake started");
        startTime = TimeUtils.nanoTime();
        intensity = shakeIntensity;
        timeToShake = shakeTime;
        indefiniteShaking = indefinite;
        elapsed = 0;
        cameraDisplacement = new Vector3();
        shakeON = true;
    }

    public static void update() {

        /*if(indefiniteShaking && shakeON) {
            calculateCameraDisplacement();
        }
        else*/ if(elapsed <= timeToShake) {
            calculateCameraDisplacement();

            elapsed = (TimeUtils.nanoTime() - startTime) * MathUtils.nanoToSec;
        }
        else {
            shakeON = false;
            //timeToShake = 0;
            Gdx.app.log("ShakeEffect", "Shake ended");
        }
    }

    public static void calculateCameraDisplacement() {
        currentIntensity = intensity /** (timeToShake - elapsed) / timeToShake*/;

        cameraDisplacement.x = MathUtils.random(-1f, 1f) * currentIntensity;
        cameraDisplacement.y = MathUtils.random(-1f, 1f) * currentIntensity * 4;
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
