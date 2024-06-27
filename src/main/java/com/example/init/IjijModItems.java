
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package com.example.init;

import com.example.examplemod.ExampleMod;
import com.example.item.SsItem;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.DeferredRegister;

import net.minecraft.world.item.Item;

public class IjijModItems {
	public static final DeferredRegister<Item> REGISTRY = DeferredRegister.create(ForgeRegistries.ITEMS, "examplemod");
	public static final RegistryObject<Item> SS = REGISTRY.register("ss", () -> new SsItem());
}
