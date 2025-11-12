package com.easeon.cs.core.api.events;

import com.easeon.cs.core.api.definitions.enums.EventPhase;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.world.World;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class EaseonBlockUseClient {
    private static final List<BlockUseTask> _beforeTasks = new CopyOnWriteArrayList<>();
    private static final List<BlockUseTask> _afterTasks = new CopyOnWriteArrayList<>();
    private static final Map<Object, BlockUseTask> _methodRefTasks = new HashMap<>();

    @FunctionalInterface
    public interface BlockUseRunnable {
        ActionResult run(ClientPlayerEntity player, World world, Hand hand, BlockHitResult hit);
    }

    public static BlockUseTask register(BlockUseRunnable task) {
        return register(EventPhase.AFTER, task);
    }

    public static BlockUseTask register(EventPhase phase, BlockUseRunnable task) {
        BlockUseTask blockUseTask = new BlockUseTask(phase, task);
        if (phase == EventPhase.BEFORE) {
            _beforeTasks.add(blockUseTask);
        } else {
            _afterTasks.add(blockUseTask);
        }
        _methodRefTasks.put(task, blockUseTask);
        return blockUseTask;
    }

    public static void register(BlockUseTask task) {
        List<BlockUseTask> targetList = task.getPhase() == EventPhase.BEFORE ? _beforeTasks : _afterTasks;
        if (!targetList.contains(task)) {
            targetList.add(task);
        }
    }

    public static void unregister(BlockUseTask task) {
        _beforeTasks.remove(task);
        _afterTasks.remove(task);
    }

    public static void unregister(BlockUseRunnable task) {
        BlockUseTask blockUseTask = _methodRefTasks.remove(task);
        if (blockUseTask != null) {
            _beforeTasks.remove(blockUseTask);
            _afterTasks.remove(blockUseTask);
        }
    }

    public static ActionResult onBlockUseBefore(ClientPlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        return executeTasks(_beforeTasks, player, world, hand, hit, "BEFORE");
    }

    public static ActionResult onBlockUseAfter(ClientPlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        return executeTasks(_afterTasks, player, world, hand, hit, "AFTER");
    }

    public static ActionResult onBlockUse(ClientPlayerEntity player, World world, Hand hand, BlockHitResult hit) {
        return onBlockUseAfter(player, world, hand, hit);
    }

    private static ActionResult executeTasks(List<BlockUseTask> tasks, ClientPlayerEntity player, World world, Hand hand, BlockHitResult hit, String phase) {
        for (BlockUseTask task : tasks) {
            try {
                ActionResult result = task.execute(player, world, hand, hit);
                if (result != ActionResult.PASS) {
                    return result;
                }
            } catch (Exception e) {
//                logger.error("Error in block use task ({}): {}", phase, e);
            }
        }
        return ActionResult.PASS;
    }

    public static class BlockUseTask {
        private final EventPhase _phase;
        private final BlockUseRunnable _task;

        public BlockUseTask(EventPhase phase, BlockUseRunnable task) {
            this._phase = phase;
            this._task = task;
        }

        public EventPhase getPhase() {
            return _phase;
        }

        public ActionResult execute(ClientPlayerEntity player, World world, Hand hand, BlockHitResult hit) {
            return _task.run(player, world, hand, hit);
        }
    }
}