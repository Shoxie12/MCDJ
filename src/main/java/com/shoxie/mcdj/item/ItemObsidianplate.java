package com.shoxie.mcdj.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemObsidianplate extends Item{

	private static String name = "obsidianplate";
	public ItemObsidianplate() {
		super();
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(CreativeTabs.MISC);
	}
}