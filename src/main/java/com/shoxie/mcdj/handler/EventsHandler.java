package com.shoxie.mcdj.handler;

import com.shoxie.mcdj.ModItems;

import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class EventsHandler
{
	
	@SubscribeEvent
	public static void onLoot(LootTableLoadEvent e)
	{
		if (
		    LootTableList.CHESTS_ABANDONED_MINESHAFT.equals(e.getName()) ||
		    LootTableList.CHESTS_WOODLAND_MANSION.equals(e.getName()) ||
		    LootTableList.CHESTS_SIMPLE_DUNGEON.equals(e.getName())
		) {
			updateloot(e.getTable().getPool("pool1"),10,0);
			updateloot(e.getTable().getPool("pool2"),10,0);
			updateloot(e.getTable().getPool("pool3"),10,0);
		}
		else if(
		    	LootTableList.ENTITIES_ZOMBIE.equals(e.getName()) ||
		    	LootTableList.ENTITIES_ZOMBIE_VILLAGER.equals(e.getName())
		)
		    updateloot(e.getTable().getPool("pool1"),1,0);  
	}
	
	private static void updateloot(LootPool pool,int w,int q)
	{
	    if (pool != null) 
	    	pool.addEntry(new LootEntryItem(ModItems.BLANK_RECORD, w, q, new LootFunction[0], new LootCondition[0], ModItems.BLANK_RECORD.getRegistryName().toString()));
	}
}