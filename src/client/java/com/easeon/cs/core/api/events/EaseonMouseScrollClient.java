package com.easeon.cs.core.api.events;

import com.easeon.cs.core.api.definitions.enums.EventPhase;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class EaseonMouseScrollClient {
    private static final List<MouseScrollTask> _beforeTasks = new CopyOnWriteArrayList<>();
    private static final List<MouseScrollTask> _afterTasks = new CopyOnWriteArrayList<>();
    private static final Map<Object, MouseScrollTask> _methodRefTasks = new HashMap<>();

    @FunctionalInterface
    public interface MouseScrollRunnable {
        boolean run(long window, double horizontal, double vertical);
    }

    public static MouseScrollTask register(MouseScrollRunnable task) {
        return register(EventPhase.AFTER, task);
    }

    public static MouseScrollTask register(EventPhase phase, MouseScrollRunnable task) {
        MouseScrollTask scrollTask = new MouseScrollTask(phase, task);
        if (phase == EventPhase.BEFORE) {
            _beforeTasks.add(scrollTask);
        } else {
            _afterTasks.add(scrollTask);
        }
        _methodRefTasks.put(task, scrollTask);
        return scrollTask;
    }

    public static void register(MouseScrollTask task) {
        List<MouseScrollTask> targetList = task.getPhase() == EventPhase.BEFORE ? _beforeTasks : _afterTasks;
        if (!targetList.contains(task)) {
            targetList.add(task);
        }
    }

    public static void unregister(MouseScrollTask task) {
        _beforeTasks.remove(task);
        _afterTasks.remove(task);
    }

    public static void unregister(MouseScrollRunnable task) {
        MouseScrollTask scrollTask = _methodRefTasks.remove(task);
        if (scrollTask != null) {
            _beforeTasks.remove(scrollTask);
            _afterTasks.remove(scrollTask);
        }
    }

    public static boolean onMouseScrollBefore(long window, double horizontal, double vertical) {
        return executeTasks(_beforeTasks, window, horizontal, vertical, "BEFORE");
    }

    public static boolean onMouseScrollAfter(long window, double horizontal, double vertical) {
        return executeTasks(_afterTasks, window, horizontal, vertical, "AFTER");
    }

    public static boolean onMouseScroll(long window, double horizontal, double vertical) {
        return onMouseScrollAfter(window, horizontal, vertical);
    }

    private static boolean executeTasks(List<MouseScrollTask> tasks, long window, double horizontal, double vertical, String phase) {
        boolean shouldCancel = false;
        for (MouseScrollTask task : tasks) {
            try {
                boolean result = task.execute(window, horizontal, vertical);
                if (result) {
                    shouldCancel = true;
                }
            } catch (Exception e) {
                // logger.error("Error in MouseScroll task ({}): {}", phase, e);
            }
        }
        return shouldCancel;
    }

    public static class MouseScrollTask {
        private final EventPhase _phase;
        private final MouseScrollRunnable _task;

        public MouseScrollTask(EventPhase phase, MouseScrollRunnable task) {
            this._phase = phase;
            this._task = task;
        }

        public EventPhase getPhase() {
            return _phase;
        }

        public boolean execute(long window, double horizontal, double vertical) {
            return _task.run(window, horizontal, vertical);
        }
    }
}