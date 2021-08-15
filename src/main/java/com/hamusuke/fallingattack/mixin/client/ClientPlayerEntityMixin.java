package com.hamusuke.fallingattack.mixin.client;

import com.hamusuke.fallingattack.mixin.PlayerEntityMixin;
import com.hamusuke.fallingattack.network.c2s.FallingAttackC2SPacket;
import com.hamusuke.fallingattack.network.NetworkManager;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;

@OnlyIn(Dist.CLIENT)
@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin extends PlayerEntityMixin {
    ClientPlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    public void sendFallingAttackPacket(boolean start) {
        NetworkManager.sendToServer(new FallingAttackC2SPacket(start));
    }
}
