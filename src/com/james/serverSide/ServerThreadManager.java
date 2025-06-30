package com.james.serverSide;

import com.james.serverSide.simulation.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A way to queue up actions to run on desired Level threads.
 * @see Level
 */
public class ServerThreadManager {

    private static final Map<Level, List<Action>> levelToActionsMap = new HashMap<>();
    private static final Map<Level, Boolean> actionToExecuteMap = new HashMap<>();

    private static final Object lock = new Object();

    /**
     * Queue up an action to be run on a desired Level thread.
     *
     * @implNote Also writes true to the actionToExecute map, so that we know that there is at least one
     * action that must be executed on that thread.
     */
    public static void executeOnALevelThread(Level level, Action action) {
        if (level == null) {
            throw new RuntimeException("The Level object passed in is null!");
        }
        if (action == null) {
            System.out.println("No action to execute on a level thread!");
            return;
        }

        synchronized (lock) {
            if (levelToActionsMap.containsKey(level)) {
                List<Action> batch = levelToActionsMap.get(level);
                batch.add(action);
            } else {
                List<Action> newBatch = new ArrayList<>();
                newBatch.add(action);
                levelToActionsMap.put(level, newBatch);
            }

            actionToExecuteMap.put(level, true);
        }
    }

    /**
     * Retrieves the actions that must be run on a given Level thread. Called by the Level thread.
     *
     * @param actionsCopied The list of actions, which are cleared and written to. This is done to not
     *                      have to instantiate a new List every time the method is called.
     * @return Whether there are actions to be executed. Thus, if this is false, nothing was written to
     * the actionsCopied list, and it was not cleared.
     */
    public static boolean getActionsForLevel(Level level, List<Action> actionsCopied) {
        synchronized (lock) {
            boolean actionToExecute;

            if (actionToExecuteMap.containsKey(level) && actionToExecuteMap.get(level)) {
                actionsCopied.clear();

                List<Action> actions = levelToActionsMap.get(level);
                actionsCopied.addAll(actions);
                actions.clear();
                actionToExecuteMap.put(level, false);

                actionToExecute = true;
            } else {
                actionToExecute = false;
            }

            return actionToExecute;
        }
    }

    public interface Action {
        void invoke();
    }

}
