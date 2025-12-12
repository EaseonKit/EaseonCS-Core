package com.easeon.cs.core.api.events;

import com.easeon.cs.core.api.definitions.enums.EventPhase;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class EaseonHudRenderClient {
    private static final List<HudRenderTask> _beforeTasks = new CopyOnWriteArrayList<>();
    private static final List<HudRenderTask> _afterTasks = new CopyOnWriteArrayList<>();
    private static final Map<Object, HudRenderTask> _methodRefTasks = new HashMap<>();

    @FunctionalInterface
    public interface HudRenderRunnable {
        void run(GuiGraphics guiGraphics, DeltaTracker deltaTracker);
    }

    public static HudRenderTask register(HudRenderRunnable task) {
        return register(EventPhase.AFTER, task);
    }

    public static HudRenderTask register(EventPhase phase, HudRenderRunnable task) {
        HudRenderTask hudRenderTask = new HudRenderTask(phase, task);
        if (phase == EventPhase.BEFORE) {
            _beforeTasks.add(hudRenderTask);
        } else {
            _afterTasks.add(hudRenderTask);
        }
        _methodRefTasks.put(task, hudRenderTask);
        return hudRenderTask;
    }

    public static void register(HudRenderTask task) {
        List<HudRenderTask> targetList = task.getPhase() == EventPhase.BEFORE ? _beforeTasks : _afterTasks;
        if (!targetList.contains(task)) {
            targetList.add(task);
        }
    }

    public static void unregister(HudRenderTask task) {
        _beforeTasks.remove(task);
        _afterTasks.remove(task);
    }

    public static void unregister(HudRenderRunnable task) {
        HudRenderTask hudRenderTask = _methodRefTasks.remove(task);
        if (hudRenderTask != null) {
            _beforeTasks.remove(hudRenderTask);
            _afterTasks.remove(hudRenderTask);
        }
    }

    public static void onHudRenderBefore(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        executeTasks(_beforeTasks, guiGraphics, deltaTracker, "BEFORE");
    }

    public static void onHudRenderAfter(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        executeTasks(_afterTasks, guiGraphics, deltaTracker, "AFTER");
    }

    public static void onHudRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        onHudRenderAfter(guiGraphics, deltaTracker);
    }

    private static void executeTasks(List<HudRenderTask> tasks, GuiGraphics guiGraphics, DeltaTracker deltaTracker, String phase) {
        for (HudRenderTask task : tasks) {
            try {
                task.execute(guiGraphics, deltaTracker);
            } catch (Exception e) {
//                logger.error("Error in HUD render task ({}): {}", phase, e);
            }
        }
    }

    public static class HudRenderTask {
        private final EventPhase _phase;
        private final HudRenderRunnable _task;

        public HudRenderTask(EventPhase phase, HudRenderRunnable task) {
            this._phase = phase;
            this._task = task;
        }

        public EventPhase getPhase() {
            return _phase;
        }

        public void execute(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
            _task.run(guiGraphics, deltaTracker);
        }
    }
}