package com.hamusuke.fallingattack.config.json;

import com.google.gson.JsonElement;
import com.hamusuke.fallingattack.FallingAttack;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class UsableItems extends JsonConfig {
    public UsableItems() {
        super(FallingAttack.MOD_ID + "-usable-items");
    }

    public synchronized boolean isUsable(Item item) {
        if (!(item instanceof AirItem)) {
            ResourceLocation location = ForgeRegistries.ITEMS.getKey(item);
            if (location != null) {
                JsonElement b = this.jsonObject.get(location.toString());
                if (b == null) {
                    this.jsonObject.addProperty(location.toString(), item instanceof SwordItem);
                    this.save();
                } else {
                    return b.getAsBoolean();
                }
            }
        }

        return item instanceof SwordItem;
    }
}
