package net.hyper_pigeon.electriceel.mixin;

import net.hyper_pigeon.electriceel.interfaces.EelPowered;
import net.minecraft.block.BlockState;
import net.minecraft.block.LightningRodBlock;
import net.minecraft.block.RodBlock;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LightningRodBlock.class)
public abstract class LightningRodBlockMixin extends RodBlock implements EelPowered {

    @Shadow
    public abstract void updateNeighbors(BlockState state, World world, BlockPos pos);

    @Shadow @Final public static BooleanProperty POWERED;

    public LightningRodBlockMixin(Settings settings) {
        super(settings);
    }

//    @Inject(method = "scheduledTick", at = @At(value = "INVOKE", target = "net/minecraft/server/world/ServerWorld.setBlockState (Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z"
//    ,shift = At.Shift.AFTER), cancellable = true)
//    public void setEelPowerToZero(BlockState state, ServerWorld world, BlockPos pos, RandomGenerator random, CallbackInfo ci){
//        BlockState blockState = world.getBlockState(pos);
//        if(blockState != null) {
//            world.setBlockState(pos, state.with(ElectricEel.EEL_POWER, 0).with(POWERED,false), 3);
//        }
//    }
//
//    @Inject(method = "getStrongRedstonePower", at = @At("HEAD"), cancellable = true)
//    public void getStrongEelPower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir){
//        if(state.get(ElectricEel.EEL_POWER) != 0) {
//            int power = state.get(FACING) == direction ? state.get(ElectricEel.EEL_POWER) : 0;
//            cir.setReturnValue(power);
//        }
//    }
//
//    @Inject(method = "getWeakRedstonePower", at = @At("HEAD"), cancellable = true)
//    public void getWeakEelPower(BlockState state, BlockView world, BlockPos pos, Direction direction, CallbackInfoReturnable<Integer> cir){
//        if(state.get(ElectricEel.EEL_POWER) != 0) {
//            cir.setReturnValue(state.get(ElectricEel.EEL_POWER));
//        }
//    }
//
//
//    public void setEelPowered(BlockState state, World world, BlockPos pos, int charge) {
//        world.setBlockState(pos, state.with(ElectricEel.EEL_POWER, charge), 3);
//        this.updateNeighbors(state, world, pos);
//        world.scheduleBlockTick(pos, this, 8);
//        world.syncWorldEvent(3002, pos, state.get(FACING).getAxis().ordinal());
//    }
//
////    @Inject(method = "appendProperties", at = @At("TAIL"))
////    public void addEelPowerProperty(StateManager.Builder<Block, BlockState> builder, CallbackInfo ci){
////        builder.add(EEL_POWER);
////    }
//
//    @Inject(method = "onStateReplaced", at = @At(value = "INVOKE",
//            target = " net/minecraft/block/RodBlock.onStateReplaced (Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;Z)V",
//            shift = At.Shift.BEFORE))
//    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo ci){
//        this.updateNeighbors(state,world,pos);
//    }

//    @Inject(method = "onBlockAdded", at = @At("TAIL"))
//    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify, CallbackInfo ci){
//        if (!state.isOf(oldState.getBlock())) {
//            if (state.get(EEL_POWER) != 0 && !world.getBlockTickScheduler().isQueued(pos, this)) {
//                world.setBlockState(pos, (BlockState)state.with(EEL_POWER, 0), 18);
//            }
//        }
//    }


}
