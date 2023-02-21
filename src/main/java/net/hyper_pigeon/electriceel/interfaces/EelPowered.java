package net.hyper_pigeon.electriceel.interfaces;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface EelPowered {
    void setEelPowered(BlockState state, World world, BlockPos pos, int charge);
}
