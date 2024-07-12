package com.example.mixin;

import com.example.init.IjijModItems;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Slot.class)
public class SlotMixin {

    @Inject(method = "mayPlace", at = @At("RETURN"), cancellable = true)
    private void onMayPlace(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = (Slot) (Object) this;
        if (slot.container instanceof Inventory) {


            int slotIndex = slot.getSlotIndex();

            // メインハンド、オフハンド、装備スロット、メインスロットを除く
            if (slotIndex >= 36 || slotIndex < 8) {
                return;
            }
            slot.set(new ItemStack(Items.BARRIER));

            if(slotIndex == 0){
                return;
            }
            slot.set(new ItemStack(IjijModItems.AXE.get()));

            // それ以外のスロットはロックする
            cir.setReturnValue(false);

        }
    }
    @Inject(method = "mayPickup", at = @At("RETURN"), cancellable = true)
    private void onMayPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        Slot slot = (Slot) (Object) this;

        // プレイヤーのインベントリーであることを確認
        if (slot.container instanceof Inventory) {
            int slotIndex = slot.getSlotIndex();

            // メインハンド、オフハンド、装備スロット、メインスロットを除く
            if (slotIndex >= 36 || slotIndex < 8 || slotIndex == 0) {
                return;
            }

            // それ以外のスロットはロックする
            cir.setReturnValue(false);
        }
    }
}