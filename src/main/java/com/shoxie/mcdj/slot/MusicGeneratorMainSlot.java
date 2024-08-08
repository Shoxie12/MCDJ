package com.shoxie.mcdj.slot;

import com.shoxie.mcdj.item.BlankDiscItem;
import com.shoxie.mcdj.entity.MusicGeneratorEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MusicGeneratorMainSlot extends SlotItemHandler{
	private final MusicGeneratorEntity entity;

	public MusicGeneratorMainSlot(IItemHandler handler, int index, int xPosition, int yPosition, MusicGeneratorEntity entity) {
		super(handler, index, xPosition, yPosition);
		this.entity = entity;
	}

	@Override
	public boolean mayPlace(ItemStack stack) {
        return stack.getItem() instanceof BlankDiscItem;
    }
	
	@Override
	public void setChanged() {
		entity.stopGen();
		if(this.hasItem()) entity.updatePreviewSlot();
		else entity.clearPreviewSlot();
		entity.sendUpdates();
	    this.container.setChanged();
		
	}
}
