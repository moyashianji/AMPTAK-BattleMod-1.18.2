package com.example.proc;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Random;

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
                        chest.setItem(i, stack.copy());
                        player.getInventory().setItem(i, ItemStack.EMPTY);
                    }
                }

                // プレイヤーの装備品をチェストに移動
                for (ItemStack stack : player.getArmorSlots()) {
                    if (!stack.isEmpty()) {
                        chest.setItem(chest.getContainerSize() - 1, stack.copy());
                    }
                }
                for (ItemStack stack : player.getHandSlots()) {
                    if (!stack.isEmpty()) {
                        chest.setItem(chest.getContainerSize() - 1, stack.copy());
                    }
                }
            }

            // チェストにアイテムが入りきらない場合、余ったアイテムをドロップ
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (!stack.isEmpty()) {
                    ItemEntity itemEntity = new ItemEntity(player.level, deathPos.getX(), deathPos.getY(), deathPos.getZ(), stack);
                    player.level.addFreshEntity(itemEntity);
                    player.getInventory().setItem(i, ItemStack.EMPTY);
                }
            }
        }
    }

    // プレイヤーがログインしたときにインベントリを設定
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
        for (int i = 9; i < player.getInventory().getContainerSize(); i++) {
            if (i >= 36 && i <= 40) { // 装備スロットの範囲をスキップ
                continue;
            }
            if (player.getInventory().getItem(i).isEmpty()) {
                player.getInventory().setItem(i, new ItemStack(Items.BARRIER));
            }
        }
    }

    @SubscribeEvent
    public static void onItemPickup(PlayerEvent.ItemPickupEvent event) {
        Player player = (Player) event.getEntity();
        protectInventory(player);
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        Player player = (Player) event.getEntity();
        protectInventory(player);
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        Player player = (Player) event.getEntity();
        protectInventory(player);
    }

    @SubscribeEvent
    public static void onInventoryClick(PlayerContainerEvent event) {
        Player player = (Player) event.getEntity();
        if (event.getContainer().equals(player.containerMenu)) {
            for (int slotIndex = 9; slotIndex < player.getInventory().getContainerSize(); slotIndex++) {
                if (player.getInventory().getItem(slotIndex).getItem() == Items.BARRIER) {
                    event.setCanceled(true); // バリアブロックの移動をキャンセル
                    break;
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerdDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            protectInventory(player);
        }
    }
}
