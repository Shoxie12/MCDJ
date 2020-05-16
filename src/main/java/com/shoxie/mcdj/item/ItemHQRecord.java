package com.shoxie.mcdj.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.SoundEvent;

public class ItemHQRecord extends ItemRecord {
	public ItemHQRecord(String name, SoundEvent soundIn) {
		super(name, soundIn);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(CreativeTabs.MISC);
	}
}