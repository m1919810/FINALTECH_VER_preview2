package io.taraxacum.finaltech.api.enchantment;

import io.taraxacum.finaltech.FinalTech;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;

/**
 * @author Final_ROOT
 * @since 1.0
 */
public class NullEnchantment extends Enchantment {
    public static final Enchantment ENCHANTMENT = new NullEnchantment(new NamespacedKey(JavaPlugin.getPlugin(FinalTech.class), "FINALTECH_NULL_ENCHANTMENT"));

    private NullEnchantment(NamespacedKey key) {
        super(key);
    }

    @Nonnull
    @Override
    public String getName() {
        return " ";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public int getStartLevel() {
        return 1;
    }

    @Nonnull
    @Override
    public EnchantmentTarget getItemTarget() {
        return EnchantmentTarget.BREAKABLE;
    }

    @Override
    public boolean isTreasure() {
        return false;
    }

    @Override
    public boolean isCursed() {
        return false;
    }

    @Override
    public boolean conflictsWith(@Nonnull Enchantment enchantment) {
        return false;
    }

    @Override
    public boolean canEnchantItem(@Nonnull ItemStack itemStack) {
        return true;
    }
}
