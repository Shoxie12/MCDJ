package com.shoxie.mcdj.entity;

import javax.annotation.Nullable;

import com.shoxie.mcdj.init.Init;
import com.shoxie.mcdj.item.BlankDiscItem;
import com.shoxie.mcdj.item.CustomDiscItem;

import com.shoxie.mcdj.menu.MusicGeneratorMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class MusicGeneratorEntity extends BlockEntity implements Nameable, MenuProvider {
	public boolean Started = false;
	public static final int MaxGenTime = 45;
	private final LazyOptional<ItemStackHandler> inventoryOptional = LazyOptional.of(() -> this.inventory);
	public int discid = 0;
	// 0 = Slot for blank 1 = Preview slot
	public int slotscnt = 2;
	private int GenTime = 0;
	
    public MusicGeneratorEntity(BlockPos p_155229_, BlockState p_155230_) {
		super(Init.MUSIC_GENERATOR_ENTITY.get(), p_155229_, p_155230_);
		
	}

    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player Player) {
        return new MusicGeneratorMenu(i, playerInventory, this);
    }
    
	public static <T> void tick(Level level, BlockPos pos, BlockState state, T blockEntity) {
		MusicGeneratorEntity mgtile = (MusicGeneratorEntity) level.getBlockEntity(pos);
    	if(mgtile.isProcessing()) {
    		mgtile.GenTime--;
    		mgtile.sendUpdates();
    	}
    	else {
	    	if (!mgtile.level.isClientSide) {
		    	ItemStack disc = mgtile.getItemInSlot(0);
		    	if(
			    	!disc.isEmpty() && (mgtile.discid >= 0 && mgtile.discid < Init.CUSTOM_RECORD_ITEMS.size()) &&
			    	disc.getItem() instanceof BlankDiscItem && mgtile.getGenTime() < 1 &&
			    	mgtile.Started
		    	)
		    		mgtile.FinaliseGen(new ItemStack(Init.CUSTOM_RECORD_ITEMS.get(mgtile.discid).get()));
	    	}
    	}
    }
    
    public void StartGen() {
    	if(this.getItemInSlot(0).getItem() instanceof RecordItem)
    	{
    		if(!(this.discid >= 0 && this.discid < Init.CUSTOM_RECORD_ITEMS.size())) {
	    		this.discid=Init.CUSTOM_RECORD_ITEMS.size()-1;
	    		return;
	    	}
	    	GenTime = MaxGenTime;
	    	Started = true;
    	}
    }
    
	private void FinaliseGen(ItemStack disc) {
    	if(!(disc.getItem() instanceof CustomDiscItem))
    		return;

		inventory.extractItem(1, 1, false);
		inventory.extractItem(0, 1, false);
		inventory.insertItem(0, disc, false);
		Started=false;
		GenTime = 0;
	}

	public ItemStackHandler getInventory(){
		return this.inventory;
	}

	public ItemStack getItemInSlot(int itnum) {
		final ItemStack[] it = new ItemStack[1];
		    it[0] = inventory.getStackInSlot(itnum);
		return it[0];
	}
    
	public void updatePreviewSlot(){
		if(this.discid >= Init.CUSTOM_RECORD_ITEMS.size()) return;

		inventory.extractItem(1, 1, false);
		inventory.insertItem(1, new ItemStack(Init.CUSTOM_RECORD_ITEMS.get(this.discid).get()), false);
	}

	public void clearPreviewSlot(){
		inventory.extractItem(1, 1, false);
	}
	
	public boolean isProcessing() {
		return (GenTime > 0);
	}
	
	public void stopGen() {
		Started = false;
		GenTime = 0;
		inventory.extractItem(1, 1, false);
		sendUpdates();
	}

	public LazyOptional<ItemStackHandler> getInventoryOptional() {
		return this.inventoryOptional;
	}

	private final ItemStackHandler inventory = new ItemStackHandler(slotscnt) {
		@Override
		protected void onContentsChanged(int slot) {
			super.onContentsChanged(slot);
			MusicGeneratorEntity.this.sendUpdates();
		}
	};

	@Override
	public void load(@NotNull CompoundTag tag) {
		super.load(tag);
		this.inventory.deserializeNBT(tag.getCompound("items"));
		GenTime = tag.getInt("GenTime");
	}

	@Override
	protected void saveAdditional(@NotNull CompoundTag tag) {
		super.saveAdditional(tag);
		tag.put("items", this.inventory.serializeNBT());
		tag.putInt("GenTime", GenTime);
	}

	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
		if (cap == ForgeCapabilities.ITEM_HANDLER) {
			return this.inventoryOptional.cast();
		}
		return super.getCapability(cap);
	}


	@Override
	public Component getDisplayName() {
		return Component.translatable("block.mcdj.musicgenerator");
	}

	public void sendUpdates() {
		level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
	}
	
    @Override
    public CompoundTag getUpdateTag() {
        return saveWithFullMetadata();
    }
	
	@Override
	@Nullable
	public Packet<ClientGamePacketListener> getUpdatePacket() {
	  return ClientboundBlockEntityDataPacket.create(this);
	}

	public int getGenTime() {
		return GenTime;
	}

	@Override
	public Component getName() {
		return getDisplayName();
	}
}