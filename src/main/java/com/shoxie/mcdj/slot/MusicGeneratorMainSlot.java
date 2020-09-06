package com.shoxie.mcdj.slot;

import com.shoxie.mcdj.item.BlankDiscItem;
import com.shoxie.mcdj.tile.MusicGeneratorTile;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MusicGeneratorMainSlot extends SlotItemHandler{
	private MusicGeneratorTile tile;

	public MusicGeneratorMainSlot(IItemHandler handler, int index, int xPosition, int yPosition, MusicGeneratorTile tile) {
		super(handler, index, xPosition, yPosition);
		this.tile = tile;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
	    if(stack.getItem() instanceof BlankDiscItem)
	    	return true;
	    return false;
	}
	
	@Override
	public void onSlotChanged() {
		tile.stopGen();
	    this.inventory.markDirty();
	}
}
