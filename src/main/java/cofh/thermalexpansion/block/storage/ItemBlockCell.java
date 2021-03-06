package cofh.thermalexpansion.block.storage;

import cofh.api.energy.IEnergyContainerItem;
import cofh.core.init.CoreEnchantments;
import cofh.core.item.IEnchantableItem;
import cofh.core.util.helpers.RedstoneControlHelper;
import cofh.core.util.helpers.SecurityHelper;
import cofh.core.util.tileentity.IRedstoneControl.ControlMode;
import cofh.lib.util.capabilities.EnergyContainerItemWrapper;
import cofh.lib.util.helpers.EnergyHelper;
import cofh.lib.util.helpers.StringHelper;
import cofh.thermalexpansion.block.ItemBlockTEBase;
import cofh.thermalexpansion.init.TEProps;
import cofh.thermalexpansion.util.helpers.ReconfigurableHelper;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

import java.util.List;

public class ItemBlockCell extends ItemBlockTEBase implements IEnergyContainerItem, IEnchantableItem {

	public ItemBlockCell(Block block) {

		super(block);
		setMaxStackSize(16);
	}

	@Override
	public ItemStack setDefaultTag(ItemStack stack, int level) {

		ReconfigurableHelper.setFacing(stack, 3);
		ReconfigurableHelper.setSideCache(stack, TileCell.DEFAULT_SIDES);
		RedstoneControlHelper.setControl(stack, ControlMode.DISABLED);
		EnergyHelper.setDefaultEnergyTag(stack, 0);
		stack.getTagCompound().setByte("Level", (byte) level);

		stack.getTagCompound().setInteger("Send", TileCell.SEND[level]);
		stack.getTagCompound().setInteger("Recv", TileCell.RECV[level]);

		return stack;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {

		return "tile.thermalexpansion.storage.cell.name";
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {

		SecurityHelper.addOwnerInformation(stack, tooltip);
		if (StringHelper.displayShiftForDetail && !StringHelper.isShiftKeyDown()) {
			tooltip.add(StringHelper.shiftForDetails());
		}
		if (!StringHelper.isShiftKeyDown()) {
			return;
		}
		SecurityHelper.addAccessInformation(stack, tooltip);
		tooltip.add(StringHelper.getInfoText("info.thermalexpansion.storage.cell"));

		if (isCreative(stack)) {
			tooltip.add(StringHelper.localize("info.cofh.charge") + ": 1.21G RF");
		} else {
			tooltip.add(StringHelper.localize("info.cofh.charge") + ": " + StringHelper.getScaledNumber(getEnergyStored(stack)) + " / " + StringHelper.getScaledNumber(getMaxEnergyStored(stack)) + " RF");
		}
		tooltip.add(StringHelper.localize("info.cofh.send") + "/" + StringHelper.localize("info.cofh.receive") + ": " + StringHelper.formatNumber(stack.getTagCompound().getInteger("Send")) + "/" + StringHelper.formatNumber(stack.getTagCompound().getInteger("Recv")) + " RF/t");

		RedstoneControlHelper.addRSControlInformation(stack, tooltip);
	}

	@Override
	public boolean isDamaged(ItemStack stack) {

		return true;
	}

	@Override
	public boolean isItemTool(ItemStack stack) {

		return true;
	}

	@Override
	public int getItemEnchantability() {

		return 10;
	}

	@Override
	public double getDurabilityForDisplay(ItemStack stack) {

		if (stack.getTagCompound() == null) {
			setDefaultTag(stack);
		}
		return 1D - ((double) stack.getTagCompound().getInteger("Energy") / (double) getMaxEnergyStored(stack));
	}

	/* IEnergyContainerItem */
	@Override
	public int receiveEnergy(ItemStack container, int maxReceive, boolean simulate) {

		if (isCreative(container)) {
			return 0;
		}
		int level = getLevel(container);

		int stored = container.getTagCompound().getInteger("Energy");
		int receive = Math.min(maxReceive, Math.min(getMaxEnergyStored(container) - stored, TileCell.RECV[level]));

		if (!simulate) {
			stored += receive;
			container.getTagCompound().setInteger("Energy", stored);
		}
		return receive;
	}

	@Override
	public int extractEnergy(ItemStack container, int maxExtract, boolean simulate) {

		if (isCreative(container)) {
			return Math.min(maxExtract, TileCell.SEND[TEProps.LEVEL_MAX]);
		}
		int level = getLevel(container);

		int stored = container.getTagCompound().getInteger("Energy");
		int extract = Math.min(maxExtract, Math.min(stored, TileCell.SEND[level]));

		if (!simulate) {
			stored -= extract;
			container.getTagCompound().setInteger("Energy", stored);
		}
		return extract;
	}

	@Override
	public int getEnergyStored(ItemStack container) {

		if (container.getTagCompound() == null) {
			setDefaultTag(container);
		}
		return container.getTagCompound().getInteger("Energy");
	}

	@Override
	public int getMaxEnergyStored(ItemStack container) {

		return TileCell.getCapacity(getLevel(container), EnchantmentHelper.getEnchantmentLevel(CoreEnchantments.holding, container));
	}

	/* CAPABILITIES */
	@Override
	public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt) {

		return new EnergyContainerItemWrapper(stack, this);
	}

	/* IEnchantableItem */
	@Override
	public boolean canEnchant(ItemStack stack, Enchantment enchantment) {

		return enchantment == CoreEnchantments.holding;
	}

}
