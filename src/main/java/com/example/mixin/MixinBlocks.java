package com.example.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Blocks.class)
public abstract class MixinBlocks {

    @Inject(method = "log(Lnet/minecraft/world/level/material/MaterialColor;Lnet/minecraft/world/level/material/MaterialColor;)Lnet/minecraft/world/level/block/RotatedPillarBlock;", at = @At("HEAD"), cancellable = true)
    private static void injectLogMethod(MaterialColor p_50789_, MaterialColor p_50790_, CallbackInfoReturnable<RotatedPillarBlock> cir) {
        RotatedPillarBlock modifiedLog = new RotatedPillarBlock(BlockBehaviour.Properties.of(Material.GLASS, (p_152624_) -> {
            return p_152624_.getValue(RotatedPillarBlock.AXIS) == Direction.Axis.Y ? p_50789_ : p_50790_;
        }).strength(2.0F).sound(SoundType.WOOD));

        cir.setReturnValue(modifiedLog);
    }



}