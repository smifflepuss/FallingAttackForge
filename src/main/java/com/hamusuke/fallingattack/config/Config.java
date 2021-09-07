package com.hamusuke.fallingattack.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class Config {
    private static final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.BooleanValue thirdPerson = builder.comment("When the player starts the falling attack, the perspective changes to third person.").define("third_person", true);
    public static final ForgeConfigSpec config = builder.build();
}
