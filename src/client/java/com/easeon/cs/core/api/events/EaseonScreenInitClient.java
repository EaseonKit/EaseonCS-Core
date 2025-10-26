package com.easeon.cs.core.api.events;

import com.easeon.cs.core.api.definitions.enums.EventPhase;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class EaseonScreenInitClient {
    private static final List<ScreenInitTask> _beforeTasks = new CopyOnWriteArrayList<>();
    private static final List<ScreenInitTask> _afterTasks = new CopyOnWriteArrayList<>();
    private static final Map<Object, ScreenInitTask> _methodRefTasks = new HashMap<>();

    @FunctionalInterface
    public interface ScreenInitRunnable {
        void run(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight);
    }

    public static ScreenInitTask register(ScreenInitRunnable task) {
        return register(EventPhase.AFTER, task);
    }

    public static ScreenInitTask register(EventPhase phase, ScreenInitRunnable task) {
        ScreenInitTask screenInitTask = new ScreenInitTask(phase, task);
        if (phase == EventPhase.BEFORE) {
            _beforeTasks.add(screenInitTask);
        } else {
            _afterTasks.add(screenInitTask);
        }
        _methodRefTasks.put(task, screenInitTask);
        return screenInitTask;
    }

    public static void register(ScreenInitTask task) {
        List<ScreenInitTask> targetList = task.getPhase() == EventPhase.BEFORE ? _beforeTasks : _afterTasks;
        if (!targetList.contains(task)) {
            targetList.add(task);
        }
    }

    public static void unregister(ScreenInitTask task) {
        _beforeTasks.remove(task);
        _afterTasks.remove(task);
    }

    public static void unregister(ScreenInitRunnable task) {
        ScreenInitTask screenInitTask = _methodRefTasks.remove(task);
        if (screenInitTask != null) {
            _beforeTasks.remove(screenInitTask);
            _afterTasks.remove(screenInitTask);
        }
    }

    public static void onScreenInitBefore(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        executeTasks(_beforeTasks, client, screen, scaledWidth, scaledHeight, "BEFORE");
    }

    public static void onScreenInitAfter(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        executeTasks(_afterTasks, client, screen, scaledWidth, scaledHeight, "AFTER");
    }

    public static void onScreenInit(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
        onScreenInitAfter(client, screen, scaledWidth, scaledHeight);
    }

    private static void executeTasks(List<ScreenInitTask> tasks, MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight, String phase) {
        for (ScreenInitTask task : tasks) {
            try {
                task.execute(client, screen, scaledWidth, scaledHeight);
            } catch (Exception e) {
//                logger.error("Error in screen init task ({}): {}", phase, e);
            }
        }
    }

    public static class ScreenInitTask {
        private final EventPhase _phase;
        private final ScreenInitRunnable _task;

        public ScreenInitTask(EventPhase phase, ScreenInitRunnable task) {
            this._phase = phase;
            this._task = task;
        }

        public EventPhase getPhase() {
            return _phase;
        }

        public void execute(MinecraftClient client, Screen screen, int scaledWidth, int scaledHeight) {
            _task.run(client, screen, scaledWidth, scaledHeight);
        }
    }
}