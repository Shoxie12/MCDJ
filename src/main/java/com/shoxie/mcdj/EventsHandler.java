package com.shoxie.mcdj;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = mcdj.MODID)
public class EventsHandler {

	@SubscribeEvent
	public static void onLoot(LootTableLoadEvent e)
	{
		if(
			e.getName().equals(new ResourceLocation("minecraft", "chests/abandoned_mineshaft")) ||
			e.getName().equals(new ResourceLocation("minecraft", "chests/simple_dungeon")) ||
			e.getName().equals(new ResourceLocation("minecraft", "chests/woodland_mansion"))
			) 
			updateloot(e.getTable(),"simple_dungeon");
		
		else if(
				e.getName().equals(new ResourceLocation("minecraft", "entities/zombie")) || 
				e.getName().equals(new ResourceLocation("minecraft", "entities/zombie_villager"))
				)
				updateloot(e.getTable(),"zombie");
	}
	
	
	private static void updateloot(LootTable table, String tablename) {
		table.addPool(LootPool.builder().
				addEntry(TableLootEntry.
						builder(new ResourceLocation(mcdj.MODID, tablename))).build());
	}
}