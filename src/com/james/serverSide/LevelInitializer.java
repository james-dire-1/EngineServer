package com.james.serverSide;

import com.james.serverSide.simulation.Level;
import templates.serverSide.communication.ServerPacketSendEvents;

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
    private final Scene scene;

    public LevelInitializer(ServerPacketSendEvents events, Scene scene) {
        lastInstance = this;

        this.thread = new Thread(this);
        this.level = new Level(scene.name(), scene.primarySpawnPoint(), events);
        this.scene = scene;

        thread.start();
    }

    @Override
    public void run() {
        scene.onStartup(level);

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

}
