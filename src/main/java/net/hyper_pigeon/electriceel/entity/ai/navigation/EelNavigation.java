package net.hyper_pigeon.electriceel.entity.ai.navigation;

import net.minecraft.entity.ai.pathing.AmphibiousNavigation;
import net.minecraft.entity.ai.pathing.AmphibiousPathNodeMaker;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.world.World;

public class EelNavigation extends AmphibiousNavigation {
    public EelNavigation(MobEntity mobEntity, World world) {
        super(mobEntity, world);
    }

    protected PathNodeNavigator createPathNodeNavigator(int range) {
        this.nodeMaker = new AmphibiousPathNodeMaker(true);
        this.nodeMaker.setCanEnterOpenDoors(true);
        return new EelPathNodeNavigator(this.nodeMaker, range);
    }
}
