package net.hyper_pigeon.electriceel.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import net.hyper_pigeon.electriceel.entity.ElectricEelEntity;
import net.minecraft.entity.ai.brain.Activity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.task.LookAroundTask;
import net.minecraft.entity.ai.brain.task.WalkToNearestVisibleWantedItemTask;
import net.minecraft.entity.ai.brain.task.WanderAroundTask;
import net.minecraft.entity.passive.AllayEntity;

public class ElectricEelBrain {
    public ElectricEelBrain(){

    }

    public static Brain<?> create( Brain<ElectricEelEntity> brain) {
        addCoreActivities(brain);
        addIdleActivities(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.resetPossibleActivities();
        return brain;
    }

    private static void addCoreActivities(Brain<ElectricEelEntity> brain) {
        brain.setTaskList(
                Activity.CORE,
                0,
                ImmutableList.of(new LookAroundTask(45, 90),
                        new WanderAroundTask()));
    }

    private static void addIdleActivities(Brain<ElectricEelEntity> brain) {
        brain.setTaskList(Activity.IDLE,
                ImmutableList.of(
                        Pair.of(0, WalkToNearestVisibleWantedItemTask.m_xkulsdab(eel -> true, 1.2F, true, 32))));
    }
}
