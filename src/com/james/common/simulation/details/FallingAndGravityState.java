package com.james.common.simulation.details;

import com.james.common.simulation.LevelProperties;
import com.james.common.simulation.collisionEngine.math.containers.CollisionDetails;
import com.james.serverSide.simulation.objects.MovableObject;
import org.lwjgl.util.vector.Vector3f;

public class FallingAndGravityState {

    private static final float EPSILON = 0.005f;

    public Vector3f getVelocityDueToGravity() { return velocityDueToGravity; }
    public State getState() { return state; }

    private final FallingAndGravityProperties fgProperties;
    private final MovableObject movableObject;
    private final Vector3f velocityDueToGravity = new Vector3f();

    private State state = State.FALLING;
    private int ticksElapsed;
    private float yWhenBeganJump;

    public FallingAndGravityState(FallingAndGravityProperties fgProperties, MovableObject movableObject) {
        this.fgProperties = fgProperties;
        this.movableObject = movableObject;
    }

    public void attemptToJump() {
        if (state == State.ON_GROUND_FIRMLY) {
            state = State.BEGAN_JUMP;
            ticksElapsed = 0;
            velocityDueToGravity.y = fgProperties.jumpSpeed;
            yWhenBeganJump = movableObject.getPosition().y;
        }
    }

    public void update(LevelProperties levelProperties) {
        CollisionDetails collisionDetails = movableObject.getCollisionDetails();
        Vector3f velocity = movableObject.getVelocity();
        boolean moving = Math.abs(velocity.x) > EPSILON || Math.abs(velocity.y) > EPSILON || Math.abs(velocity.z) > EPSILON;
        float targetGravityVelocity = fgProperties.getTargetGravityVelocityForInclinationAndMoving(collisionDetails.inclinationAngle, moving);

        if (state == State.FALLING) {
            ticksElapsed++;

            if (collisionDetails.onGround) {
                if (ticksElapsed > 5) {
                    state = State.ON_GROUND_RECOVERING;
                    ticksElapsed = 0;
                } else {
                    state = State.ON_GROUND_FIRMLY;
                }
            } else {
                velocityDueToGravity.y += levelProperties.gravityAcceleration * levelProperties.secondsPerGameTick;

                if (velocityDueToGravity.y < levelProperties.terminalVelocity) {
                    velocityDueToGravity.y = levelProperties.terminalVelocity;
                }
            }
        }

        else if (state == State.ON_GROUND_RECOVERING) {
            ticksElapsed++;

            if (!collisionDetails.onGround) {
                state = State.FALLING;
                ticksElapsed = 0;
            } else if (ticksElapsed * levelProperties.secondsPerGameTick >= fgProperties.maxSecondsNeededToRecoverFromFall) {
                state = State.ON_GROUND_FIRMLY;
            } else {
                if (velocityDueToGravity.y >= targetGravityVelocity - EPSILON) {
                    velocityDueToGravity.y = targetGravityVelocity;
                    state = State.ON_GROUND_FIRMLY;
                } else {
                    velocityDueToGravity.y -= levelProperties.gravityAcceleration * levelProperties.secondsPerGameTick;

                    if (velocityDueToGravity.y > targetGravityVelocity) {
                        velocityDueToGravity.y = targetGravityVelocity;
                    }
                }
            }
        }

        else if (state == State.ON_GROUND_FIRMLY) {
            velocityDueToGravity.y = targetGravityVelocity;

            if (!collisionDetails.onGround) {
                state = State.FALLING;
                ticksElapsed = 0;
            }
        }

        else if (state == State.BEGAN_JUMP) {
            ticksElapsed++;

            if (ticksElapsed >= 3) {
                state = State.FALLING_FROM_JUMP;
            }
        }

        else if (state == State.FALLING_FROM_JUMP) {
            if (collisionDetails.onGround) {
                float jumpDisplacement = movableObject.getPosition().y - yWhenBeganJump;

                if (jumpDisplacement < fgProperties.minJumpDisplacementBeforeRecovery) {
                    state = State.ON_GROUND_RECOVERING;
                    ticksElapsed = 0;
                } else {
                    state = State.ON_GROUND_FIRMLY;
                }
            } else {
                velocityDueToGravity.y += levelProperties.gravityAcceleration * levelProperties.secondsPerGameTick;

                if (velocityDueToGravity.y < levelProperties.terminalVelocity) {
                    velocityDueToGravity.y = levelProperties.terminalVelocity;
                }
            }
        }
    }

    public enum State {
        FALLING,
        ON_GROUND_RECOVERING,
        ON_GROUND_FIRMLY,
        BEGAN_JUMP,
        FALLING_FROM_JUMP
    }

}
