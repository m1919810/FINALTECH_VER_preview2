package io.taraxacum.finaltech.core.items;

import io.github.thebusybiscuit.slimefun4.api.SlimefunAddon;
import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.taraxacum.finaltech.FinalTech;
import io.taraxacum.finaltech.api.interfaces.RecipeItem;
import io.taraxacum.finaltech.core.factory.MachineRecipeFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * We may add something soon
 * @author Final_ROOT
 * @since 2.0
 */
public abstract class AbstractMySlimefunItem extends SlimefunItem {
    public AbstractMySlimefunItem(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item, @Nonnull RecipeType recipeType, @Nonnull ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    public AbstractMySlimefunItem(@Nonnull ItemGroup itemGroup, @Nonnull SlimefunItemStack item, @Nonnull RecipeType recipeType, @Nonnull ItemStack[] recipe, @Nullable ItemStack recipeOutput) {
        super(itemGroup, item, recipeType, recipe, recipeOutput);
    }

    protected AbstractMySlimefunItem(@Nonnull ItemGroup itemGroup, @Nonnull ItemStack item, @Nonnull String id, @Nonnull RecipeType recipeType, @Nonnull ItemStack[] recipe) {
        super(itemGroup, item, id, recipeType, recipe);
    }

    @Override
    public void register(@Nonnull SlimefunAddon addon) {
        super.register(addon);
        if (this instanceof RecipeItem) {
            this.getAddon().getJavaPlugin().getServer().getScheduler().runTask((Plugin)addon, () -> {
                ((RecipeItem) AbstractMySlimefunItem.this).registerDefaultRecipes();
                MachineRecipeFactory.getInstance().initAdvancedRecipeMap(AbstractMySlimefunItem.this.getClass());
            });
        }
    }

    public void register() {
        this.register(JavaPlugin.getPlugin(FinalTech.class));
    }
}
