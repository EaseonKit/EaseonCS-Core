package com.easeon.cs.core.api.events;

import com.easeon.cs.core.api.definitions.enums.EventPhase;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.Level;
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
        InteractionResult run(LocalPlayer player, Level world, InteractionHand hand, BlockHitResult hit);
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

    public static InteractionResult onBlockUseBefore(LocalPlayer player, Level world, InteractionHand hand, BlockHitResult hit) {
        return executeTasks(_beforeTasks, player, world, hand, hit, "BEFORE");
    }

    public static InteractionResult onBlockUseAfter(LocalPlayer player, Level world, InteractionHand hand, BlockHitResult hit) {
        return executeTasks(_afterTasks, player, world, hand, hit, "AFTER");
    }

    public static InteractionResult onBlockUse(LocalPlayer player, Level world, InteractionHand hand, BlockHitResult hit) {
        return onBlockUseAfter(player, world, hand, hit);
    }

    private static InteractionResult executeTasks(List<BlockUseTask> tasks, LocalPlayer player, Level world, InteractionHand hand, BlockHitResult hit, String phase) {
        for (BlockUseTask task : tasks) {
            try {
                InteractionResult result = task.execute(player, world, hand, hit);
                if (result != InteractionResult.PASS) {
                    return result;
                }
            } catch (Exception e) {
//                logger.error("Error in block use task ({}): {}", phase, e);
            }
        }
        return InteractionResult.PASS;
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

        public InteractionResult execute(LocalPlayer player, Level world, InteractionHand hand, BlockHitResult hit) {
            return _task.run(player, world, hand, hit);
        }
    }
}