package com.shoxie.mcdj;

import net.minecraftforge.common.config.Configuration;

public class Config {

	public static boolean headlessmode = false;
	public static boolean sr = true;
	public static boolean vanilaenabled = true;
    public static String mcdjpath = "";
    public static String mcdjrootfoldername = ".mcdj";

    public static void readConfig() {
        Configuration cfg = mcdj.config;
        try {
            cfg.load();
            loadConfig(cfg);
        } catch (Exception e) {
            mcdj.logger.error("MCDJ Can't load config file :(", e);
        }
    }

    private static void loadConfig(Configuration cfg) {
    	headlessmode = cfg.getBoolean("HeadlessMode", "general", headlessmode, "In headless mode MCDJ will only add sounds to the game, but not create music discs.");
    	sr = cfg.getBoolean("IsShotcutRequied", "general", sr, "Allow MCDJ to create shotcut for music folder");
    	vanilaenabled = cfg.getBoolean("VanilaEnabled", "general", vanilaenabled, "Should music generator also produce vanila records?");
    	mcdjpath = cfg.getString("MCDJPath", "general", mcdjpath, "Path to mcdj root folder. \n examples: 'mods' = /.minecraft/mods/; 'misc/mcdj' = /.minecraft/misc/mcdj \n leave blank for choise minecraft root folder.");
    	mcdjrootfoldername = cfg.getString("MCDJRootFolderName", "general", mcdjrootfoldername, "Sets MCDJ root folder name.");
    }
}