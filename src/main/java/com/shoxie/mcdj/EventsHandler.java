package com.shoxie.mcdj;

import net.minecraft.util.ResourceLocation;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.TableLootEntry;
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
			updateChests(e.getTable(),"blankdisc","simple_dungeon");
		
		else if(
				e.getName().equals(new ResourceLocation("minecraft", "entities/zombie")) || 
				e.getName().equals(new ResourceLocation("minecraft", "entities/zombie_villager"))
				)
			updateEntityLoot(e.getTable(),"zombie");
	}
	
	private static void updateEntityLoot(LootTable table, String tablename) {
		table.addPool(LootPool.builder().name("mcdj").
				addEntry(TableLootEntry.
						builder(new ResourceLocation(mcdj.MODID, tablename))).build());
	}
    
	private static void updateChests(LootTable table, String pool, String tablename) {
		table.addPool(LootPool.builder().
				addEntry(TableLootEntry.
						builder(new ResourceLocation(mcdj.MODID, tablename)).weight(1)).
							name(pool).build());
	}
}