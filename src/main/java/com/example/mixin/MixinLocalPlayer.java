package com.example.mixin;

import com.example.init.IjijModItems;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayer extends Player {

    @Shadow
    @Final
    public ClientPacketListener connection;

    public MixinLocalPlayer(Level p_36114_, BlockPos p_36115_, float p_36116_, GameProfile p_36117_) {
        super(p_36114_, p_36115_, p_36116_, p_36117_);
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean drop(boolean p_108701_) {
        int selectedSlotIndex = this.getInventory().selected;
        ItemStack selectedStack = this.getInventory().getItem(selectedSlotIndex);

        if (selectedStack.getItem() == Items.BARRIER || selectedStack.getItem() == IjijModItems.AXE.get()) {
            return false; // Cancel dropping barrier items
        }

        ItemStack removedStack = this.getInventory().removeItem(selectedSlotIndex, 1);


        ServerboundPlayerActionPacket.Action action = p_108701_ ?
                ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS :
                ServerboundPlayerActionPacket.Action.DROP_ITEM;

        this.connection.send(new ServerboundPlayerActionPacket(action, BlockPos.ZERO, Direction.DOWN));
        return !removedStack.isEmpty();
    }
}