package com.example.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.LinkedList;
import java.util.Queue;

@Mod.EventBusSubscriber
public class OriAxe extends AxeItem {

    // キューで処理するブロックの位置を保持する
    private static final Queue<BlockPos> blockQueue = new LinkedList<>();
    private static int tickCounter = 0; // ティックをカウントする

    public OriAxe() {
        super(new Tier() {
            public int getUses() {
                return 10000;
            }

            public float getSpeed() {
                return 4f;
            }

            public float getAttackDamageBonus() {
                return 2f;
            }

            public int getLevel() {
                return 1;
            }

            public int getEnchantmentValue() {
                return 2;
            }

            public Ingredient getRepairIngredient() {
                return Ingredient.EMPTY;
            }
        }, 1, -3f, new Item.Properties().tab(CreativeModeTab.TAB_TOOLS));
    }

    // ブロック破壊イベントにフック
    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.getPlayer() != null && event.getPlayer().getMainHandItem().getItem() instanceof OriAxe) {
            BlockPos pos = event.getPos();
            Level world = (Level) event.getWorld();

            // 原木ブロックかどうかを確認
            Block block = world.getBlockState(pos).getBlock();
            if (isLogBlock(block)) {
                event.setCanceled(true); // ブロックのドロップをキャンセル
                world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

                // 最初の破壊対象のブロックをキューに追加
                blockQueue.add(pos);
                spawnPlanksFromLog((ServerLevel) world, block, pos); // 板材をドロップ
            }
        }
    }

    // 原木ブロックかどうかを判定するメソッド
    private static boolean isLogBlock(Block block) {
        return block == Blocks.OAK_LOG || block == Blocks.SPRUCE_LOG || block == Blocks.BIRCH_LOG ||
                block == Blocks.JUNGLE_LOG || block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG;
    }

    // サーバーのティックごとに呼び出されるイベント
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        tickCounter++;

        // 20ティックごとに処理
        if (tickCounter >= 20 && !blockQueue.isEmpty()) {
            tickCounter = 0; // カウンターをリセット

            // キューからブロック位置を取り出して処理
            BlockPos logPos = blockQueue.poll();
            if (logPos != null) {
                // サーバーレベルを取得
                ServerLevel world = ServerLifecycleHooks.getCurrentServer().overworld();
                if (world != null) {
                    BlockPos abovePos = logPos.above();
                    BlockState state = world.getBlockState(abovePos);
                    Block block = state.getBlock();

                    // 上に原木ブロックがあれば破壊し、板材をドロップ
                    if (isLogBlock(block)) {
                        world.setBlock(abovePos, Blocks.AIR.defaultBlockState(), 3); // ブロックを空気に置き換え
                        spawnBlockBreakParticles(world, abovePos, block); // 破壊パーティクルを生成
                        spawnPlanksFromLog(world, block, abovePos); // 板材をドロップ

                        // 上の原木を再度キューに追加
                        blockQueue.add(abovePos);
                    }
                }
            }
        }
    }

    // ブロック破壊時に破壊パーティクルを生成するメソッド
    private static void spawnBlockBreakParticles(Level world, BlockPos pos, Block block) {
        if (!(world instanceof ServerLevel)) {
            return; // サーバーサイドでのみ処理
        }

        // ブロック破壊パーティクルを生成
        ((ServerLevel) world).sendParticles(
                new BlockParticleOption(ParticleTypes.BLOCK, block.defaultBlockState()),
                pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, // パーティクルの位置
                20, // パーティクルの数
                0.25, 0.25, 0.25, // パーティクルの広がり
                0.05 // パーティクルの速度
        );
    }

    // 原木ブロックに対応するプランクをスポーンさせるメソッド
    private static void spawnPlanksFromLog(ServerLevel world, Block block, BlockPos pos) {
        // 原木に対応するプランクを生成するロジック
        ItemStack planks = ItemStack.EMPTY;

        if (block == Blocks.ACACIA_LOG) {
            planks = new ItemStack(Blocks.ACACIA_PLANKS);
        } else if (block == Blocks.BIRCH_LOG) {
            planks = new ItemStack(Blocks.BIRCH_PLANKS);
        } else if (block == Blocks.SPRUCE_LOG) {
            planks = new ItemStack(Blocks.SPRUCE_PLANKS);
        } else if (block == Blocks.OAK_LOG) {
            planks = new ItemStack(Blocks.OAK_PLANKS);
        } else if (block == Blocks.JUNGLE_LOG) {
            planks = new ItemStack(Blocks.JUNGLE_PLANKS);
        } else if (block == Blocks.DARK_OAK_LOG) {
            planks = new ItemStack(Blocks.DARK_OAK_PLANKS);
        }

        // 各プランクを4つ生成してドロップ
        if (!planks.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                double offsetX = (world.random.nextDouble() - 0.5) * 0.5; // 生成位置を少しランダムに
                double offsetZ = (world.random.nextDouble() - 0.5) * 0.5;

                ItemEntity entityToSpawn = new ItemEntity(world, pos.getX() + 0.5 + offsetX, pos.getY() + 0.5, pos.getZ() + 0.5 + offsetZ, planks);
                entityToSpawn.setPickUpDelay(10); // 拾えるようになるまでの遅延
                entityToSpawn.setUnlimitedLifetime(); // デスポーンしないように設定
                world.addFreshEntity(entityToSpawn);
            }
        }
    }
}
