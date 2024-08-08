package com.shoxie.mcdj.menu;

import com.shoxie.mcdj.init.Init;
import com.shoxie.mcdj.slot.MusicGeneratorMainSlot;
import com.shoxie.mcdj.slot.MusicGeneratorPreviewSlot;
import com.shoxie.mcdj.entity.MusicGeneratorEntity;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;

public class MusicGeneratorMenu extends AbstractContainerMenu {
	
	private final MusicGeneratorEntity entity;

    public MusicGeneratorMenu(int windowId, Inventory inv, BlockEntity _entity) {
        super(Init.MUSIC_GENERATOR_MENU.get(), windowId);
		if (_entity instanceof MusicGeneratorEntity e) {
			this.entity = e;
		} else {
			throw new IllegalStateException("Block entity (%s) is not MusicGeneratorEntity!"
					.formatted(_entity.getClass().getCanonicalName()));
		}
		entity.getInventoryOptional().ifPresent(inventory -> {
        	addSlot(new MusicGeneratorMainSlot(inventory, 0, 77, 75, entity));
        	addSlot(new MusicGeneratorPreviewSlot(inventory, 1, 77, 24));
		});

		layoutPlayerInventorySlots(inv, 8, 100);
        }

	public MusicGeneratorMenu(int windowId, Inventory inv, FriendlyByteBuf buf) {
		this(windowId, inv, inv.player.level().getBlockEntity(buf.readBlockPos()));
	}

	@Override
	public boolean stillValid(Player playerIn) {
	    return true;
	}
	
	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
	   ItemStack itemstack = ItemStack.EMPTY;
	    Slot slot = this.slots.get(index);
	    if (slot != null && slot.hasItem()) {
	        ItemStack stack = slot.getItem();
	        itemstack = stack.copy();
	        if (index == 0) {
	            if (!this.moveItemStackTo(stack, 1, 37, true)) {
	                return ItemStack.EMPTY;
	            }
	            slot.onQuickCraft(stack, itemstack);
	        } else {
	            if (!this.moveItemStackTo(stack, 0, entity.slotscnt, false)) {
				    return ItemStack.EMPTY;
				}
	        }
	
	        if (stack.isEmpty()) {
	            slot.set(ItemStack.EMPTY);
	        } else {
	            slot.setChanged();
	        }
	
	        if (stack.getCount() == itemstack.getCount()) {
	            return ItemStack.EMPTY;
	        }
	
	        slot.onTake(playerIn, stack);
	    }
	
	    return itemstack;
		
		}
	
	public MusicGeneratorEntity getEntity() {
		return entity;
	}

    
	public boolean isProcessing() {
        return this.entity.isProcessing();
	}

	public int getGenTime(int p) {
        int i1 = entity.getGenTime();
        int i2 = MusicGeneratorEntity.MaxGenTime;
        return i2 > 0 && i1 > 0 ? i1 * p / i2 : 0;
	}

	public BlockPos getPos() {
		return entity.getBlockPos();
	}

	private int addSlotRange(Inventory handler, int index, int x, int y, int amount, int dx) {
		for (int i = 0 ; i < amount ; i++) {
			addSlot(new Slot(handler, index, x, y));
			x += dx;
			index++;
		}
		return index;
	}

	private void addSlotBox(Inventory inv, int index, int x, int y, int horAmount, int dx, int verAmount, int dy) {
		for (int j = 0 ; j < verAmount ; j++) {
			index = addSlotRange(inv, index, x, y, horAmount, dx);
			y += dy;
		}
	}

	private void layoutPlayerInventorySlots(Inventory inv, int leftCol, int topRow) {

		addSlotBox(inv, 9, leftCol, topRow, 9, 18, 3, 18);
		topRow += 58;
		addSlotRange(inv, 0, leftCol, topRow, 9, 18);
	}
}