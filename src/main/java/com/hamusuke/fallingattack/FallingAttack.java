package com.hamusuke.fallingattack;

import com.hamusuke.fallingattack.client.gui.ConfigScreen;
import com.hamusuke.fallingattack.config.Config;
import com.hamusuke.fallingattack.enchantment.FallingAttackEnchantment;
import com.hamusuke.fallingattack.network.NetworkManager;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FallingAttack.MOD_ID)
public class FallingAttack {
    public static final String MOD_ID = "fallingattack";
    public static final Enchantment FALLING_ATTACK = new FallingAttackEnchantment();

    public FallingAttack() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.config);
        ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY, () -> ConfigScreen::new);

        FMLJavaModLoadingContext.get().getModEventBus().addListener((final FMLCommonSetupEvent event) -> NetworkManager.initNetworking());
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onEnchantmentsRegistry(final RegistryEvent.Register<Enchantment> event) {
            event.getRegistry().register(FALLING_ATTACK);
        }
    }
}
