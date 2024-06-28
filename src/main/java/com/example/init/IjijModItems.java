
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.example.init;

import com.example.item.SsItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IjijModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, "untitled7");
	public static final RegistryObject<Item> SS = REGISTRY.register("ss", () -> new SsItem());
}
