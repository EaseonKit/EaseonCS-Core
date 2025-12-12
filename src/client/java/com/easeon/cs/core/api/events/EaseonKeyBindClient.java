package com.easeon.cs.core.api.events;

import net.minecraft.client.Minecraft;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class EaseonKeyBindClient {
    private static final List<KeyBindTask> _tasks = new CopyOnWriteArrayList<>();
    private static final Map<Object, KeyBindTask> _methodRefTasks = new HashMap<>();

    @FunctionalInterface
    public interface KeyBindRunnable {
        void run(Minecraft client);
    }

    public static KeyBindTask register(KeyBindRunnable task) {
        KeyBindTask keyBindTask = new KeyBindTask(task);
        _tasks.add(keyBindTask);
        _methodRefTasks.put(task, keyBindTask);
        return keyBindTask;
    }

    public static void register(KeyBindTask task) {
        if (!_tasks.contains(task)) {
            _tasks.add(task);
        }
    }

    public static void unregister(KeyBindTask task) {
        _tasks.remove(task);
    }

    public static void unregister(KeyBindRunnable task) {
        KeyBindTask keyBindTask = _methodRefTasks.remove(task);
        if (keyBindTask != null) {
            _tasks.remove(keyBindTask);
        }
    }

    public static void onEndClientTick(Minecraft client) {
        executeTasks(_tasks, client);
    }

    private static void executeTasks(List<KeyBindTask> tasks, Minecraft client) {
        for (KeyBindTask task : tasks) {
            try {
                task.execute(client);
            } catch (Exception e) {
//                logger.error("Error in key bind task: {}", e);
            }
        }
    }

    public static class KeyBindTask {
        private final KeyBindRunnable _task;

        public KeyBindTask(KeyBindRunnable task) {
            this._task = task;
        }

        public void execute(Minecraft client) {
            _task.run(client);
        }
    }
}