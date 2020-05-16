package com.shoxie.mcdj.handler;

import com.shoxie.mcdj.Blocks;
import com.shoxie.mcdj.Items;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.item.ItemHQRecord;

import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class RenderHandler {
	@SubscribeEvent
	public static void onModelLoad(ModelRegistryEvent e) {
		mcdj.proxy.renderItem(Item.getItemFromBlock(Blocks.MUSIC_GENERATOR), 0, Blocks.MUSIC_GENERATOR.getRegistryName());
		mcdj.proxy.renderItem(Items.OBSIDIAN_PLATE, 0, Items.OBSIDIAN_PLATE.getRegistryName());
		mcdj.proxy.renderItem(Items.BLANK_RECORD, 0, Items.BLANK_RECORD.getRegistryName());
		
		if(mcdj.musicloaded) 
			for (ItemHQRecord i : Items.RECORDS) 
				mcdj.proxy.renderItem(i, 0, i.getRegistryName());
		
	}
}
