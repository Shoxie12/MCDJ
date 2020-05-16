package com.shoxie.mcdj.handler;

import com.shoxie.mcdj.Blocks;
import com.shoxie.mcdj.Items;
import com.shoxie.mcdj.mcdj;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class RegisterHandler {
	@SubscribeEvent
	public static void onBlockRegister(Register<Block> e) {
		e.getRegistry().register(Blocks.MUSIC_GENERATOR);
	}
	
	@SubscribeEvent
	public static void onItemRegister(Register<Item> e) {
		e.getRegistry().register(Items.OBSIDIAN_PLATE);
		e.getRegistry().register(Items.BLANK_RECORD);
		e.getRegistry().register(new ItemBlock(Blocks.MUSIC_GENERATOR).setRegistryName(Blocks.MUSIC_GENERATOR.getRegistryName()));

		if(mcdj.musicloaded) 
			e.getRegistry().registerAll(Items.RECORDS);
	}
	

}
