package com.robot.game.util;

import com.robot.game.entities.Robot;

public class HandReference {


    private Robot robot;
    private String description;

    public HandReference(Robot robot, String description) {
        this.robot = robot;
        this.description = description;
    }

    public Robot getRobot() {
        return robot;
    }

    public String getDescription() {
        return description;
    }
}
