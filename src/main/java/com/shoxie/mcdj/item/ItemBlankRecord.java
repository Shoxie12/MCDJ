package com.shoxie.mcdj.item;

import com.shoxie.mcdj.ModSoundEvents;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.SoundEvent;

public class ItemBlankRecord extends ItemRecord {
	
	private static String name = "blankrecord";
	protected static SoundEvent snd = ModSoundEvents.BLANK_RECORD;
	public ItemBlankRecord() {
		super(name, snd);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(CreativeTabs.MISC);
	}
}