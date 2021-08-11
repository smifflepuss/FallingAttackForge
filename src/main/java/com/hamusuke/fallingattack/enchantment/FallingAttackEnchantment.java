package com.hamusuke.fallingattack.enchantment;

import com.hamusuke.fallingattack.FallingAttack;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentType;
import net.minecraft.inventory.EquipmentSlotType;

public class FallingAttackEnchantment extends Enchantment {
    public FallingAttackEnchantment() {
        super(Rarity.RARE, EnchantmentType.WEAPON, new EquipmentSlotType[]{EquipmentSlotType.MAINHAND});
        this.setRegistryName(FallingAttack.MOD_ID, "falling_attack");
    }

    public int getMaxLevel() {
        return 5;
    }
}
