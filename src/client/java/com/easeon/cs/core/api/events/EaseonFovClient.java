package com.easeon.cs.core.api.events;

import com.easeon.cs.core.api.definitions.enums.EventPhase;
import net.minecraft.client.render.Camera;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class EaseonFovClient {
    private static final List<FovTask> _beforeTasks = new CopyOnWriteArrayList<>();
    private static final List<FovTask> _afterTasks = new CopyOnWriteArrayList<>();
    private static final Map<Object, FovTask> _methodRefTasks = new HashMap<>();

    @FunctionalInterface
    public interface FovRunnable {
        float run(Camera camera, float tickDelta, boolean changingFov, float originalFov);
    }

    public static FovTask register(FovRunnable task) {
        return register(EventPhase.AFTER, task);
    }

    public static FovTask register(EventPhase phase, FovRunnable task) {
        FovTask fovTask = new FovTask(phase, task);
        if (phase == EventPhase.BEFORE) {
            _beforeTasks.add(fovTask);
        } else {
            _afterTasks.add(fovTask);
        }
        _methodRefTasks.put(task, fovTask);
        return fovTask;
    }

    public static void register(FovTask task) {
        List<FovTask> targetList = task.getPhase() == EventPhase.BEFORE ? _beforeTasks : _afterTasks;
        if (!targetList.contains(task)) {
            targetList.add(task);
        }
    }

    public static void unregister(FovTask task) {
        _beforeTasks.remove(task);
        _afterTasks.remove(task);
    }

    public static void unregister(FovRunnable task) {
        FovTask fovTask = _methodRefTasks.remove(task);
        if (fovTask != null) {
            _beforeTasks.remove(fovTask);
            _afterTasks.remove(fovTask);
        }
    }

    public static float onFovBefore(Camera camera, float tickDelta, boolean changingFov, float originalFov) {
        return executeTasks(_beforeTasks, camera, tickDelta, changingFov, originalFov, "BEFORE");
    }

    public static float onFovAfter(Camera camera, float tickDelta, boolean changingFov, float originalFov) {
        return executeTasks(_afterTasks, camera, tickDelta, changingFov, originalFov, "AFTER");
    }

    public static float onFov(Camera camera, float tickDelta, boolean changingFov, float originalFov) {
        return onFovAfter(camera, tickDelta, changingFov, originalFov);
    }

    private static float executeTasks(List<FovTask> tasks, Camera camera, float tickDelta, boolean changingFov, float currentFov, String phase) {
        float resultFov = currentFov;
        for (FovTask task : tasks) {
            try {
                float newFov = task.execute(camera, tickDelta, changingFov, resultFov);
                if (newFov != resultFov) {
                    resultFov = newFov;
                }
            } catch (Exception e) {
//                logger.error("Error in FOV task ({}): {}", phase, e);
            }
        }
        return resultFov;
    }

    public static class FovTask {
        private final EventPhase _phase;
        private final FovRunnable _task;

        public FovTask(EventPhase phase, FovRunnable task) {
            this._phase = phase;
            this._task = task;
        }

        public EventPhase getPhase() {
            return _phase;
        }

        public float execute(Camera camera, float tickDelta, boolean changingFov, float originalFov) {
            return _task.run(camera, tickDelta, changingFov, originalFov);
        }
    }
}