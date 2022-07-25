package com.hamusuke.fallingattack.enchantment;

import com.hamusuke.fallingattack.FallingAttack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SharpnessOfFallingAttackEnchantment extends Enchantment {
    public SharpnessOfFallingAttackEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        this.setRegistryName(FallingAttack.MOD_ID, "falling_attack");
    }

    public int getMaxLevel() {
        return 5;
    }
}
