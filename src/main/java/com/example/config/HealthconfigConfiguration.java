package com.example.config;


import net.minecraftforge.common.ForgeConfigSpec;

public class HealthconfigConfiguration {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Integer> RADIUS;
    public static final ForgeConfigSpec.ConfigValue<Integer> GAMETIME;
    public static final ForgeConfigSpec.ConfigValue<Integer> Xpos;
    public static final ForgeConfigSpec.ConfigValue<Integer> Zpos;
    public static final ForgeConfigSpec.ConfigValue<Integer> Ypos;


    static {
        BUILDER.push("Radius");
        RADIUS = BUILDER.define("Radius", 1000);
        BUILDER.pop();
        BUILDER.push("GameTime(sec)");
        GAMETIME = BUILDER.define("GameTime",600);
        BUILDER.pop();

        BUILDER.push("Xpos");
        Xpos = BUILDER.define("Xpos",0);
        BUILDER.pop();
        BUILDER.push("Ypos");

        Ypos = BUILDER.define("Ypos",0);
        BUILDER.pop();

        BUILDER.push("Zpos");

        Zpos = BUILDER.define("Zpos",0);
        BUILDER.pop();

        SPEC = BUILDER.build();
    }

}