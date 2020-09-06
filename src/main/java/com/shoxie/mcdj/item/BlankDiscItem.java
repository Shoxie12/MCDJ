package com.shoxie.mcdj.item;

import com.shoxie.mcdj.ModSoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.SoundEvent;

public class BlankDiscItem extends MusicDiscItem {
	
	private static String name = "blankrecord";
	protected static SoundEvent snd = ModSoundEvents.BLANK_RECORD;
	public BlankDiscItem() {
		super(0, snd,new Item.Properties().group(ItemGroup.MISC).maxStackSize(1));
		setRegistryName(name);
	}
}