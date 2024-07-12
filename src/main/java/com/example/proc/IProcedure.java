package com.example.proc;

import com.example.init.IjijModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class IProcedure {


    @SubscribeEvent
    public static void onContainerClose(PlayerContainerEvent.Close event) {
        Player player = (Player) event.getEntity();
        if (player.level.isClientSide) {
            return; // サーバーサイドのみ実行
        }

        if (event.getContainer() instanceof ChestMenu) {
            ChestMenu chestMenu = (ChestMenu) event.getContainer();
            Level world = player.level;
            BlockEntity blockEntity = (BlockEntity) chestMenu.getContainer();

            if (blockEntity instanceof ChestBlockEntity) {
                ChestBlockEntity chest = (ChestBlockEntity) blockEntity;
                BlockPos pos = chest.getBlockPos(); // チェストの位置を取得

                boolean isEmpty = true;
                for (int i = 0; i < chest.getContainerSize(); i++) {
                    ItemStack stack = chest.getItem(i);
                    if (!stack.isEmpty()) {
                        isEmpty = false;
                        break;
                    }
                }

                if (isEmpty) {
                    world.destroyBlock(pos, false);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            BlockPos deathPos = player.blockPosition();
            BlockPos chestPos = new BlockPos(deathPos.getX(), deathPos.getY(), deathPos.getZ());
            BlockState chestState = Blocks.CHEST.defaultBlockState();

            // チェストを生成
            player.level.setBlock(chestPos, chestState, 3);

            // チェストのBlockEntityを取得
            BlockEntity blockEntity = player.level.getBlockEntity(chestPos);
            if (blockEntity instanceof ChestBlockEntity) {
                ChestBlockEntity chest = (ChestBlockEntity) blockEntity;

                // プレイヤーのインベントリをチェストに移動
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    if (!stack.isEmpty()) {
                        if (!stack.is(Items.BARRIER)&& !stack.is(IjijModItems.AXE.get())) {
                            chest.setItem(i, stack.copy());
                            player.getInventory().setItem(i, ItemStack.EMPTY);
                        }
                    }
                }

                // プレイヤーの装備品をチェストに移動
                for (ItemStack stack : player.getArmorSlots()) {
                    if (!stack.isEmpty()) {
                        if (!stack.is(Items.BARRIER)&& !stack.is(IjijModItems.AXE.get())) {

                            chest.setItem(chest.getContainerSize() - 1, stack.copy());
                        }
                    }
                }
                for (ItemStack stack : player.getHandSlots()) {
                    if (!stack.isEmpty()) {
                        if (!stack.is(Items.BARRIER)  && !stack.is(IjijModItems.AXE.get())) {

                            chest.setItem(chest.getContainerSize() - 1, stack.copy());
                        }
                    }
                }
            }

            // チェストにアイテムが入りきらない場合、余ったアイテムをドロップ
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (!stack.isEmpty()) {
                    if (!stack.is(Items.BARRIER) && !stack.is(IjijModItems.AXE.get())) {

                        ItemEntity itemEntity = new ItemEntity(player.level, deathPos.getX(), deathPos.getY(), deathPos.getZ(), stack);
                        player.level.addFreshEntity(itemEntity);
                        player.getInventory().setItem(i, ItemStack.EMPTY);
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public static void onPlayerDeath(LivingDropsEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            // Check if the entity is a player and handle barrier items
            handleBarrierItems(player, event);
        }
    }

    private static void handleBarrierItems(Player player, LivingDropsEvent event) {
        // Iterate through drops and remove barrier items
        event.getDrops().removeIf(drop -> drop.getItem().getItem() == Items.BARRIER);
        event.getDrops().removeIf(drop -> drop.getItem().getItem() == IjijModItems.AXE.get());

    }
    @SubscribeEvent
    public static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = (Player) event.getEntity();
        protectInventory(player);
    }
    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        Player player = (Player) event.getEntity();
        protectInventory(player);
    }

    private static void protectInventory(Player player) {
        for (int i = 8; i < player.getInventory().getContainerSize(); i++) {
            if (i >= 36 && i <= 40) { // 装備スロットの範囲をスキップ
                continue;
            }
            if (player.getInventory().getItem(i).isEmpty()) {
                player.getInventory().setItem(i, new ItemStack(Items.BARRIER));

            }
            player.getInventory().setItem(0, new ItemStack(IjijModItems.AXE.get()));

        }

    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getPlacedBlock().getBlock() == Blocks.BARRIER) {
            // Check if the item being placed is a barrier block
            ItemStack itemStack = ((Player)event.getEntity()).getItemInHand(InteractionHand.MAIN_HAND);
            if (itemStack.getItem() == Items.BARRIER) {
                // Prevent placing barrier blocks
                event.setCanceled(true);
            }
        }
    }
    @SubscribeEvent
    public static void onItemRightClick(PlayerInteractEvent.RightClickItem event) {
        ItemStack itemStack = event.getItemStack();

        // Check if the item being right-clicked is a barrier block
        if (itemStack.getItem() == Items.BARRIER) {
            // Prevent placing barrier blocks by cancelling the event
            event.setCanceled(true);
        }
    }
}
