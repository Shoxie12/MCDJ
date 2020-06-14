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
	private static ForgeConfigSpec.ConfigValue<String> mcdjpath;
	private static ForgeConfigSpec.ConfigValue<String> mcdjrootfoldername;
	private static ForgeConfigSpec.ConfigValue<Boolean> sr;
	private static ForgeConfigSpec.ConfigValue<Boolean> vanilaenabled;
    public static ForgeConfigSpec cfg;


    static {
		ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
		builder.push("general");
		headlessmode = builder.comment(
				"",
				"In headless mode MCDJ will only add sounds to the game, but not create music discs.",
				"usefull for mapbased modpacks").define("HeadlessMode", false);
		
		mcdjpath = builder.comment(
				"",
				"Path to mcdj root folder.",
				"examples: 'mods' = /.minecraft/mods/; 'misc/mcdj' = /.minecraft/misc/mcdj",
				"leave blank for choise minecraft root folder.").define("MCDJPath", "");
		
		mcdjrootfoldername = builder.comment(
				"",
				"MCDJ root folder name.").define("MCDJRootFolderName", ".mcdj");
		
		sr = builder.comment(
				"",
				"Generate shotcut to music folder.").define("ShotcutRequied", true);
		
		vanilaenabled = builder.comment(
				"",
				"Should music generator also produce vanila records?").define("VanilaRecordsEnabled", true);
		
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
	
	public static String GetMcdjPath() {
		return mcdjpath.get();
	}
	
	public static String GetMcdjRootFolderName() {
		return mcdjrootfoldername.get();
	}
	
	public static boolean IsShotcutRequied() {
		return sr.get();
	}

	public static boolean isvanilaenabled() {
		return vanilaenabled.get();
	}
    
}