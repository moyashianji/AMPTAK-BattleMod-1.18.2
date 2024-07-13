package com.example.init;

import com.example.config.HealthconfigConfiguration;
import com.example.examplemod.ExampleMod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@Mod.EventBusSubscriber(modid = "untitled7", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigInit {
    @SubscribeEvent
    public static void register(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, HealthconfigConfiguration.SPEC, "gungame.toml");
        });
    }
}