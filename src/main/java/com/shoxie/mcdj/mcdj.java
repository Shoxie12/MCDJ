package com.shoxie.mcdj;

import com.shoxie.mcdj.init.Init;
import com.shoxie.mcdj.misc.discRegData;
import com.shoxie.mcdj.proxy.ClientProxy;
import com.shoxie.mcdj.proxy.IProxy;
import com.shoxie.mcdj.proxy.ServerProxy;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;

import java.util.ArrayList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.shoxie.mcdj.misc.MusicFile;

@Mod(mcdj.MODID)
public class mcdj
{
    public static final String MODID = "mcdj";
    public static final String NAME = "MCDJ";
    public static final String VERSION = "3.0.1";
	public static final String DEFAULT_BLANK_RECORD_SOUND = "br";
	public static final String DEFTEXTURE = "defaultrecord";
	public static final int DEFAULT_SONG_DURATION = 300;
	public static ArrayList<MusicFile> currentPlaylist = new ArrayList<>();
	public static ArrayList<discRegData> RECORDS_DATA;
    public static Logger logger = LogManager.getLogger(MODID);
	public static SoundInstance is;
	public static final IProxy proxy = DistExecutor.unsafeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
	protected static final Map<String, String> PreInitLangMap = proxy.getPreInitModTranslationsMap();
    public static boolean musicloaded = false;
	public static boolean needrestart = false;
	public static boolean updateinprogress = false;
	public static int activeThreads = 0;
    public static boolean ffmpegfound = false;
	public enum SupportedFormats{
		mp3,
		ogg
	}

    public mcdj() {
        MinecraftForge.EVENT_BUS.register(this);   
        ModLoadingContext.get().registerConfig(Type.COMMON, Config.cfg);
        Config.loadConfig(Config.cfg, FMLPaths.CONFIGDIR.get().resolve("mcdj-common.toml"));
		RECORDS_DATA = Lib.MusicScan();
		final var bus = FMLJavaModLoadingContext.get().getModEventBus();
		Init.SOUND_EVENTS.register(bus);
		if(!Config.isNoDiscsModEnabled()) {
			Init.ITEMS.register(bus);
			Init.BLOCKS.register(bus);
			Init.BLOCK_ENTITIES.register(bus);
			Init.MENU_TYPES.register(bus);
		}
		proxy.checkFFmpeg();
    }
}
