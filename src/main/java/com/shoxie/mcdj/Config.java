package com.shoxie.mcdj;

import java.nio.file.Path;

import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Loading;
import net.minecraftforge.fml.config.ModConfig.Reloading;

@Mod.EventBusSubscriber(modid = mcdj.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class Config {
	private static ForgeConfigSpec.ConfigValue<Boolean> headlessmode;
	private static ForgeConfigSpec.ConfigValue<Boolean> legacymg;
	private static ForgeConfigSpec.ConfigValue<String> mcdjpath;
	private static ForgeConfigSpec.ConfigValue<String> mcdjrootfoldername;
	private static ForgeConfigSpec.ConfigValue<Boolean> shortcut;
	private static ForgeConfigSpec.ConfigValue<Boolean> hires_texture;
	private static ForgeConfigSpec.ConfigValue<Boolean> albumarts;
    public static ForgeConfigSpec cfg;


    static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("general");		
		mcdjrootfoldername = builder.comment(
				"",
				"MCDJ will create its root folder with that name.").define("RootFolderName", ".mcdj");
		
		shortcut = builder.comment(
				"",
				"Let MCDJ create shortcut called \"Music\" to streaming folder in .minecraft").define("Shortcut", true);
		
		albumarts = builder.comment(
				"",
				"Draws album art on top of the disc if possible").define("AlbumArt", false);
		
		hires_texture = builder.comment(
				"",
				"Enables high resolution disc texture (recommended for use with album arts)").define("HiresDiscTextures", false);		
		
		headlessmode = builder.comment(
				"",
				"In headless mode MCDJ will only add sounds to the game, but not create music discs.").define("HeadlessMode", false);
		
		legacymg = builder.comment(
				"",
				"Returns an old Music Generator, that give you the random music disc ",
				"when you use the blank disc on it.").define("LegacyMusicGenerator", false);
		
		mcdjpath = builder.comment(
				"",
				"Path to mcdj root folder.",
				"examples: 'mods' = /.minecraft/mods/; 'misc/mcdj' = /.minecraft/misc/mcdj",
				"leave blank for choise .minecraft folder.").define("MCDJPath", "");
		
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

    }
    
	public static boolean isHeadlessMode() {
		return headlessmode.get();
	}
	
	public static boolean isLegacyMode() {
		return legacymg.get();
	}
	
	public static String GetMcdjPath() {
		return mcdjpath.get();
	}
	
	public static String GetMcdjRootFolderName() {
		return mcdjrootfoldername.get();
	}
	
	public static boolean IsShortcutRequied() {
		return shortcut.get();
	}
	
	public static boolean IsHiResTexture() {
		return hires_texture.get();
	}

	public static boolean AlbumArts() {
		return albumarts.get();
	}
    
}