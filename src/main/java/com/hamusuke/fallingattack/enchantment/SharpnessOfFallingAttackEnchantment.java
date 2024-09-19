package com.hamusuke.fallingattack.enchantment;

import com.hamusuke.fallingattack.config.Config;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SharpnessOfFallingAttackEnchantment extends Enchantment {
    public SharpnessOfFallingAttackEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
    }

    @Override
    public boolean canEnchant(ItemStack stack) {
        return super.canEnchant(stack) || Config.Common.USABLE_ITEMS.isUsable(stack.getItem());
    }

    public int getMaxLevel() {
        return 5;
    }
}
