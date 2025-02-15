package io.taraxacum.finaltech.api.operation;

import io.github.thebusybiscuit.slimefun4.core.machines.MachineOperation;
import io.taraxacum.finaltech.core.items.unusable.CopyCardItem;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ItemStackUtil;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Final_ROOT
 * @since 2.0
 */
public interface ItemSerializationConstructorOperation extends MachineOperation {
    int COPY_CARD = 1;
    int ITEM_PHONY = 2;
    int ERROR_ITEM = -1;

    static int getType(@Nonnull ItemStack item) {
        if (CopyCardItem.isValid(item)) {
            return ITEM_PHONY;
        }
        if (CopyCardItem.copiableItem(item)) {
            return COPY_CARD;
        }
        return ERROR_ITEM;
    }

    @Nullable
    static ItemSerializationConstructorOperation newInstance(@Nonnull ItemStack item) {
        int type = ItemSerializationConstructorOperation.getType(item);
        if (type == COPY_CARD) {
            ItemStack itemStack = item.clone();
            item.setAmount(0);
            return new ItemCopyCardOperation(itemStack);
        } else if (type == ITEM_PHONY) {
            ItemStack itemStack = item.clone();
            item.setAmount(0);
            return new ItemPhonyOperation(itemStack);
        }
        return null;
    }

    int getType();

    @Nonnull
    ItemStack getShowItem();

    void updateShowItem();

    int addItem(@Nullable ItemStack item);

    @Nonnull
    ItemStack getResult() ;
}
