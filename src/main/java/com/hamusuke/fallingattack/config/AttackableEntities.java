package com.hamusuke.fallingattack.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class AttackableEntities {
    private final ForgeConfigSpec.Builder builder;
    private final ImmutableMap<ResourceLocation, ForgeConfigSpec.BooleanValue> attackableMap;

    public AttackableEntities(ForgeConfigSpec.Builder builder) {
        this.builder = builder;
        this.builder.push("AttackableEntities");
        this.attackableMap = ImmutableMap.copyOf(this.registerConfig());
        this.builder.pop();
    }

    private Map<ResourceLocation, ForgeConfigSpec.BooleanValue> registerConfig() {
        Map<ResourceLocation, ForgeConfigSpec.BooleanValue> map = Maps.newHashMap();
        Collection<EntityType<?>> values = ForgeRegistries.ENTITY_TYPES.getValues();
        for (EntityType<?> type : values) {
            ResourceLocation name = Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(type));
            map.put(name, this.builder.define(name.toString(), !type.getCategory().isFriendly()));
        }

        return map;
    }

    public boolean isAttackable(LivingEntity livingEntity) {
        ResourceLocation location = ForgeRegistries.ENTITY_TYPES.getKey(livingEntity.getType());
        if (location != null) {
            ForgeConfigSpec.BooleanValue booleanValue = this.attackableMap.get(location);
            return booleanValue != null && booleanValue.get();
        }

        return false;
    }
}
