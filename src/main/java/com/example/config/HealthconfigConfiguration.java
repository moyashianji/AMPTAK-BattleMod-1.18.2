package com.example.config;


import net.minecraftforge.common.ForgeConfigSpec;

public class HealthconfigConfiguration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> RADIUS;
    public static final ForgeConfigSpec.ConfigValue<Integer> GAMETIME;
    static {
        BUILDER.push("Radius");
        RADIUS = BUILDER.define("Radius", 1000);
        BUILDER.pop();
        BUILDER.push("GameTime(sec)");
        GAMETIME = BUILDER.define("GameTime",700);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}