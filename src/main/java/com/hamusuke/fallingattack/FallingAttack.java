package com.hamusuke.fallingattack;

import com.hamusuke.fallingattack.enchantment.FallingAttackEnchantment;
import com.hamusuke.fallingattack.invoker.IPlayerEntity;
import com.hamusuke.fallingattack.network.NetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FallingAttack.MOD_ID)
public class FallingAttack {
    public static final String MOD_ID = "fallingattack";
    public static final Enchantment FALLING_ATTACK = new FallingAttackEnchantment();

    public FallingAttack() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener((final FMLCommonSetupEvent event) -> NetworkManager.setupNetworking());
        FMLJavaModLoadingContext.get().getModEventBus().addListener((final FMLClientSetupEvent event) -> MinecraftForge.EVENT_BUS.register(this));
    }

    @SubscribeEvent
    public void onAttack(final InputEvent.ClickInputEvent event) {
        ClientPlayerEntity clientPlayerEntity = Minecraft.getInstance().player;
        assert clientPlayerEntity != null;
        IPlayerEntity invoker = (IPlayerEntity) clientPlayerEntity;

        if (!invoker.isUsingFallingAttack()) {
            if (EnchantmentHelper.getItemEnchantmentLevel(FallingAttack.FALLING_ATTACK, clientPlayerEntity.getMainHandItem()) > 0 && invoker.checkFallingAttack()) {
                invoker.sendFallingAttackPacket(true);
                event.setCanceled(true);
                event.setSwingHand(false);
            }
        } else {
            invoker.sendFallingAttackPacket(false);
        }
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        @SubscribeEvent
        public static void onEnchantmentsRegistry(final RegistryEvent.Register<Enchantment> event) {
            event.getRegistry().register(FALLING_ATTACK);
        }
    }
}
