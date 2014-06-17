package net.mcft.copy.backpacks.item;

import net.mcft.copy.backpacks.api.BackpackHelper;
import net.mcft.copy.backpacks.api.IBackpack;
import net.mcft.copy.backpacks.api.IBackpackData;
import net.mcft.copy.backpacks.api.IBackpackProperties;
import net.mcft.copy.backpacks.client.BackpackResources;
import net.mcft.copy.backpacks.misc.BackpackDataItems;
import net.mcft.copy.core.container.ContainerBase;
import net.mcft.copy.core.inventory.InventoryStacks;
import net.mcft.copy.core.misc.BlockLocation;
import net.mcft.copy.core.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBackpack extends ItemBlock implements IBackpack, ISpecialArmor {
	
	public ItemBackpack(Block block) {
		super(block);
		setMaxStackSize(1);
	}
	
	// IBackpack implementation
	
	@Override
	public void onSpawnedWith(EntityLivingBase entity) {
		// TODO: Fill backpack with random items.
	}
	
	@Override
	public <T extends TileEntity & IBackpackProperties> void onEquip(EntityLivingBase entity, T tileEntity) {  }
	
	@Override
	public <T extends TileEntity & IBackpackProperties> void onUnequip(EntityLivingBase entity, T tileEntity) {  }
	
	@Override
	public <T extends TileEntity & IBackpackProperties> void onPlacedInteract(EntityPlayer player, final T tileEntity) {
		if (player.worldObj.isRemote) return;
		BackpackDataItems data = (BackpackDataItems)tileEntity.getBackpackData();
		ContainerBase.create(player,
				new InventoryStacks(data.items) {
						@Override public String getInventoryName() { return "Backpack"; }
						@Override public boolean hasCustomInventoryName() { return true; }
						@Override public void openInventory() {
							tileEntity.setPlayersUsing(tileEntity.getPlayersUsing() + 1); }
						@Override public void closeInventory() {
							tileEntity.setPlayersUsing(tileEntity.getPlayersUsing() - 1); }
					}).open();
	}
	
	@Override
	public void onEquippedInteract(EntityPlayer player, EntityLivingBase target) {
		
	}
	
	@Override
	public void onEquippedTick(EntityLivingBase entity) {  }
	
	@Override
	public void onDeath(EntityLivingBase entity) {
		IBackpackData data = BackpackHelper.getEquippedBackpackData(entity);
		if (!(data instanceof BackpackDataItems)) return;
		BackpackDataItems dataItems = (BackpackDataItems)data;
		// TODO: Drop items.
	}

	@Override
	public void onFaultyRemoval(EntityLivingBase entity) {
		onDeath(entity);
	}
	
	@Override
	public <T extends TileEntity & IBackpackProperties> void onBlockBreak(T tileEntity) {
		BackpackDataItems data = (BackpackDataItems)tileEntity.getBackpackData();
		for (ItemStack stack : data.items)
			WorldUtils.dropStackFromBlock(tileEntity, stack);
	}
	
	@Override
	public IBackpackData createBackpackData() {
		return new BackpackDataItems(36); // TODO: Get configurable backpack size.
	}
	
	
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getModel(ItemStack backpack) {
		return BackpackResources.modelBackpack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public ResourceLocation getTexture(ItemStack backpack, int pass) {
		return ((pass == 0) ? BackpackResources.textureBackpack
		                    : BackpackResources.textureBackpackOverlay);
	}
	
	
	@Override
	public int getLidMaxTicks() { return 5; }
	
	@Override
	public float getLidAngle(int prevLidTicks, int lidTicks, float partialTicks) {
		float progress = lidTicks + (lidTicks - prevLidTicks) * partialTicks;
		progress = Math.max(0, Math.min(getLidMaxTicks(), progress)) / getLidMaxTicks();
		return (1.0F - (float)Math.pow(1.0F - progress, 2)) * 45;
	}
	
	// Item methods
	
	@Override
	public int getRenderPasses(int metadata) { return 2; }
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack stack, int pass) {
		if (pass != 0) return 0xFFFFFF;
		return 0xA06540;
	}
	
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player,
	                         World world, int x, int y, int z, int side,
	                         float hitX, float hitY, float hitZ) {
		// Check if the backpack is being placed on top of a solid block.
		BlockLocation block = BlockLocation.get(world, x, y, z);
		if (block.isReplaceable()
				? !block.below().isSideSolid(ForgeDirection.UP)
				: ((side != ForgeDirection.UP.ordinal()) || !block.isSideSolid(ForgeDirection.UP)))
			return false;
		return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
	}
	
	// ISpecialArmor implementation
	
	@Override
	public int getArmorDisplay(EntityPlayer player, ItemStack armor, int slot) { return 2; }
	
	@Override
	public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor,
	                                     DamageSource source, double damage, int slot) {
		return new ArmorProperties(0, 12.5, armor.getMaxDamage() + 1 - armor.getItemDamage());
	}
	
	@Override
	public void damageArmor(EntityLivingBase entity, ItemStack armor,
	                        DamageSource source, int damage, int slot) {
		armor.damageItem(damage, entity);
	}
	
}
