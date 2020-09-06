package com.shoxie.mcdj.container;

import com.shoxie.mcdj.ModContainers;
import com.shoxie.mcdj.slot.MusicGeneratorMainSlot;
import com.shoxie.mcdj.slot.MusicGeneratorPreviewSlot;
import com.shoxie.mcdj.tile.MusicGeneratorTile;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.api.distmarker.Dist;

public class MusicGeneratorContainer extends Container {
	
    private MusicGeneratorTile tile;
	private IItemHandler playerInventory;

	public MusicGeneratorContainer(int windowId, MusicGeneratorTile tile, PlayerEntity player, PlayerInventory pinv) {
        super(ModContainers.CONTAINER_MUSIC_GENERATOR, windowId);
        this.playerInventory = new InvWrapper(pinv);
        this.tile = tile;
        this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
        	addSlot(new MusicGeneratorMainSlot(h, 0, 77, 75, tile));
        	addSlot(new MusicGeneratorPreviewSlot(h, 1, 77, 24));
        	
        });
        layoutPlayerInventorySlots(8, 100);
        }
        
	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
	    return true;
	}
	
	@Override
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
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
	
	public MusicGeneratorTile getTile() {
		return tile;
	}
	
    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int dx) {
        for (int i = 0 ; i < amount ; i++) {
            addSlot(new SlotItemHandler(handler, index, x, y));
            x += dx;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
        for (int j = 0 ; j < verAmount ; j++) {
            index = addSlotRange(handler, index, x, y, horAmount, dx);
            y += dy;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow) {
    	
        addSlotBox(playerInventory, 9, leftCol, topRow, 9, 18, 3, 18);
        topRow += 58;
        addSlotRange(playerInventory, 0, leftCol, topRow, 9, 18);
    }


    @OnlyIn(Dist.CLIENT)
	public boolean isProcessing() {
        return this.tile.isProcessing();
	}

	public int getGenTime(int p) {
        int i1 = tile.getGenTime();
        int i2 = MusicGeneratorTile.MaxGenTime;
        return i2 > 0 && i1 > 0 ? i1 * p / i2 : 0;
	}

	public BlockPos getPos() {
		return tile.getPos();
	}
}