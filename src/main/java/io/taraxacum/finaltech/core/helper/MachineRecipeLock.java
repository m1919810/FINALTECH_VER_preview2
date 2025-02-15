package io.taraxacum.finaltech.core.helper;

import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.taraxacum.finaltech.api.dto.AdvancedMachineRecipe;
import io.taraxacum.finaltech.api.dto.ItemStackWithWrapperAmount;
import io.taraxacum.finaltech.core.factory.BlockStorageHelper;
import io.taraxacum.finaltech.core.factory.BlockStorageLoreHelper;
import io.taraxacum.finaltech.core.factory.MachineRecipeFactory;
import io.taraxacum.finaltech.core.items.machine.AbstractMachine;
import io.taraxacum.finaltech.core.menu.AbstractMachineMenu;
import io.taraxacum.finaltech.util.ItemStackUtil;
import io.taraxacum.finaltech.util.TextUtil;
import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.ChestMenu;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Final_ROOT
 */
public final class MachineRecipeLock {
    public static final String KEY = "rl";

    public static final String VALUE_UNLOCK = "-1";
    public static final String VALUE_LOCK_OFF = "-2";

    public static final ItemStack ICON = new CustomItemStack(Material.TRIPWIRE_HOOK, TextUtil.colorPseudorandomString("配方锁"), TextUtil.COLOR_PASSIVE + "禁用锁定");

    public static final BlockStorageLoreHelper HELPER = new BlockStorageLoreHelper(BlockStorageHelper.ID_CARGO, new LinkedHashMap<>(2) {{
        this.put("-2", List.of(TextUtil.COLOR_PASSIVE + "禁用锁定"));
        this.put("-1", List.of(TextUtil.COLOR_INITIATIVE + "未锁定"));
    }}) {
        @Nonnull
        @Override
        public String getKey() {
            return KEY;
        }

        @Nonnull
        @Override
        public String nextOrDefaultValue(@Nullable String value) {
            return this.defaultValue();
        }

        @Nonnull
        @Override
        public String previousOrDefaultValue(@Nullable String value) {
            return this.defaultValue();
        }

        @Nonnull
        @Override
        public ChestMenu.MenuClickHandler getHandler(@Nonnull BlockMenu blockMenu, @Nonnull Block block, @Nonnull AbstractMachineMenu abstractMachineMenu, int slot) {
            return (player, i, itemStack, clickAction) -> {
                HELPER.checkOrSetBlockStorage(block.getLocation());
                String value = clickAction.isRightClicked() ? VALUE_LOCK_OFF : VALUE_UNLOCK;
                HELPER.setIcon(blockMenu.getItemInSlot(slot), value);
                BlockStorage.addBlockInfo(block.getLocation(), KEY, value);
                return false;
            };
        }

        @Override
        public boolean setIcon(@Nonnull ItemStack item, @Nullable String value, @Nonnull AbstractMachine abstractMachine) {
            if (this.validValue(value)) {
                super.setIcon(item, value);
                return true;
            } else {
                int recipeLock = value == null ? Integer.parseInt(this.defaultValue()) :  Integer.parseInt(value);
                List<AdvancedMachineRecipe> advancedMachineRecipeList = MachineRecipeFactory.getInstance().getAdvancedRecipe(abstractMachine.getClass());
                if (recipeLock < advancedMachineRecipeList.size() && recipeLock >= 0) {
                    AdvancedMachineRecipe advancedMachineRecipe = advancedMachineRecipeList.get(recipeLock);
                    List<String> loreList;
                    if (advancedMachineRecipe.getOutputList().size() == 1) {
                        loreList = new ArrayList<>(advancedMachineRecipe.getInput().size() + advancedMachineRecipe.getOutputList().get(0).getOutputItem().size() + 3);
                        loreList.add("§9输入:");
                        for (ItemStackWithWrapperAmount inputItem : advancedMachineRecipe.getInput()) {
                            loreList.add("    §f" + ItemStackUtil.getItemName(inputItem.getItemStack()) + TextUtil.COLOR_NUMBER + " x" + inputItem.getAmount());
                        }
                        loreList.add("");
                        loreList.add("§6输出:");
                        for (ItemStackWithWrapperAmount outputItem : advancedMachineRecipe.getOutputList().get(0).getOutputItem()) {
                            loreList.add("    §f" + ItemStackUtil.getItemName(outputItem.getItemStack()) + TextUtil.COLOR_NUMBER + " x" + outputItem.getAmount());
                        }
                    } else {
                        int outputLength = 0;
                        for (AdvancedMachineRecipe.AdvancedRandomOutput advancedRandomOutput : advancedMachineRecipe.getOutputList()) {
                            outputLength += advancedRandomOutput.getOutputItem().size() + 1;
                        }
                        loreList = new ArrayList<>(advancedMachineRecipe.getInput().size() + outputLength + 3);
                        loreList.add("§9输入:");
                        for (ItemStackWithWrapperAmount inputItem : advancedMachineRecipe.getInput()) {
                            loreList.add("    §f" + ItemStackUtil.getItemName(inputItem.getItemStack()) + TextUtil.COLOR_NUMBER + " x" + inputItem.getAmount());
                        }
                        loreList.add("");
                        loreList.add("§6输出:");
                        for (AdvancedMachineRecipe.AdvancedRandomOutput advancedRandomOutput : advancedMachineRecipe.getOutputList()) {
                            String random = String.valueOf(((double) advancedRandomOutput.getWeight()) / advancedMachineRecipe.getWeightSum() * 100.0);
                            if (random.contains(".")) {
                                random = random.substring(0, Math.min(random.indexOf(".") + 3, random.length()));
                            }
                            loreList.add("  §a" + random + "%");
                            for (ItemStackWithWrapperAmount outputItem : advancedRandomOutput.getOutputItem()) {
                                loreList.add("    §f" + ItemStackUtil.getItemName(outputItem.getItemStack()) + TextUtil.COLOR_NUMBER + " x" + outputItem.getAmount());
                            }
                        }
                    }
                    ItemStackUtil.setLore(item, loreList);
                }
                return false;
            }
        }
    };
}
