package com.hamusuke.fallingattack.config;

import com.google.common.collect.ImmutableList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.compress.utils.Lists;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class AttackableEntities {
    private final ForgeConfigSpec.Builder builder;
    private final ImmutableList<Attackable> attackables;

    public AttackableEntities(ForgeConfigSpec.Builder builder) {
        this.builder = builder;
        this.attackables = ImmutableList.copyOf(this.registerConfig());
    }

    private List<Attackable> registerConfig() {
        List<Attackable> list = Lists.newArrayList();
        Collection<EntityType<?>> values = ForgeRegistries.ENTITY_TYPES.getValues();
        for (EntityType<?> type : values) {
            list.add(new Attackable(this.builder, Objects.requireNonNull(ForgeRegistries.ENTITY_TYPES.getKey(type)), !type.getCategory().isFriendly()));
        }

        return list;
    }

    public ImmutableList<Attackable> getAttackables() {
        return this.attackables;
    }

    public static class Attackable {
        public final ForgeConfigSpec.BooleanValue booleanValue;
        public final ResourceLocation name;

        public Attackable(ForgeConfigSpec.Builder builder, ResourceLocation name, boolean defaultV) {
            this.booleanValue = builder.define(name.toString(), defaultV);
            this.name = name;
        }
    }
}
