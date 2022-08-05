package com.hamusuke.fallingattack.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class UsableItems {
    private final ForgeConfigSpec.Builder builder;
    private final ImmutableMap<ResourceLocation, ForgeConfigSpec.BooleanValue> usableMap;

    public UsableItems(ForgeConfigSpec.Builder builder) {
        this.builder = builder;
        this.builder.push("UsableItems");
        this.usableMap = ImmutableMap.copyOf(this.registerConfig());
        this.builder.pop();
    }

    private Map<ResourceLocation, ForgeConfigSpec.BooleanValue> registerConfig() {
        Map<ResourceLocation, ForgeConfigSpec.BooleanValue> map = Maps.newHashMap();
        Collection<Item> values = ForgeRegistries.ITEMS.getValues();
        for (Item item : values) {
            ResourceLocation name = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(item));
            map.put(name, this.builder.define(name.toString(), item instanceof SwordItem));
        }

        return map;
    }

    public boolean isUsable(Item item) {
        ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
        if (location != null) {
            ForgeConfigSpec.BooleanValue booleanValue = this.usableMap.get(location);
            return booleanValue != null && booleanValue.get();
        }

        return false;
    }
}
