package com.james.tools;

import java.util.ArrayList;
import java.util.List;

public class ThreadManager {

    private static final List<Action> executeOnMainThread = new ArrayList<>();
    private static final List<Action> executeCopiedOnMainThread = new ArrayList<>();
    private static volatile boolean actionToExecuteOnMainThread = false;

    public static void executeOnMainThread(Action action) {
        if (action == null) {
            System.out.println("No action to execute on main thread!");
            return;
        }

        synchronized (executeOnMainThread) {
            executeOnMainThread.add(action);
            actionToExecuteOnMainThread = true;
        }
    }

    public static void updateMain() {
        if (actionToExecuteOnMainThread) {
            executeCopiedOnMainThread.clear();

            synchronized (executeOnMainThread) {
                executeCopiedOnMainThread.addAll(executeOnMainThread);
                executeOnMainThread.clear();
                actionToExecuteOnMainThread = false;
            }

            for (Action action : executeCopiedOnMainThread) {
                action.invoke();
            }
        }
    }

    public interface Action {
        void invoke();
    }

}
