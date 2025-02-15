package io.taraxacum.finaltech.api.operation;

import io.github.thebusybiscuit.slimefun4.libraries.dough.items.CustomItemStack;
import io.github.thebusybiscuit.slimefun4.utils.itemstack.ItemStackWrapper;
import io.taraxacum.finaltech.core.items.unusable.CopyCardItem;
import io.taraxacum.finaltech.core.items.unusable.ItemPhony;
import io.taraxacum.finaltech.core.items.unusable.Singularity;
import io.taraxacum.finaltech.core.items.unusable.Spirochete;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ItemStackUtil;
import io.taraxacum.finaltech.util.TextUtil;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Final_ROOT
 * @since 1.0
 */
public class ItemPhonyOperation implements ItemSerializationConstructorOperation {
    private int itemTypeCount;
    private int itemAmountCount;
    private final int itemTypeDifficulty;
    private final int itemAmountDifficulty;
    private final ItemStack showItem;
    private final List<ItemStackWrapper> itemTypeList = new ArrayList<>(Spirochete.SPIROCHETE_DIFFICULTY);

    protected ItemPhonyOperation(@Nonnull ItemStack item) {
        this.itemTypeCount = 1;
        this.itemAmountCount = item.getAmount();
        this.itemTypeDifficulty = Spirochete.SPIROCHETE_DIFFICULTY;
        this.itemAmountDifficulty = Singularity.SINGULARITY_DIFFICULTY;
        this.showItem = new CustomItemStack(FinalTechItems.PHONY.getType(), TextUtil.COLOR_NORMAL + "完成进度", TextUtil.COLOR_NORMAL + "物品个数= " + TextUtil.COLOR_NUMBER + this.itemAmountCount + "/" + this.itemAmountDifficulty, TextUtil.COLOR_NORMAL + "物品种数= " + TextUtil.COLOR_NUMBER + this.itemTypeCount + "/" + this.itemTypeDifficulty);
        this.itemTypeList.add(ItemStackWrapper.wrap(item));
    }

    @Override
    public int getType() {
        return ItemSerializationConstructorOperation.ITEM_PHONY;
    }

    @Nonnull
    @Override
    public ItemStack getShowItem() {
        return this.showItem;
    }

    @Override
    public void updateShowItem() {
        ItemStackUtil.setLore(this.showItem, TextUtil.COLOR_NORMAL + "物品个数= " + TextUtil.COLOR_NUMBER + this.itemAmountCount + "/" + this.itemAmountDifficulty, TextUtil.COLOR_NORMAL + "物品种数= " + TextUtil.COLOR_NUMBER + this.itemTypeCount + "/" + this.itemTypeDifficulty);
    }

    @Override
    public int addItem(@Nullable ItemStack item) {
        if (!CopyCardItem.isValid(item)) {
            return 0;
        }

        if (this.itemTypeCount <= this.itemTypeDifficulty) {
            boolean newType = true;
            ItemStackWrapper itemWrapper = ItemStackWrapper.wrap(item);
            for (ItemStackWrapper itemTypeWrapper : this.itemTypeList) {
                if (ItemStackUtil.isItemSimilar(itemWrapper, itemTypeWrapper)) {
                    newType = false;
                    break;
                }
            }
            if(newType) {
                this.itemTypeCount++;
                this.itemTypeList.add(itemWrapper);
            }
        }
        int amount = Math.min(item.getAmount(), this.itemAmountDifficulty - this.itemAmountCount);
        this.itemAmountCount += amount;

        item.setAmount(item.getAmount() - amount);

        return amount;
    }

    @Override
    public boolean isFinished() {
        return this.itemAmountCount >= this.itemAmountDifficulty || this.itemTypeCount >= this.itemTypeDifficulty;
    }

    @Nonnull
    @Override
    public ItemStack getResult() {
        if (this.itemAmountCount >= this.itemAmountDifficulty && this.itemTypeCount >= this.itemTypeDifficulty) {
            return ItemPhony.newItem(null, null, null);
        }
        if (this.itemAmountCount >= this.itemAmountDifficulty) {
            return Singularity.newItem(null, null);
        }
        if (this.itemTypeCount >= this.itemTypeDifficulty) {
            return Spirochete.newItem(null, null);
        }
        return ItemStackUtil.AIR;
    }

    @Deprecated
    @Override
    public void addProgress(int i) {

    }

    @Deprecated
    @Override
    public int getProgress() {
        return 0;
    }

    @Deprecated
    @Override
    public int getTotalTicks() {
        return 0;
    }
}
