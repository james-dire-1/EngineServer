package com.james.serverSide;

import com.james.common.simulation.objects.PhysicalObjectType;
import com.james.serverSide.simulation.Level;
import com.james.serverSide.simulation.collisionEngine.hitboxes.AABBHitbox;
import com.james.serverSide.simulation.objects.MovableObject;
import com.james.serverSide.simulation.objects.PhysicalObject;
import templates.serverSide.communication.ServerPacketSendEvents;
import org.lwjgl.util.vector.Vector3f;

/**
 * Class that handles the management of a Level instance. That is, it creates a new Level when constructed,
 * and creates a new Thread for the game loop on which the Level will be run. Calls the Level's update()
 * method every game tick.
 * @see Level
 */
public class LevelInitializer implements Runnable {

    public static boolean isOnlineGame;

    public static LevelInitializer lastInstance;

    public volatile boolean shouldRun = true;
    public final Thread thread;

    private final Level level;

    public LevelInitializer(String levelName, ServerPacketSendEvents events) {
        LevelInitializer.lastInstance = this;

        this.level = new Level(levelName, events);
        this.thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        onStartup();

        long lastTime = System.nanoTime();
        while (shouldRun) {
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (System.nanoTime() - lastTime >= level.secondsPerGameTick * 1_000_000_000) {
                lastTime = System.nanoTime();
                level.update();
            }
        }
    }

    public void onStartup() {
        for (int i = 0; i < 10; i++) {
            PhysicalObject wall = new PhysicalObject(PhysicalObjectType.Wall, new Vector3f(-i*2, 0, -10), new Vector3f(0, 0, 0), 1);
            level.add(wall);
        }

        PhysicalObject testEnvironment = new PhysicalObject(PhysicalObjectType.TestEnvironment, new Vector3f(), new Vector3f(), 1);
        level.add(testEnvironment);

        AABBHitbox aabbHitbox = new AABBHitbox(testEnvironment, "/test-environment.dae");
        level.addAABBHitbox(aabbHitbox);

        MovableObject abstractArt = new MovableObject(level, PhysicalObjectType.Other, new Vector3f(0, 5, 0), new Vector3f(), 1) {
            private boolean firstTime = true;

            @Override
            public boolean update() {
                rotate(0, 10, 0);

                return super.update();
            }

            @Override
            public void moveUpdate() {
                if (firstTime) {
                    firstTime = false;
                    isAffectedByGravity = false;
                    setVelocity(6, 0, 0);
                }

                if (getPosition().x >= 10) {
                    setVelocity(-6, 0, 0);
                } else if (getPosition().x <= -10) {
                    setVelocity(6, 0, 0);
                }

                super.moveUpdate();
            }
        };

        level.add(abstractArt);
    }

}
