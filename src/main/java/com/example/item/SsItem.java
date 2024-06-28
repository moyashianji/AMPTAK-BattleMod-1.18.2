
package com.example.item;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

import java.util.Random;

public class SsItem extends Item {
	public SsItem() {
		super(new Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS).stacksTo(64).rarity(Rarity.COMMON));
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		super.useOn(context);
		Random random = new Random();
		context.getLevel().setBlock(new BlockPos( context.getClickedPos().getX(), context.getClickedPos().getY()+1, context.getClickedPos().getZ()), Blocks.CHEST.defaultBlockState(), 2);
		RandomizableContainerBlockEntity.setLootTable(context.getLevel(), random, new BlockPos( context.getClickedPos().getX(), context.getClickedPos().getY()+1, context.getClickedPos().getZ()),new ResourceLocation("untitled7:chests/bonusplus"));

		return InteractionResult.SUCCESS;
	}

}
