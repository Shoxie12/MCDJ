package com.shoxie.mcdj;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gagravarr.vorbis.VorbisFile;
import com.shoxie.mcdj.item.ItemCustomRecord;
import com.shoxie.mcdj.proxy.CommonProxy;

@Mod(modid = mcdj.MODID, name = mcdj.NAME, version = mcdj.VERSION)
public class mcdj
{
    public static final String MODID = "mcdj";
    public static final String NAME = "MCDJ";
    public static final String VERSION = "2.1";
    public static Logger logger = LogManager.getLogger(MODID);
    public static Configuration config;
    
	public static final String DEFTEXTURE = "defaultrecord";
	public static final String DEFTEXTURE16 = "defaultrecord16";
	public static final String DEFAULT_BLANK_RECORD_SOUND = "br";
    public static boolean musicloaded;
    private boolean playlistchanged;
	
    @Mod.Instance(MODID)
	public static mcdj instance;
	
	@SidedProxy(serverSide = "com.shoxie.mcdj.proxy.CommonProxy", clientSide = "com.shoxie.mcdj.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {	
		proxy.preInit(event);
		
		File directory = event.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "mcdj.cfg"));
        Config.readConfig();
        
        String mpath = Config.mcdjpath;
        String fname = Config.mcdjrootfoldername;
        if(!mpath.equals("")) mpath = mpath + "/";
        if(fname.equals("")) fname = ".mcdj";
        
        String rootpath = mpath+fname+"/";
        String respath = rootpath+"assets/"+MODID+"/";
        
		String musicpath = respath+"sounds/streaming/";
		logger.debug("Music folder is located at "+musicpath);
		MusicScan(rootpath, respath, musicpath);
    }

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if(playlistchanged) proxy.rpreload();
        if (config.hasChanged()) {
            config.save();
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
    		if(Config.shortcut) Lib.createSymLink(musicpath);
    	}
    	else
    		logger.info("Scanning music folder...");
    	proxy.rpinit(rp);
    	File[] MusicFiles = Paths.get(musicpath).toFile().listFiles();
		Arrays.sort(MusicFiles, Comparator.comparing(File::getName));
    	String type = "";
    	String soundsj = "";
    	String lang = "";
    	ArrayList<SoundEvent> sounds = new ArrayList<SoundEvent>();
	    ArrayList<ItemCustomRecord> records = new ArrayList<ItemCustomRecord>();
		
		//Scaning music folder
		if(MusicFiles.length == 0) {
			if(rootexist) logger.info("Your playlist is empty. Try to add some music!");
			else if(rootcreated) logger.info("MCDJ installed sucessful! Now go to .minecraft/Music/ folder and add some music (restarting Minecraft is requred!)");
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
				
				//Adding static names to en_us.lang and sounds.json
				lang = lang + Lib.appendLang("tile","musicgenerator","Music Generator","",false);
				lang = lang + Lib.appendLang("item","blankrecord","Blank Music Disc","nothing",true);
				lang = lang + Lib.appendLang("item","obsidianplate","Obsidian Plate","",false);
				soundsj = soundsj + Lib.appendSounds("br","{");
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
						lang = lang + Lib.appendLang("item","record_"+curfile,"Music Disc",displayname,true);
						Lib.generateItemJson("record_"+curfile,customtexture, respath);
					}
					else if(matcher.find()) {
						logger.warn(curfile+" has illegal symbols in name and can't be renamed, please rename this file manually.");
						continue;
					}

					
					//Initialising sounds and records
					SoundEvent snd = new SoundEvent(new ResourceLocation(mcdj.MODID,curfile)).setRegistryName(mcdj.MODID,curfile);
					ItemCustomRecord currec = new ItemCustomRecord("record_"+curfile,records.size(), snd);
					sounds.add(snd);
					if(!Config.headlessmode) records.add(currec);
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
				Lib.writefile(respath+"sounds.json",soundsj);
				Lib.writefile(respath+"lang/en_us.lang",lang);
				Lib.writefile(rootpath+"mc.hash",Lib.HashMusicDir(MusicFiles));
			}
			musicloaded = sounds.size() > 0;
			if(!Config.headlessmode) ModItems.RECORDS = records.toArray(new ItemCustomRecord[records.size()]);
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
		mcdj.logger.info("MCDJ is installing...");
		Paths.get(res+"models/item").toFile().mkdirs();
		Paths.get(res+"textures/items").toFile().mkdirs();
		Paths.get(res+"sounds/streaming").toFile().mkdirs();
		Paths.get(res+"lang").toFile().mkdirs();
		Lib.writefile(r+"pack.mcmeta","{\"pack\": {\"pack_format\": 3,\"description\": \"mcdj\"}}");
		
		if(Paths.get(r+"mc.hash").toFile().exists())
			Paths.get(r+"mc.hash").toFile().delete();
		return true;
	}
	
	private boolean GenerateAlbumArtTexture(String curfile,String base64image, String respath)
    {
    	boolean artfound = false;
		
    	//Getting album art from base64
		if(Config.albumarts && base64image != null) {
	    	int ind = base64image.indexOf("/9j/");
	    	if(ind!=-1 && ind < 100) {
	    		base64image = base64image.substring(ind);
	    		artfound = true;
	    	}
	    	else if(Config.hires_texture) return false;
		}
		else if(Config.hires_texture) return false;
		
    	BufferedImage Basetexture = null;
		try {
			if(Config.hires_texture)
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
