package com.robot.game.camera;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.TimeUtils;

public class ShakeEffect {

    private float startTime;
    private float shakeTime;
    private float elapsed;
    private float shakeIntensity;
    private Vector3 cameraDisplacement = new Vector3();
    private boolean shakeON;

    public void shake(float shakeIntensity, float shakeTime) {
        Gdx.app.log("ShakeEffect", "Shake started");
        this.startTime = TimeUtils.nanoTime();
        this.shakeIntensity = shakeIntensity;
        this.shakeTime = shakeTime;
        this.elapsed = 0;
        this.shakeON = true;
    }

    public void update() {

        if(elapsed <= shakeTime) {
            calculateCameraDisplacement();
            elapsed = (TimeUtils.nanoTime() - startTime) * MathUtils.nanoToSec;
        }
        else {
            shakeON = false;
            Gdx.app.log("ShakeEffect", "Shake ended");
        }
    }

    private void calculateCameraDisplacement() {
        cameraDisplacement.x = MathUtils.random(-1f, 1f) * shakeIntensity;
        cameraDisplacement.y = MathUtils.random(-1f, 1f) * shakeIntensity * 4;
    }

    public Vector3 getCameraDisplacement() {
        return cameraDisplacement;
    }

    public boolean isShakeON() {
        return shakeON;
    }

    public void setToNull() {
        cameraDisplacement = null;
        Gdx.app.log("ShakeEffect", "Objects set to null");
    }

}
