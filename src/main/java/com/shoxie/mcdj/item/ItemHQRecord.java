package com.shoxie.mcdj.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.SoundEvent;

public class ItemHQRecord extends MusicDiscItem {
	public String name;
	public ItemHQRecord(String _name, SoundEvent snd) {
		super(0, snd,new Item.Properties().group(ItemGroup.MISC).maxStackSize(1));
		name = "record_"+_name;
		setRegistryName(name);
		
		
	}
}