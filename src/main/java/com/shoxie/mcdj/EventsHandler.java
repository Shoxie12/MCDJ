package com.shoxie.mcdj;

import com.shoxie.mcdj.gui.MusicGeneratorScreen;
import com.shoxie.mcdj.init.Init;
import com.shoxie.mcdj.networking.Networking;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.nio.file.Paths;

import static com.shoxie.mcdj.Lib.getPlaylistRootPath;
import static com.shoxie.mcdj.mcdj.MODID;
import static com.shoxie.mcdj.mcdj.proxy;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventsHandler {

	//Screens
	@SubscribeEvent
	public static void ScreenInit(FMLClientSetupEvent event) {
		if (Config.isNoDiscsModEnabled() || !mcdj.isModLoaded()) return;
		MenuScreens.register(Init.MUSIC_GENERATOR_MENU.get(), MusicGeneratorScreen::new);
		proxy.ScreenInit();
	}

	@SubscribeEvent
	public static void commonSetup(FMLCommonSetupEvent event) {
		if (Config.isNoDiscsModEnabled() || !mcdj.isModLoaded()) return;
		event.enqueueWork(Networking::registerMessages);
	}

	@SubscribeEvent
	public static void AddPackFinders(AddPackFindersEvent event) {
		if (Config.isNoDiscsModEnabled() || !mcdj.isModLoaded()) return;
		if(event.getPackType() == PackType.SERVER_DATA){
			{
				var pack = Pack.readMetaAndCreate("builtin/mcdj_tags", Component.literal("MCDJ"), false,
                        s -> new PathPackResources(s,Paths.get(getPlaylistRootPath()),true),
                        PackType.SERVER_DATA, Pack.Position.TOP, PackSource.BUILT_IN);
				event.addRepositorySource((packConsumer) -> packConsumer.accept(pack));
			}
		}
	}

	@SubscribeEvent
	public static void buildContents(BuildCreativeModeTabContentsEvent event) {
		if (Config.isNoDiscsModEnabled() || !mcdj.isModLoaded()) return;
		if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
			event.accept(Init.MUSIC_GENERATOR_ITEM);
			event.accept(Init.SEALED_RECORD_ITEM);
			event.accept(Init.BLANK_RECORD);
			event.accept(Init.OBSIDIAN_PLATE);

			for (var iRecord : Init.CUSTOM_RECORD_ITEMS) {
                if(iRecord.isPresent())
				    event.accept(iRecord);
			}
		}
	}

	@Mod.EventBusSubscriber(modid = MODID)
	public static class LootEventHandler {
		@SubscribeEvent
		public static void onLoot(final LootTableLoadEvent e) {
			if (Config.isNoDiscsModEnabled() || !mcdj.isModLoaded()) return;
			if (
					Config.isDungeonSpawnEnabled() &&
							(e.getName().equals(new ResourceLocation("minecraft", "chests/abandoned_mineshaft")) ||
									e.getName().equals(new ResourceLocation("minecraft", "chests/simple_dungeon")) ||
									e.getName().equals(new ResourceLocation("minecraft", "chests/woodland_mansion"))
							))
				updateChests(e.getTable(), "sealed_record", "simple_dungeon");

			else if (
					Config.isZombieDropEnabled() &&
							(e.getName().equals(new ResourceLocation("minecraft", "entities/zombie")) ||
									e.getName().equals(new ResourceLocation("minecraft", "entities/zombie_villager"))
							))
				updateEntityLoot(e.getTable(), "zombie");
		}

		private static void updateEntityLoot(LootTable table, String tablename) {
			table.addPool(LootPool.lootPool().name("mcdj").
					add(LootTableReference.
							lootTableReference(new ResourceLocation(MODID, tablename))).build());
		}

		private static void updateChests(LootTable table, String pool, String tablename) {
			table.addPool(LootPool.lootPool().name("mcdj").
					add(LootTableReference.
							lootTableReference(new ResourceLocation(MODID, tablename)).setWeight(1)).
					name(pool).build());
		}
	}
}

