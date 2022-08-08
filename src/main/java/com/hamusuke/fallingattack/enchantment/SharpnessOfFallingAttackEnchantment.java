package com.hamusuke.fallingattack.enchantment;

import com.hamusuke.fallingattack.FallingAttack;
import com.hamusuke.fallingattack.config.Config;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SharpnessOfFallingAttackEnchantment extends Enchantment {
    public SharpnessOfFallingAttackEnchantment() {
        super(Rarity.RARE, EnchantmentCategory.WEAPON, new EquipmentSlot[]{EquipmentSlot.MAINHAND});
        this.setRegistryName(FallingAttack.MOD_ID, "falling_attack");
    }

    @Override
    public boolean canEnchant(ItemStack p_44689_) {
        return super.canEnchant(p_44689_) || Config.Common.USABLE_ITEMS.isUsable(p_44689_.getItem());
    }

    public int getMaxLevel() {
        return 5;
    }
}
