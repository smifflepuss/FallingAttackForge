package com.hamusuke.fallingattack.enchantment;

import com.hamusuke.fallingattack.FallingAttack;
import com.hamusuke.fallingattack.config.Config;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;

public class SharpnessOfFallingAttackEnchantment extends Enchantment {
    public SharpnessOfFallingAttackEnchantment() {
        super(Rarity.RARE, EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
        this.setRegistryName(FallingAttack.MOD_ID, "falling_attack");
    }

    @Override
    public boolean canEnchant(ItemStack p_92089_1_) {
        return super.canEnchant(p_92089_1_) || Config.Common.USABLE_ITEMS.isUsable(p_92089_1_.getItem());
    }

    public int getMaxLevel() {
        return 5;
    }
}
