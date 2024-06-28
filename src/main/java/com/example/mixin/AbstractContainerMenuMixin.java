package com.example.mixin;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {
    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void onSlotClick(int slotId, int dragType, ClickType clickType, Player player, CallbackInfo ci) {
        if (slotId >= 0) {
            AbstractContainerMenu container = (AbstractContainerMenu) (Object) this;
            Slot slot = container.slots.get(slotId);

            // プレイヤーのインベントリーであることを確認
            if (slot.container instanceof Inventory) {
                int slotIndex = slot.getSlotIndex();

                // メインハンド、オフハンド、装備スロット、メインスロットを除く
                if (slotIndex >= 36 || slotIndex < 9) {
                    return;
                }

                // メインスロットの一番右のスロットを保護
                if (slotIndex == 8) {
                    ci.cancel();
                }

                // それ以外のスロットはロックする
                // それ以外のロックされたスロット
                if (slotIndex > 8 && slotIndex < 36) {
                    if (clickType == ClickType.THROW || clickType == ClickType.PICKUP || clickType == ClickType.PICKUP_ALL || clickType == ClickType.SWAP) {
                        ci.cancel();
                    }
                }
            }
        }
    }


}
