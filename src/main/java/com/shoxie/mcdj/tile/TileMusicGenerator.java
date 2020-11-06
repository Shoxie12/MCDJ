package com.shoxie.mcdj.tile;

import javax.annotation.Nullable;

import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.item.ItemBlankRecord;
import com.shoxie.mcdj.item.ItemCustomRecord;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileMusicGenerator extends TileEntity implements ITickable{
	public boolean Started = false;
	public static final int MaxGenTime = 30;
	public int discid = 0;
	public int slotscnt = 2;
	private int GenTime = 0;
    private ItemStackHandler itemStackHandler = new ItemStackHandler(slotscnt) {
        @Override
        protected void onContentsChanged(int slot) {
        	TileMusicGenerator.this.markDirty();
        }
    };
	
    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        GenTime = compound.getInteger("GenTime");
        if (compound.hasKey("items")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("GenTime", GenTime);
        compound.setTag("items", itemStackHandler.serializeNBT());
        return compound;
    }
    
    public boolean canInteractWith(EntityPlayer playerIn) {
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }
    
    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemStackHandler);
        }
        return super.getCapability(capability, facing);
    }

	public int getGenTime() {
		return GenTime;
	}
    
	public void sendUpdates() {
		world.notifyBlockUpdate(pos, world.getBlockState(this.getPos()), world.getBlockState(this.getPos()), 2);
		markDirty();
	}
	
	@Override
	public ITextComponent getDisplayName() {
		if (!world.isRemote) 
			return new TextComponentTranslation("block.mcdj.musicgenerator");
		return null;
	}
	
	@Override
	@Nullable
    public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		return this.writeToNBT(new NBTTagCompound());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(pkt.getNbtCompound());
	}
	
	@Override
	public void update() {
    	if(isProcessing()) {
    		GenTime = getGenTime() - 1;
    		mcdj.logger.debug(GenTime);
    		sendUpdates();
    	}
    	else {
	    	if (!this.world.isRemote) {
		    	ItemStack disc = this.getItemInSlot(0);
		    	if(disc != null && mcdj.musicloaded)
			    	if(
				    	!disc.isEmpty() && (this.discid >= 0 && this.discid < ModItems.RECORDS.length) &&
				    	disc.getItem() instanceof ItemBlankRecord && this.getGenTime() < 1 &&
				    	Started
			    	)
			    	FinaliseGen(new ItemStack(ModItems.RECORDS[this.discid]));
	    	}
    	}
	}

    public void StartGen() {
    	if(this.getItemInSlot(0).getItem() instanceof ItemRecord)
    	{
    		if(!(this.discid >= 0 && this.discid < ModItems.RECORDS.length)) {
	    		this.discid=ModItems.RECORDS.length-1; 
	    		return;
	    	}
	    	
	    	GenTime = MaxGenTime;
	    	itemStackHandler.extractItem(1, 1, false);
	    	itemStackHandler.insertItem(1, new ItemStack(ModItems.RECORDS[discid]), false);
	    	Started = true;
    	}
    }
    
	private void FinaliseGen(ItemStack disc) {
    	if(!(disc.getItem() instanceof ItemCustomRecord))
    		return;
	    	
    	itemStackHandler.extractItem(1, 1, false);
    	itemStackHandler.extractItem(0, 1, false);
    	itemStackHandler.insertItem(0, disc, false);
		Started=false;
		GenTime = 0;
	}

	public ItemStack getItemInSlot(int itnum) {
		return itemStackHandler.getStackInSlot(itnum);
	}
    
	public boolean isProcessing() {
		return (GenTime > 0);
	}
	
	public void stopGen() {
		Started = false;
		GenTime = 0;
	    itemStackHandler.extractItem(1, 1, false);
		
	}
	
}
