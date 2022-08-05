package com.hamusuke.fallingattack.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static class Client {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec.BooleanValue THIRD_PERSON = BUILDER.comment("When the player starts the falling attack, the perspective changes to third person.").define("third_person", true);
        public static final ForgeConfigSpec CONFIG = BUILDER.build();
    }

    public static class Common {
        private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
        public static final ForgeConfigSpec.IntValue DAMAGE_AMOUNT = BUILDER.comment("default: 100%").defineInRange("damage_amount", 100, 0, Integer.MAX_VALUE);
        public static final ForgeConfigSpec.IntValue KNOCKBACK_AMOUNT = BUILDER.comment("default: 100%").defineInRange("knockback_amount", 100, 0, Integer.MAX_VALUE);
        public static final AttackableEntities ATTACKABLE_ENTITIES = new AttackableEntities(BUILDER);
        public static final ForgeConfigSpec CONFIG = BUILDER.build();
    }
}
