package com.example.mixin;

import com.example.item.OriAxe;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(method = "dropResources(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/storage/loot/LootContext$Builder;)V", at = @At("HEAD"), cancellable = true)
    private static void dropResources(BlockState state, LootContext.Builder builder, CallbackInfo ci) {
        // LootContextからツールを取得
        ItemStack tool = builder.getParameter(LootContextParams.TOOL);
        if (tool != null && tool.getItem() instanceof OriAxe) {
            System.out.println("aaa");
            // 原木ブロックを対象にドロップを無効化
            if (isLogBlock(state.getBlock())) {
                ci.cancel(); // ドロップをキャンセル
                breakAboveLogs(builder.getLevel(), new BlockPos(builder.getParameter(LootContextParams.ORIGIN))); // 上の原木を破壊
            }
        }
    }

    // 原木ブロックかどうかを判定するメソッド
    private static boolean isLogBlock(Block block) {
        return block == Blocks.OAK_LOG || block == Blocks.SPRUCE_LOG || block == Blocks.BIRCH_LOG ||
                block == Blocks.JUNGLE_LOG || block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG;
    }

    // 上方向にある連続した原木ブロックを破壊
    private static void breakAboveLogs(ServerLevel world, BlockPos pos) {
        BlockPos currentPos = pos.above();
        while (true) {
            BlockState state = world.getBlockState(currentPos);
            Block block = state.getBlock();

            if (isLogBlock(block)) {
                // 原木ブロックを空気に置き換え
                world.setBlock(currentPos, Blocks.AIR.defaultBlockState(), 3);
                currentPos = currentPos.above(); // 上のブロックを確認
            } else {
                break; // 原木がなくなったら終了
            }
        }
    }
}