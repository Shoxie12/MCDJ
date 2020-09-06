package com.shoxie.mcdj.slot;

import javax.annotation.Nonnull;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MusicGeneratorPreviewSlot extends SlotItemHandler{
	public MusicGeneratorPreviewSlot(IItemHandler handler, int index, int xPosition, int yPosition) {
		super(handler, index, xPosition, yPosition);
	}
	
    @Override
    public boolean canTakeStack(PlayerEntity playerIn) {
    	return false;
    }
    
	@Override
	public boolean isItemValid(@Nonnull ItemStack stack) {
	    return false;
	}
}
