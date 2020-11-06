package com.shoxie.mcdj;

import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gagravarr.vorbis.VorbisFile;

import com.shoxie.mcdj.container.MusicGeneratorContainer;
import com.shoxie.mcdj.item.CustomDiscItem;
import com.shoxie.mcdj.networking.Networking;
import com.shoxie.mcdj.proxy.ServerProxy;
import com.shoxie.mcdj.tile.MusicGeneratorTile;
import com.shoxie.mcdj.proxy.ClientProxy;
import com.shoxie.mcdj.proxy.IProxy;

@Mod("mcdj")
public class mcdj
{
    public static final String MODID = "mcdj";
    public static final String NAME = "MCDJ";
    public static final String VERSION = "2.1";
    
    public static Logger logger = LogManager.getLogger(MODID);
  
    public static boolean musicloaded;
    private boolean playlistchanged;

    public static final IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	public static final String DEFAULT_BLANK_RECORD_SOUND = "br";
	public static final String DEFTEXTURE = "defaultrecord";
	public static final String DEFTEXTURE16 = "defaultrecord16";
	
    public mcdj() {
    	
        MinecraftForge.EVENT_BUS.register(this);   
        ModLoadingContext.get().registerConfig(Type.COMMON, Config.cfg);
        
        Config.loadConfig(Config.cfg, FMLPaths.CONFIGDIR.get().resolve("mcdj-common.toml"));
        String mpath = Config.GetMcdjPath();
        String fname = Config.GetMcdjRootFolderName();
        if(!mpath.equals("")) mpath = mpath + "/";
        if(fname.equals("")) fname = ".mcdj";
        
        String rootpath = mpath+fname+"/";
        String respath = rootpath+"assets/"+MODID+"/";
        
		String musicpath = respath+"sounds/streaming/";
		logger.debug("Music folder is located at "+musicpath);
		MusicScan(rootpath, respath, musicpath);
    }

    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {    	
    	
    	@SubscribeEvent
    	public static void ScreenInit(final FMLClientSetupEvent event) {
    		
    	}
    	
    	@SubscribeEvent
        public static void NetworkingInit(final FMLCommonSetupEvent event) {
        	proxy.ScreenInit();
            Networking.registerMessages();
        }
    	
    	@SubscribeEvent
    	public static void onSoundRegister(final RegistryEvent.Register<SoundEvent> e) {
    		e.getRegistry().register(ModSoundEvents.BLANK_RECORD);
    		if(musicloaded) e.getRegistry().registerAll(ModSoundEvents.RECORDS);
    	}
    	
        @SubscribeEvent
        public static void onBlockRegister(final RegistryEvent.Register<Block> e) {
        	e.getRegistry().register(ModBlocks.MUSIC_GENERATOR);
        }

        @SubscribeEvent
        public static void onItemRegister(final RegistryEvent.Register<Item> e) {
        	
    		e.getRegistry().register(ModItems.OBSIDIAN_PLATE);
    		e.getRegistry().register(ModItems.BLANK_RECORD);
    		e.getRegistry().register(new BlockItem(ModBlocks.MUSIC_GENERATOR, new Item.Properties().group(ItemGroup.DECORATIONS)).setRegistryName(ModBlocks.MUSIC_GENERATOR.getRegistryName()));

    		if(musicloaded && !Config.isHeadlessMode()) 
    			e.getRegistry().registerAll(ModItems.RECORDS);
        }
        
        @SubscribeEvent
        public static void onTileEntityRegistry(final RegistryEvent.Register<TileEntityType<?>> event) {
        	event.getRegistry().register(TileEntityType.Builder.create(MusicGeneratorTile::new, ModBlocks.MUSIC_GENERATOR).build(null).setRegistryName("musicgenerator"));
        }
        
        @SubscribeEvent
        public static void onContainerRegistry(final RegistryEvent.Register<ContainerType<?>> event) {
            event.getRegistry().register(IForgeContainerType.create((windowId, inv, data) -> {
                BlockPos pos = data.readBlockPos();
                MusicGeneratorTile tile = (MusicGeneratorTile) proxy.getClientWorld().getTileEntity(pos);
                return new MusicGeneratorContainer(windowId, tile, inv.player, inv);
            }).setRegistryName("musicgenerator"));
        
        }
    }
    
	private void MusicScan(String rootpath, String respath, String musicpath) {
		File rp = Paths.get(rootpath).toFile();
    	boolean rootexist = rp.exists();
    	boolean rootcreated = false;
    	if(!CheckResourcePack(rootpath, respath)) {
    		if(rootexist) logger.error("MCDJ Root directory is damaged! Repairing...");
    		GenerateResourcePack(rootpath, respath, rootexist);
    		rootcreated = true;
    		if(Config.IsShortcutRequied()) Lib.createSymLink(musicpath);
    	}
    	else
    		logger.info("Scanning music folder...");
    	proxy.rpinit(rp);
    	File[] MusicFiles = Paths.get(musicpath).toFile().listFiles();
    	
    	String type = "";
    	String soundsj = "";
    	String lang = "";
    	ArrayList<SoundEvent> sounds = new ArrayList<SoundEvent>();
	    ArrayList<CustomDiscItem> records = new ArrayList<CustomDiscItem>();
		
		//Scaning music folder
		if(MusicFiles.length == 0) {
			if(rootexist) logger.info("Your playlist is empty. Try to add some music!");
			else if(rootcreated) logger.info("MCDJ installed successfully! Now you can close Minecraft add some music.");
			else logger.error("Error while installing MCDJ :(");
			musicloaded=false;
			
			//Cleaning up textures and json files, if we found old hash file
			File hf = Paths.get(rootpath+"mc.hash").toFile();
			if(hf.exists()) {
				Lib.cleanAll(respath);
				hf.delete();
			}
		}
		else {
			
			String lasthash = Lib.readFile(Paths.get(rootpath+"mc.hash").toFile());
			playlistchanged = true;
			
			//Checking for changes in music directory
			if(lasthash!=null)
				if(lasthash.compareTo(Lib.HashMusicDir(MusicFiles))==0) 
					playlistchanged=false;
			
			if(playlistchanged) {
				logger.info("Playlist was changed");
			    Lib.cleanAll(respath);
			}
			else logger.info("No playlist changes detected!");
			
			//Parsing streaming directory
			for (int i = 0; i < MusicFiles.length; i++) {
			  if (MusicFiles[i].isFile()) {
				  
				String curfile = MusicFiles[i].getName();
				logger.debug("Processing file: "+curfile);
				type = Lib.getfiletype(Paths.get(musicpath+curfile));
				if(Lib.ismusic(type)) {
					String ext = "."+FilenameUtils.getExtension(curfile);
					curfile = curfile.substring(0,curfile.length() - (ext.length()));
					//Checking file name for illegal symbols
					String regextocheck = "[^a-z0-9- _]";
					Matcher matcher = Pattern.compile(regextocheck).matcher(curfile);
					if(playlistchanged) { 
						if(matcher.find() || curfile.length() <= 5) {
							logger.info("Renaming file: "+curfile);
							String newcurfile = curfile.toLowerCase();
							
							if(newcurfile.replaceAll("[^a-z0-9]", "").length() > 5) {
								newcurfile = newcurfile.replaceAll(" ", "_");
								newcurfile = newcurfile.replaceAll(regextocheck, "");
							}
							else {
								newcurfile = Lib.genrandomword(8);
								logger.warn(curfile+" has too short name or many illegal symbols and will be saved as \""+newcurfile+"\"! Make sure it has vorbis tags or rename it manually later!");
							}
							File file = new File(musicpath+curfile+ext);
							ext = ".ogg";
							File file2 = new File(musicpath+newcurfile+".ogg");
							if (!file.renameTo(file2)) {
								logger.error(" Error while renaming file" + curfile + ". Trying to save it with randomly generated name...");
								newcurfile = Lib.genrandomword(8);
								file2 = new File(musicpath+newcurfile+".ogg");
								if(file.renameTo(file2)) {
									logger.info(curfile + " saved as "+ newcurfile);
								}
								else {
									logger.error(" Error while renaming file " + curfile + " this file will be ignored.");
									continue;
								}
							}
							curfile = newcurfile;
						}
						
						String displayname = Lib.ToUpperWords(curfile.replaceAll("_", " "));
						boolean customtexture = false;
						//Getting vorbis data from ogg file
						try {
							VorbisFile vrb = new VorbisFile(Paths.get(musicpath+curfile+ext).toFile());
							if(!(vrb.getTags().getTitle() == null || vrb.getTags().getArtist() == null))
								displayname = vrb.getTags().getArtist() + " - " + vrb.getTags().getTitle();
							
							customtexture = GenerateAlbumArtTexture(curfile,vrb.getTags().getAlbumArt(),respath);
						} catch (Exception e) {
							logger.error("Error getting vorbis data from ogg file! "+e.getMessage());
						}
						
						logger.debug("File registered: "+curfile);
						String fc = soundsj.isEmpty() ? "{" : ",";
						soundsj = soundsj + Lib.appendSounds(curfile,fc);
						lang = lang + Lib.appendLang("item","record_"+curfile,"Music Disc",displayname,fc);
						Lib.generateItemJson("record_"+curfile,customtexture, respath);
					}
					else if(matcher.find()) {
						logger.warn(curfile+" has illegal symbols in name and can't be renamed, please rename this file manually.");
						continue;
					}
					
					//Initialising sounds and records
					SoundEvent snd = new SoundEvent(new ResourceLocation(mcdj.MODID,curfile)).setRegistryName(mcdj.MODID,curfile);
					sounds.add(snd);
					if(!Config.isHeadlessMode()) records.add(new CustomDiscItem(curfile, snd,records.size()));
					
					
				    
				}
				else {
					if(!Paths.get(musicpath+"junk/").toFile().exists())
						Paths.get(musicpath+"junk/").toFile().mkdirs();
					File file = new File(proxy.getCWD()+musicpath+curfile);
					File file2 = new File(proxy.getCWD()+musicpath+"junk/"+curfile);
					if(file.renameTo(file2)) logger.warn("nonmusic file \"" +curfile+"\" is moved to junk folder!");
					else logger.warn("nonmusic file \"" +curfile+"\" can't be moved! Please remove it manually later. ");
				}
			  }
			}
			if(playlistchanged) {
				soundsj = soundsj + "}";
				lang = lang + "}";
				Lib.writefile(respath+"sounds.json",soundsj);
				Lib.writefile(respath+"lang/en_us.json",lang);
				Lib.writefile(rootpath+"mc.hash",Lib.HashMusicDir(MusicFiles));
			}
			musicloaded = sounds.size() > 0;
			if(!Config.isHeadlessMode()) ModItems.RECORDS = records.toArray(new CustomDiscItem[records.size()]);
			ModSoundEvents.RECORDS = sounds.toArray(new SoundEvent[sounds.size()]);
		}
    }
	
	private static boolean CheckResourcePack(String r, String res) {
		return (
				Files.isDirectory(Paths.get(res+"models/item")) && 
				Files.isDirectory(Paths.get(res+"textures/items")) && 
				Files.isDirectory(Paths.get(res+"sounds/streaming")) && 
				Files.isDirectory(Paths.get(res+"lang")) && 
				Paths.get(r+"pack.mcmeta").toFile().exists()
				);
	}
	
	private static boolean GenerateResourcePack(String r, String res, boolean rootexist) {
		if(!rootexist) mcdj.logger.info("MCDJ is installing...");
		Paths.get(res+"models/item").toFile().mkdirs();
		Paths.get(res+"textures/items").toFile().mkdirs();
		Paths.get(res+"sounds/streaming").toFile().mkdirs();
		Paths.get(res+"lang").toFile().mkdirs();
		Lib.writefile(r+"pack.mcmeta","{\"pack\": {\"pack_format\": 5,\"description\": \"mcdj\"}}");
		
		if(Paths.get(r+"mc.hash").toFile().exists())
			Paths.get(r+"mc.hash").toFile().delete();
		return true;
	}
	
	private boolean GenerateAlbumArtTexture(String curfile,String base64image, String respath)
    {
    	boolean artfound = false;
		
    	//Getting album art from base64
		if(Config.AlbumArts() && base64image != null) {
	    	int ind = base64image.indexOf("/9j/");
	    	if(ind!=-1 && ind < 100) {
	    		base64image = base64image.substring(ind);
	    		artfound = true;
	    	}
	    	else if(Config.IsHiResTexture()) return false;
		}
		else if(Config.IsHiResTexture()) return false;
		
    	BufferedImage Basetexture = null;
		try {
			if(Config.IsHiResTexture())
				Basetexture = ImageIO.read(getClass().getClassLoader().getResourceAsStream("custom.png"));
			else
				Basetexture = ImageIO.read(getClass().getClassLoader().getResourceAsStream("custom16.png"));
		} catch (IOException e1) {
			mcdj.logger.error("Error getting base texture! Try to reinstall MCDJ"+e1.getMessage());
			return false;
		}
		
		//Getting base texture size
		double hb = Basetexture.getHeight();
		double wb = Basetexture.getWidth();
		double h = hb/2.28;
		double w = wb/3.37;
		
    	BufferedImage AlbumArt = null;
    	if(artfound) {
	    	byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64image);
			try {
				AlbumArt = ImageIO.read(new ByteArrayInputStream(imageBytes));
			} catch (IOException e1) {
				mcdj.logger.error("Error getting album art!"+e1.getMessage());
			}
    	}
    	
    	else if(AlbumArt == null){
    		AlbumArt = new BufferedImage((int)hb/2, (int)wb/2, BufferedImage.TYPE_INT_ARGB);
    		Graphics2D g2d = AlbumArt.createGraphics();
    		
    		//GradientPaint p = new GradientPaint(0, 0, new Color(getrand(curfile, 50),getrand(curfile, 150),getrand(curfile, 250)), (int)h, (int)w, new Color(getrand(curfile, 500),getrand(curfile, 300),getrand(curfile, 450)));
    		Color p = new Color(getrand(curfile, 50),getrand(curfile, 100),getrand(curfile, 150));
    		//g2d.setPaint ( new Color ( getrand(curfile,2), getrand(curfile,5), getrand(curfile,3) ) );
    		g2d.setPaint (p);
    		g2d.fillRect (0, 0, AlbumArt.getWidth(), AlbumArt.getHeight());
    	}
		
		double scale_h = Math.round((h / AlbumArt.getHeight())*10000)/10000.0;
		double scale_w = Math.round((w / AlbumArt.getWidth())*10000)/10000.0;

		int shifting_h = (int) (hb / 3.55);
		int shifting_w = (int) (wb / 2.78);
		
		// Preparing album art
		BufferedImage ResizedAlbumArt = 
				new BufferedImage((int) h, (int) w, BufferedImage.TYPE_INT_ARGB);
		AffineTransform at = new AffineTransform();
		at.scale(scale_h, scale_w);
		AffineTransformOp so = new AffineTransformOp(at, AffineTransformOp.TYPE_BILINEAR);
		ResizedAlbumArt = so.filter(AlbumArt, ResizedAlbumArt);
    	
        // Drawing album art on base texture
    	BufferedImage albumtexture = 
    			new BufferedImage((int) hb, (int) wb, BufferedImage.TYPE_INT_ARGB);
    	Graphics g = albumtexture.getGraphics();
    	g.drawImage(ResizedAlbumArt, shifting_h, shifting_w, null);
    	g.drawImage(Basetexture, 0, 0, null);
    	g.dispose();

    	try {
			ImageIO.write(albumtexture, "PNG", new File(respath+"textures/items", "record_"+ curfile +".png"));
		} catch (IOException e) {
			mcdj.logger.error("Error creating custom disc texture, default texture will be used!"+e.getMessage());
			return false;
		}
    	return true;
    }
	
	public static int getrand(String name, int c) {
		int r = Math.abs((name.hashCode() % 256) - c);
		while (r > 255) r = r/2;
		return r;
	}
}
