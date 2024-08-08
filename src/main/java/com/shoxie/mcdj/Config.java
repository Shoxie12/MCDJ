package com.shoxie.mcdj;

import java.nio.file.Path;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent.Loading;
import net.minecraftforge.fml.event.config.ModConfigEvent.Reloading;

@Mod.EventBusSubscriber(modid = mcdj.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	private static ForgeConfigSpec.ConfigValue<Boolean> nodiscsmode;
	private static ForgeConfigSpec.ConfigValue<Boolean> enablemg;
	private static ForgeConfigSpec.ConfigValue<Boolean> creeperdrop;
	private static int _convactivethreads;
	private static ForgeConfigSpec.ConfigValue<Integer> convactivethreads;
	private static ForgeConfigSpec.ConfigValue<String> mcdjpath;
	private static ForgeConfigSpec.ConfigValue<Boolean> spawnindungeon;
	private static ForgeConfigSpec.ConfigValue<Boolean> zombiedrop;
	private static ForgeConfigSpec.ConfigValue<Boolean> showtracknumber;
    public static ForgeConfigSpec cfg;
	  
    static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("MCDJ");
		enablemg = builder.comment(
				Lib.getTranslated("gui.mcdj.configscreen.title"),
				Lib.getTranslated("options.mcdj.enablemg"),
				Lib.getTranslated("options.mcdj.enablemg.tooltip")).define("EnableMG", true);

		spawnindungeon = builder.comment(
				"",
				Lib.getTranslated("options.mcdj.dungeonspawn"),
				Lib.getTranslated("options.mcdj.dungeonspawn.tooltip")).define("SpawnInDungeon", true);

		zombiedrop = builder.comment(
				"",
				Lib.getTranslated("options.mcdj.zombiedrop"),
				Lib.getTranslated("options.mcdj.zombiedrop.tooltip")).define("DropFromZombie", true);

		showtracknumber = builder.comment(
				"",
				Lib.getTranslated("options.mcdj.showtracknumbers"),
				Lib.getTranslated("options.mcdj.showtracknumbers.tooltip")).define("ShowTrackNumber", false);

		nodiscsmode = builder.comment(
				"",
				Lib.getTranslated("options.mcdj.nodiscsmode"),
				Lib.getTranslated("options.mcdj.nodiscsmode.tooltip")).define("NoDiscsMode", false);

		creeperdrop = builder.comment(
				"",
				Lib.getTranslated("options.mcdj.creeperdrop"),
				Lib.getTranslated("options.mcdj.creeperdrop.tooltip")).define("DropFromCreeper", true);

		convactivethreads = builder.comment(
				"",
				Lib.getTranslated("options.mcdj.convmaxthreads"),
				Lib.getTranslated("options.mcdj.convmaxthreads.tooltip")).define("FFmpegMaxThreads", 10);

		mcdjpath = builder.comment(
				"",
				Lib.getTranslated("options.mcdj.mcdjpath.tooltip")).define("MCDJPath", "");

		builder.pop();
		cfg = builder.build();
    }

    public static void loadConfig(ForgeConfigSpec spec, Path path) {

        final CommentedFileConfig configData = CommentedFileConfig.builder(path)
                .sync()
                .autosave()
                .writingMode(WritingMode.REPLACE)
                .build();

        configData.load();
        spec.setConfig(configData);
    }

    @SubscribeEvent
    public static void onReload(final Reloading configEvent) {

    }
    
    @SubscribeEvent
    public static void onLoad(final Loading configEvent) {
		_convactivethreads = convactivethreads.get();
    }

	public static boolean isMGenabled() {
		return enablemg.get();
	}

	public static void setMGenabled(boolean val) {
		enablemg.set(val);
	}

	public static boolean isDungeonSpawnEnabled() {
		return spawnindungeon.get();
	}

    public static void setDungeonSpawn(boolean val) {
		spawnindungeon.set(val);
    }

    public static boolean isZombieDropEnabled() {
        return zombiedrop.get();
    }

    public static void setZombieDrop(boolean val) {
		zombiedrop.set(val);
    }

	public static boolean isTrackNumbersEnabled() {
		return showtracknumber.get();
	}

    public static void setTrackNumbers(boolean val) {
        showtracknumber.set(val);
    }

	public static boolean isNoDiscsModEnabled() {
		return nodiscsmode.get();
	}

	public static void setNoDiscsMode(boolean val) {
		nodiscsmode.set(val);
	}

	public static int getFFMpegMaxThreads() {
		return convactivethreads.get();
	}

	public static void setFFMpegMaxThreads(int val) {
		_convactivethreads = val;
		convactivethreads.set(_convactivethreads);
	}

	public static String getMcdjPath() {
		return mcdjpath.get();
	}
	public static void setMcdjPath(String val) {
		mcdjpath.set(val);
	}

	public static boolean isCreeperDropEnabled() {
		return creeperdrop.get();
	}

	public static void setCreeperDrop(boolean val) {
		creeperdrop.set(val);
	}
}