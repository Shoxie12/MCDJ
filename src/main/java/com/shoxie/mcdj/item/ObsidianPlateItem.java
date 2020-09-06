package com.shoxie.mcdj.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;

public class ObsidianPlateItem extends Item{
	
	private static String name = "obsidianplate";
	public ObsidianPlateItem() {
		super(new Item.Properties().group(ItemGroup.MISC));
		setRegistryName(name);
	}
}