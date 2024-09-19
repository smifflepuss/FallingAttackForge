package com.hamusuke.fallingattack.client;

import com.hamusuke.fallingattack.invoker.PlayerInvoker;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;

public class FallingAttackClient {
    public static void init() {
        MinecraftForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingIn event) -> ((PlayerInvoker) event.getPlayer()).fallingattack$sendSynchronizeFallingAttackPacket());
    }
}
