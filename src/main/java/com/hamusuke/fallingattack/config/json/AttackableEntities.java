package com.hamusuke.fallingattack.config.json;

import com.google.gson.JsonElement;
import com.hamusuke.fallingattack.FallingAttack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class AttackableEntities extends JsonConfig {
    public AttackableEntities() {
        super(FallingAttack.MOD_ID + "-attackable-entities");
    }

    public synchronized boolean isAttackable(LivingEntity livingEntity) {
        ResourceLocation location = ForgeRegistries.ENTITIES.getKey(livingEntity.getType());
        if (location != null) {
            JsonElement b = this.jsonObject.get(location.toString());
            if (b == null) {
                this.jsonObject.addProperty(location.toString(), !livingEntity.getType().getCategory().isFriendly());
                this.save();
            } else {
                return b.getAsBoolean();
            }
        }

        return !livingEntity.getType().getCategory().isFriendly();
    }
}
