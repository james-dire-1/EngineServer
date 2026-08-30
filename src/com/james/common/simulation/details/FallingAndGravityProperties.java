package com.james.common.simulation.details;

public class FallingAndGravityProperties {

    public float inclinationForLowGravityThresholdNotMoving = 40.0f;
    public float inclinationForHighGravityThresholdNotMoving = 60.0f;
    public float lowGravityNotMoving = -0.05f;
    public float highGravityNotMoving = -9.5f;

    public float inclinationForLowGravityThresholdWhileMoving = 30.0f;
    public float inclinationForHighGravityThresholdWhileMoving = 60.0f;
    public float lowGravityWhileMoving = -3.0f;
    public float highGravityWhileMoving = -9.5f;

    public float maxSecondsNeededToRecoverFromFall = 4.0f;

    public float jumpSpeed = 30.0f;
    public float minJumpDisplacementBeforeRecovery = -12.0f;

    public float getTargetGravityVelocityForInclinationAndMoving(float inclination, boolean moving) {
        if (!moving) {
            if (inclination <= inclinationForLowGravityThresholdNotMoving) return lowGravityNotMoving;
            if (inclination >= inclinationForHighGravityThresholdNotMoving) return highGravityNotMoving;

            float deltaThresholds = inclinationForHighGravityThresholdNotMoving - inclinationForLowGravityThresholdNotMoving;
            float deltaProgress = inclination - inclinationForLowGravityThresholdNotMoving;
            float normalizedProgress = deltaProgress / deltaThresholds;

            float deltaGravity = highGravityNotMoving - lowGravityNotMoving;
            return lowGravityNotMoving + normalizedProgress * deltaGravity;
        } else {
            if (inclination <= inclinationForLowGravityThresholdWhileMoving) return lowGravityWhileMoving;
            if (inclination >= inclinationForHighGravityThresholdWhileMoving) return highGravityWhileMoving;

            float deltaThresholds = inclinationForHighGravityThresholdWhileMoving - inclinationForLowGravityThresholdWhileMoving;
            float deltaProgress = inclination - inclinationForLowGravityThresholdWhileMoving;
            float normalizedProgress = deltaProgress / deltaThresholds;

            float deltaGravity = highGravityWhileMoving - lowGravityWhileMoving;
            return lowGravityWhileMoving + normalizedProgress * deltaGravity;
        }
    }

}
