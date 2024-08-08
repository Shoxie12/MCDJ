package com.shoxie.mcdj.slot;

import javax.annotation.Nonnull;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

public class MusicGeneratorPreviewSlot extends SlotItemHandler{
	public MusicGeneratorPreviewSlot(IItemHandler handler, int index, int xPosition, int yPosition) {
		super(handler, index, xPosition, yPosition);
	}
	
    @Override
    public boolean mayPickup(Player playerIn) {
    	return false;
    }
    
	@Override
	public boolean mayPlace(@Nonnull ItemStack stack) {
	    return false;
	}
}
