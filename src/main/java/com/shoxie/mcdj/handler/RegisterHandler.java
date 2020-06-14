package com.shoxie.mcdj.handler;

import com.shoxie.mcdj.Config;
import com.shoxie.mcdj.ModBlocks;
import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.ModSoundEvents;
import com.shoxie.mcdj.mcdj;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class RegisterHandler {
	@SubscribeEvent
	public static void onSoundRegister(Register<SoundEvent> e) {
		e.getRegistry().register(ModSoundEvents.BLANK_RECORD);
		if(mcdj.musicloaded) e.getRegistry().registerAll(ModSoundEvents.RECORDS);
	}
	
	@SubscribeEvent
	public static void onBlockRegister(Register<Block> e) {
		e.getRegistry().register(ModBlocks.MUSIC_GENERATOR);
	}
	
	@SubscribeEvent
	public static void onItemRegister(Register<Item> e) {
		e.getRegistry().register(ModItems.OBSIDIAN_PLATE);
		e.getRegistry().register(ModItems.BLANK_RECORD);
		e.getRegistry().register(new ItemBlock(ModBlocks.MUSIC_GENERATOR).setRegistryName(ModBlocks.MUSIC_GENERATOR.getRegistryName()));

		if(mcdj.musicloaded && !Config.headlessmode) 
			e.getRegistry().registerAll(ModItems.RECORDS);
	}
	

}
