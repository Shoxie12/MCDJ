package com.shoxie.mcdj;

import net.minecraftforge.common.config.Configuration;

public class Config {

	public static boolean headlessmode = false;
	public static boolean legacymg = false;
	public static boolean shortcut = true;
	public static boolean hires_texture = false;
	public static boolean albumarts = false;
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
    	shortcut = cfg.getBoolean("Shortcut", "general", shortcut, "Let MCDJ create shortcut called \"Music\" to streaming folder in .minecraft");
    	legacymg = cfg.getBoolean("LegacyMusicGenerator", "general", legacymg, "Returns an old Music Generator, that give you the random music disc \nwhen you use the blank disc on it.");
    	hires_texture = cfg.getBoolean("HiresDiscTextures", "general", hires_texture, "Enables high resolution disc texture (recommended for use with album arts)");
    	albumarts = cfg.getBoolean("AlbumArts", "general", albumarts, "Draws album art on top of the disc if possible");
    	mcdjpath = cfg.getString("MCDJPath", "general", mcdjpath, "Path to mcdj root folder. \n examples: 'mods' = /.minecraft/mods/; 'misc/mcdj' = /.minecraft/misc/mcdj \n leave blank for choise .minecraft folder.");
    	mcdjrootfoldername = cfg.getString("MCDJRootFolderName", "general", mcdjrootfoldername, "MCDJ will create its root folder with that name.");
    }
}