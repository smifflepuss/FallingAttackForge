package com.hamusuke.fallingattack;

import com.hamusuke.fallingattack.client.gui.ConfigScreen;
import com.hamusuke.fallingattack.config.Config;
import com.hamusuke.fallingattack.enchantment.SharpnessOfFallingAttackEnchantment;
import com.hamusuke.fallingattack.network.NetworkManager;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(FallingAttack.MOD_ID)
public class FallingAttack {
    public static final String MOD_ID = "fallingattack";

    public FallingAttack() {
        ModRegistries.init();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.Client.CONFIG);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.Common.CONFIG);
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory(ConfigScreen::new));

        FMLJavaModLoadingContext.get().getModEventBus().addListener((final FMLCommonSetupEvent event) -> NetworkManager.initNetworking());
    }

    public static final class ModRegistries {
        private static final DeferredRegister<Enchantment> ENCHANTMENT = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);

        public static void init() {
            IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
            ENCHANTMENT.register(bus);
        }

        public static final RegistryObject<SharpnessOfFallingAttackEnchantment> SHARPNESS_OF_FALLING_ATTACK = ENCHANTMENT.register("falling_attack", SharpnessOfFallingAttackEnchantment::new);
    }
}
