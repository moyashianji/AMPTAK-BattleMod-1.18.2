
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.example.init;

import com.example.item.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class IjijModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, "untitled7");
	public static final RegistryObject<Item> SS = REGISTRY.register("ss", () -> new SsItem());
	public static final RegistryObject<Item> ENERGY = REGISTRY.register("energy", () -> new EnergyItem());
	public static final RegistryObject<Item> AXE = REGISTRY.register("oriaxe", () -> new OriAxe());
	public static final RegistryObject<Item> PINE = REGISTRY.register("pine", () -> new Pinapple());
	public static final RegistryObject<Item> GARLIC = REGISTRY.register("garlic", () -> new Garlic());

}
