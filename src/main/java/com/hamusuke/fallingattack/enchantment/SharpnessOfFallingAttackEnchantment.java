package com.hamusuke.fallingattack.enchantment;

import com.hamusuke.fallingattack.FallingAttack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class SharpnessOfFallingAttackEnchantment extends Enchantment {
    public SharpnessOfFallingAttackEnchantment() {
        super(Rarity.RARE, EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
        this.setRegistryName(FallingAttack.MOD_ID, "sharpness_of_falling_attack");
    }

    public int getMaxLevel() {
        return 5;
    }
}
