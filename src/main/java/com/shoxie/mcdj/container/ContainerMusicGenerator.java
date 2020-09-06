package com.shoxie.mcdj.container;

import com.shoxie.mcdj.slot.MusicGeneratorMainSlot;
import com.shoxie.mcdj.slot.MusicGeneratorPreviewSlot;
import com.shoxie.mcdj.tile.TileMusicGenerator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class ContainerMusicGenerator extends Container {

    private TileMusicGenerator tile;
    public ContainerMusicGenerator(InventoryPlayer playerInventory, TileMusicGenerator tile) {
    	this.tile = tile;
    	
        IItemHandler itemHandler = this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        addSlotToContainer(new MusicGeneratorMainSlot(itemHandler, 0, 77, 75, tile));
        addSlotToContainer(new MusicGeneratorPreviewSlot(itemHandler, 1, 77, 24));
        layoutPlayerInventorySlots(8, 100, playerInventory);
        }
    
    private int addSlotRange(IInventory handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
        	addSlotToContainer(new Slot(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IInventory handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow, IInventory playerInventory) {
    	
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }
    
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
	    return tile.canInteractWith(playerIn);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		tile.stopGen();
	    ItemStack itemstack = ItemStack.EMPTY;
	    Slot slot = this.inventorySlots.get(index);
	    if (slot != null && slot.getHasStack()) {
	        ItemStack stack = slot.getStack();
	        itemstack = stack.copy();
	        if (index == 0) {
	            if (!this.mergeItemStack(stack, 1, 37, true)) {
	                return ItemStack.EMPTY;
	            }
	            slot.onSlotChange(stack, itemstack);
	        } else {
	            if (!this.mergeItemStack(stack, 0, tile.slotscnt, false)) {
				    return ItemStack.EMPTY;
				}
	        }
	
	        if (stack.isEmpty()) {
	            slot.putStack(ItemStack.EMPTY);
	        } else {
	            slot.onSlotChanged();
	        }
	
	        if (stack.getCount() == itemstack.getCount()) {
	            return ItemStack.EMPTY;
	        }
	
	        slot.onTake(playerIn, stack);
	    }
	
	    return itemstack;
	}

	public boolean isProcessing() {
        return this.tile.isProcessing();
	}

	public int getGenTime(int p) {
        int i1 = tile.getGenTime();
        int i2 = TileMusicGenerator.MaxGenTime;
        return i2 > 0 && i1 > 0 ? i1 * p / i2 : 0;
	}

	public BlockPos getPos() {
		return tile.getPos();
	}

}