package com.shoxie.mcdj.handler;

import com.shoxie.mcdj.ModBlocks;
import com.shoxie.mcdj.ModItems;
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
		mcdj.proxy.renderItem(Item.getItemFromBlock(ModBlocks.MUSIC_GENERATOR), 0, ModBlocks.MUSIC_GENERATOR.getRegistryName());
		mcdj.proxy.renderItem(ModItems.OBSIDIAN_PLATE, 0, ModItems.OBSIDIAN_PLATE.getRegistryName());
		mcdj.proxy.renderItem(ModItems.BLANK_RECORD, 0, ModItems.BLANK_RECORD.getRegistryName());
		
		if(mcdj.musicloaded) 
			for (ItemHQRecord i : ModItems.RECORDS) 
				mcdj.proxy.renderItem(i, 0, i.getRegistryName());
		
	}
}
