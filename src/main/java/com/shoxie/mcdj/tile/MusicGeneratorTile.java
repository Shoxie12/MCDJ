package com.shoxie.mcdj.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.ModTileEntities;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.container.MusicGeneratorContainer;
import com.shoxie.mcdj.item.BlankDiscItem;
import com.shoxie.mcdj.item.CustomDiscItem;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class MusicGeneratorTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {
	public boolean Started = false;
	public static final int MaxGenTime = 30;
	private LazyOptional<IItemHandler> handler = LazyOptional.of(this::createHandler);
	public int discid = 0;
	public int slotscnt = 2;
	private int GenTime = 0;
	
    public MusicGeneratorTile() {
		super(ModTileEntities.TILE_MG);
		
	}

    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new MusicGeneratorContainer(i, this, playerEntity, playerInventory);
    }
    
    public void tick() {
    	if(isProcessing()) {
    		GenTime--;
    		sendUpdates();
    	}
    	else {
	    	if (!this.world.isRemote) {
		    	ItemStack disc = this.getItemInSlot(0);
		    	if(disc != null && mcdj.musicloaded)
			    	if(
				    	!disc.isEmpty() && (this.discid >= 0 && this.discid < ModItems.RECORDS.length) &&
				    	disc.getItem() instanceof BlankDiscItem && this.getGenTime() < 1 &&
				    	Started
			    	)
			    	FinaliseGen(new ItemStack(ModItems.RECORDS[this.discid]));
	    	}
    	}
    }
    
    public void StartGen() {
    	if(this.getItemInSlot(0).getItem() instanceof MusicDiscItem)
    	{
    		if(!(this.discid >= 0 && this.discid < ModItems.RECORDS.length)) {
	    		this.discid=ModItems.RECORDS.length-1; 
	    		return;
	    	}
    		
	    	GenTime = MaxGenTime;
	    	Started = true;
    	}
    }
    
	private void FinaliseGen(ItemStack disc) {
    	if(!(disc.getItem() instanceof CustomDiscItem))
    		return;

	    handler.ifPresent(h -> {
	    	h.extractItem(1, 1, false);
	    	h.extractItem(0, 1, false);
	    	h.insertItem(0, disc, false);
	    });
		Started=false;
		GenTime = 0;
	}

	public ItemStack getItemInSlot(int itnum) {
		final ItemStack[] it = new ItemStack[1];
		    handler.ifPresent(h -> {
		    it[0] = h.getStackInSlot(itnum);
		});
		return it[0];
	}
    
	public boolean isProcessing() {
		return (GenTime > 0);
	}
	
	public void stopGen() {
		Started = false;
		GenTime = 0;
	    handler.ifPresent(h -> {
	    	h.extractItem(1, 1, false);
	    });
		sendUpdates();
		
	}

	private IItemHandler createHandler() {
        return new ItemStackHandler(slotscnt) {
            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        };
    }
    
    @Override
    public void func_230337_a_(BlockState p_230337_1_, CompoundNBT tag) {
	    CompoundNBT compound = tag.getCompound("items");
	    GenTime = tag.getInt("GenTime");
	    handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(compound));
	    super.func_230337_a_(p_230337_1_, tag);
    }
    
    @Override
    public CompoundNBT write(CompoundNBT tag) {
	    handler.ifPresent(h -> {
		    CompoundNBT compound = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
		    tag.put("items", compound);
		    tag.putInt("GenTime", GenTime);
	    });
    return super.write(tag);
    }
    
	@Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
        	return handler.cast();
        }
        return super.getCapability(cap, side);
    }
    
	@Override
	public ITextComponent getDisplayName() {
		if (!world.isRemote) 
			return new TranslationTextComponent("block.mcdj.musicgenerator");
		return null;
	}
	public void sendUpdates() {
		world.notifyBlockUpdate(pos, getBlockState(), getBlockState(), 2);
		markDirty();
	}
	
	@Override
	@Nullable
	public SUpdateTileEntityPacket getUpdatePacket() {
		return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}
	
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		super.onDataPacket(net, pkt);
		handleUpdateTag(getBlockState(), pkt.getNbtCompound());
	}

	public int getGenTime() {
		return GenTime;
	}
}