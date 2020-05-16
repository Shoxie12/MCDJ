package com.shoxie.mcdj.item;

import com.shoxie.mcdj.mcdj;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemRecord;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ItemBlankRecord extends ItemRecord {
	
	private static String name = "blankrecord";
	protected static SoundEvent snd = new SoundEvent(new ResourceLocation(mcdj.MODID,"br")).setRegistryName(mcdj.MODID,"br");
	public ItemBlankRecord() {
		super(name, snd);
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(CreativeTabs.MISC);
	}
}