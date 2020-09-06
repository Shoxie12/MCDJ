package com.shoxie.mcdj.slot;

import com.shoxie.mcdj.item.ItemBlankRecord;
import com.shoxie.mcdj.tile.TileMusicGenerator;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MusicGeneratorMainSlot extends SlotItemHandler{
	private TileMusicGenerator tile;

	public MusicGeneratorMainSlot(IItemHandler handler, int index, int xPosition, int yPosition, TileMusicGenerator tile) {
		super(handler, index, xPosition, yPosition);
		this.tile = tile;
	}

	@Override
	public boolean isItemValid(ItemStack stack) {
	    if(stack.getItem() instanceof ItemBlankRecord)
	    	return true;
	    return false;
	}
	
	@Override
	public void onSlotChanged() {
		tile.stopGen();
	    this.inventory.markDirty();
	}
}
