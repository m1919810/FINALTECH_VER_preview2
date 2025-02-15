package io.taraxacum.finaltech.core.items.machine.range.area.generator;

import io.github.thebusybiscuit.slimefun4.api.items.ItemGroup;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItem;
import io.github.thebusybiscuit.slimefun4.api.items.SlimefunItemStack;
import io.github.thebusybiscuit.slimefun4.api.recipes.RecipeType;
import io.github.thebusybiscuit.slimefun4.core.attributes.EnergyNetComponent;
import io.github.thebusybiscuit.slimefun4.implementation.Slimefun;
import io.taraxacum.finaltech.api.interfaces.AntiAccelerationMachine;
import io.taraxacum.finaltech.core.menu.AbstractMachineMenu;
import io.taraxacum.finaltech.core.menu.unit.StatusL2Menu;
import io.taraxacum.finaltech.core.menu.unit.StatusMenu;
import io.taraxacum.finaltech.setup.FinalTechItems;
import io.taraxacum.finaltech.util.ItemStackUtil;
import io.taraxacum.finaltech.util.SlimefunUtil;
import io.taraxacum.common.util.StringNumberUtil;
import io.taraxacum.finaltech.util.TextUtil;
import me.mrCookieSlime.CSCoreLibPlugin.Configuration.Config;
import me.mrCookieSlime.Slimefun.api.BlockStorage;
import me.mrCookieSlime.Slimefun.api.inventory.BlockMenu;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Final_ROOT
 * @since 1.0
 */
public class MatrixGenerator extends AbstractCubeElectricGenerator implements AntiAccelerationMachine {
    protected static final String KEY = "energy-charge";

    public final static String ELECTRICITY = StringNumberUtil.VALUE_INFINITY;
    public final static int RANGE = 10;

    @Nonnull
    @Override
    protected AbstractMachineMenu setMachineMenu() {
        return new StatusL2Menu(this);
    }

    public MatrixGenerator(ItemGroup itemGroup, SlimefunItemStack item, RecipeType recipeType, ItemStack[] recipe) {
        super(itemGroup, item, recipeType, recipe);
    }

    @Override
    protected void tick(@Nonnull Block block, @Nonnull SlimefunItem slimefunItem, @Nonnull Config config) {
        BlockMenu blockMenu = BlockStorage.getInventory(block);
        World world = block.getWorld();
        int extraRange = 0;
        for (int slot : this.getInputSlot()) {
            ItemStack item = blockMenu.getItemInSlot(slot);
            if (!ItemStackUtil.isItemNull(item) && ItemStackUtil.isItemSimilar(item, FinalTechItems.PHONY)) {
                int amount = item.getAmount() / 2;
                while (amount > 0) {
                    extraRange++;
                    amount /= 2;
                }
            }
        }
        AtomicReference<String> energyCharge = new AtomicReference<>(StringNumberUtil.ZERO);
        int count = this.function(block, extraRange + this.getRange(), location -> {
            if (BlockStorage.hasBlockInfo(location)) {
                Config energyComponentConfig = BlockStorage.getLocationInfo(location);
                if (energyComponentConfig.contains(SlimefunUtil.KEY_ID)) {
                    String slimefunItemId = energyComponentConfig.getString(SlimefunUtil.KEY_ID);
                    if (slimefunItemId.equals(this.getId()) && !location.equals(block.getLocation())) {
                        //todo Event
                        Slimefun.runSync(() -> {
                            List<ItemStack> dropItemList = new ArrayList<>(this.getInputSlot().length + 1);
                            for (int slot : MatrixGenerator.this.getInputSlot()) {
                                ItemStack item = blockMenu.getItemInSlot(slot);
                                if (!ItemStackUtil.isItemNull(item)) {
                                    dropItemList.add(blockMenu.getItemInSlot(slot));
                                }
                            }
                            dropItemList.add(this.getItem());
                            block.setType(Material.AIR);
                            BlockStorage.clearBlockInfo(block.getLocation());
                            for (ItemStack item : dropItemList) {
                                block.getWorld().dropItem(block.getLocation(), item);
                            }
                        });
                        return -1;
                    }
                    SlimefunItem item = SlimefunItem.getById(energyComponentConfig.getString(SlimefunUtil.KEY_ID));
                    if (item instanceof EnergyNetComponent) {
                        int componentCapacity = ((EnergyNetComponent) item).getCapacity();
                        if (componentCapacity == 0) {
                            return 0;
                        }
                        String componentEnergy = energyComponentConfig.contains(KEY) ? energyComponentConfig.getString(KEY) : StringNumberUtil.ZERO;
                        if (StringNumberUtil.easilyCompare(componentEnergy, String.valueOf(componentCapacity)) >= 0) {
                            return 0;
                        }
                        String transferEnergy = StringNumberUtil.min(StringNumberUtil.sub(String.valueOf(componentCapacity), componentEnergy), this.getElectricity());
                        if (StringNumberUtil.easilyCompare(transferEnergy, StringNumberUtil.ZERO) > 0) {
                            componentEnergy = StringNumberUtil.add(componentEnergy, transferEnergy);
                            energyCharge.set(StringNumberUtil.add(energyCharge.get(), transferEnergy));
                            SlimefunUtil.setCharge(energyComponentConfig, componentEnergy);
                            world.spawnParticle(Particle.COMPOSTER, location.getX() + 0.5, location.getY() + 1, location.getZ() + 0.5, 1);
                            return 1;
                        }
                    }
                }
            }
            return 0;
        });
        if(blockMenu.hasViewer()) {
            ItemStack item = blockMenu.getItemInSlot(StatusMenu.STATUS_SLOT);
            ItemStackUtil.setLore(item,
                    TextUtil.COLOR_NORMAL + "当前生效的机器= " + TextUtil.COLOR_NUMBER + count + "个",
                    TextUtil.COLOR_NORMAL + "实际发电量= " + TextUtil.COLOR_NUMBER + energyCharge + "J");
            if (count == 0) {
                item.setType(Material.RED_STAINED_GLASS_PANE);
            } else {
                item.setType(Material.GREEN_STAINED_GLASS_PANE);
            }
        }
    }

    @Override
    public void registerDefaultRecipes() {
        registerDescriptiveRecipe(TextUtil.COLOR_PASSIVE + "机制",
                "",
                TextUtil.COLOR_NORMAL + "每 " + TextUtil.COLOR_NUMBER + String.format("%.2f", Slimefun.getTickerTask().getTickRate() / 20.0) + "秒" + TextUtil.COLOR_NORMAL + " 对周围 " + TextUtil.COLOR_NUMBER + this.getRange() + "格" + TextUtil.COLOR_NORMAL + " 的机器进行充电",
                TextUtil.COLOR_NORMAL + "每次充电使其电量充满至最大电容量");
        this.registerDescriptiveRecipe(TextUtil.COLOR_PASSIVE + "极化扩展",
                "",
                TextUtil.COLOR_NORMAL + "放入 " + FinalTechItems.PHONY.getDisplayName() + TextUtil.COLOR_NORMAL + " 后 其工作范围会扩大");
    }

    @Override
    protected String getElectricity() {
        return ELECTRICITY;
    }

    @Override
    protected int getRange() {
        return RANGE;
    }
}
