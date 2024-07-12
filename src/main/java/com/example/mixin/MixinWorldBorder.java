package com.example.mixin;


import net.minecraft.core.BlockPos;
import net.minecraft.world.level.border.WorldBorder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(WorldBorder.class)
public class MixinWorldBorder {

    @Inject(method = "isWithinBounds", at = @At("HEAD"), cancellable = true)
    private void isWithinBounds(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        WorldBorder worldBorder = (WorldBorder) (Object) this;
        double centerX = worldBorder.getCenterX();
        double centerZ = worldBorder.getCenterZ();
        double radius = worldBorder.getSize() / 2.0;

        double dx = pos.getX() - centerX;
        double dz = pos.getZ() - centerZ;

        if (dx * dx + dz * dz < radius * radius) {
            cir.setReturnValue(true);
        } else {
            cir.setReturnValue(false);
        }
    }
}