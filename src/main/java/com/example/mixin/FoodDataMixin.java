package com.example.mixin;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FoodData.class)
public class FoodDataMixin {

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void onTick(Player player, CallbackInfo ci) {
        // ここで満腹度が減少しないようにする
        // 満腹度を常に最大に設定
        ((FoodData) (Object) this).setFoodLevel(20);
        ((FoodData) (Object) this).setSaturation(5.0f);  // 5.0fは飽和度の最大値
          }
}