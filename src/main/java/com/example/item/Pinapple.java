

package com.example.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Pinapple extends Item {
	public Pinapple() {
		super(new Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS).stacksTo(64).rarity(Rarity.COMMON).food((new FoodProperties.Builder()).nutrition(0).saturationMod(0.3f).alwaysEat().build()));
	}


	@Override
	public ItemStack finishUsingItem(ItemStack itemstack, Level world, LivingEntity entity) {
		ItemStack retval = super.finishUsingItem(itemstack, world, entity);



		entity.heal(14.0f);
		return retval;
	}

}
