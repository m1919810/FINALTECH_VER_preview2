package io.taraxacum.finaltech.api.command;

import io.taraxacum.finaltech.core.items.unusable.CopyCardItem;
import io.taraxacum.finaltech.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

/**
 * A {@link Command} that will transfer item in player's hand to a {@link CopyCardItem}.
 * @author Final_ROOT
 * @since 2.0
 */
public class TransferToCopyCardItem implements CommandExecutor {
    @Override
    public boolean onCommand(@Nonnull CommandSender commandSender, @Nonnull Command command, @Nonnull String s, @Nonnull String[] strings) {
        if (commandSender instanceof Player player) {
            ItemStack item = player.getItemInHand();
            if(ItemStackUtil.isItemNull(item) || !CopyCardItem.copiableItem(item)) {
                return false;
            }
            ItemStack copyCardItem = CopyCardItem.newItem(item, "1");
            player.setItemInHand(copyCardItem);
            return true;
        } else {
            Bukkit.getLogger().info("Not support for console");
        }
        return false;
    }
}
