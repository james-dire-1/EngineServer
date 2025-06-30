package com.james.common.simulation.objects;

/**
 * Used to categorize PhysicalObjects. This is sent to clients when an PhysicalObject gets added to a
 * Level server side so that the client knows what model should be used for rendering.
 */
public enum PhysicalObjectType {
    Wall,
    TestEnvironment,
    Other
}
