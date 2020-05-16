package com.shoxie.mcdj;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.gagravarr.vorbis.VorbisFile;
import com.shoxie.mcdj.item.ItemHQRecord;
import com.shoxie.mcdj.proxy.CommonProxy;

@Mod(modid = mcdj.MODID, name = mcdj.NAME, version = mcdj.VERSION)
public class mcdj
{
    public static final String MODID = "mcdj";
    public static final String NAME = "MCDJ";
    public static final String VERSION = "1.0";
    public static Logger logger = LogManager.getLogger(MODID);
    
    public static final String DEFAULT_RECORD_TEXTURE = "record_default";
    public static boolean musicloaded;
    private boolean playlistchanged;
	
    @Mod.Instance(MODID)
	public static mcdj instance;
	
	@SidedProxy(serverSide = "com.shoxie.mcdj.proxy.CommonProxy", clientSide = "com.shoxie.mcdj.proxy.ClientProxy")
	public static CommonProxy proxy;
	
	
	
	@Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {	
		String rootpath = "."+MODID+"/";
		String respath = rootpath+"assets/"+MODID+"/";
		String musicpath = respath+"sounds/streaming/";
		MusicScan(rootpath, respath, musicpath);
    }

	@Mod.EventHandler
	public void init(FMLInitializationEvent event) {

	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		if(playlistchanged) proxy.rpreload();
	}

	
	
	
	private void MusicScan(String rootpath, String respath, String musicpath) {
		File rp = Paths.get(rootpath).toFile();
    	boolean rootexist = rp.exists();
    	boolean rootcreated = false;
    	if(!rootexist) {
    		rootcreated = GenerateResourcePack(rootpath, respath);
    		Lib.createSymLink(musicpath);
    	}
    	else
    		logger.info("Scanning music folder...");
    	proxy.rpinit(rp);
    	File[] MusicFiles = Paths.get(musicpath).toFile().listFiles();
    	
    	String soundsj = "";
    	String lang = "";
    	ArrayList<SoundEvent> sounds = new ArrayList<SoundEvent>();
	    ArrayList<ItemHQRecord> records = new ArrayList<ItemHQRecord>();
		
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
				if(curfile.substring(curfile.length() - 4).compareTo(".ogg") == 0) {
					curfile = curfile.substring(0,curfile.length() - 4);
					
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
							File file = new File(musicpath+curfile+".ogg");
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
						boolean Albumarttexture = false;
						
						//Getting vorbis data from ogg file
						try {
							VorbisFile vrb = new VorbisFile(Paths.get(musicpath+curfile+".ogg").toFile());
							if(!(vrb.getTags().getTitle() == null || vrb.getTags().getArtist() == null))
								displayname = vrb.getTags().getArtist() + " - " + vrb.getTags().getTitle();
							
							String art = vrb.getTags().getAlbumArt();
							if(art!=null) Albumarttexture = GenerateAlbumArtTexture(curfile,art,respath);
						} catch (FileNotFoundException e) {
							logger.error("Error getting vorbis data from ogg file!"+e.getMessage());
						} catch (IOException e) {
							logger.error("Error getting vorbis data from ogg file!"+e.getMessage());
						}
						
						logger.debug("File registered: "+curfile);
						soundsj = soundsj + Lib.appendSounds(curfile,",");
						lang = lang + Lib.appendLang("item","record_"+curfile,"Music Disc",displayname,true);
						Lib.generateItemJson("record_"+curfile,Albumarttexture, respath);
					}
					else if(matcher.find()) {
						logger.warn(curfile+" has illegal symbols in name and can't be renamed, please rename this file manually.");
						continue;
					}
					
					//Initialising sounds and records
					SoundEvent snd = new SoundEvent(new ResourceLocation(mcdj.MODID,curfile)).setRegistryName(mcdj.MODID,curfile);
					ItemHQRecord currec = new ItemHQRecord("record_"+curfile, snd);
					sounds.add(snd);
				    records.add(currec);
				}
				else logger.warn(curfile+" is not valid music file! Only .ogg files are allowed");
			  }
			}
			if(playlistchanged) {
				soundsj = soundsj + "}";
				Lib.writefile(respath+"sounds.json",soundsj);
				Lib.writefile(respath+"lang/en_us.lang",lang);
				Lib.writefile(rootpath+"mc.hash",Lib.HashMusicDir(MusicFiles));
			}
			musicloaded = records.size() > 0;
			Items.RECORDS = records.toArray(new ItemHQRecord[records.size()]);
			SoundEvents.RECORDS = sounds.toArray(new SoundEvent[sounds.size()]);
		}
    }
	
	private static boolean GenerateResourcePack(String r, String res) {
		mcdj.logger.info("MCDJ is installing...");
		if(!(
				Paths.get(res+"models/item").toFile().mkdirs() && 
				Paths.get(res+"textures/items").toFile().mkdirs() && 
				Paths.get(res+"sounds/streaming").toFile().mkdirs() && 
				Paths.get(res+"lang").toFile().mkdirs()))
			return false;
		
		Lib.writefile(r+"pack.mcmeta","{\"pack\": {\"pack_format\": 3,\"description\": \"mcdj\"}}");
		return true;
	}
	
	private boolean GenerateAlbumArtTexture(String curfile,String base64image, String respath)
    {
    	
    	//Getting album art from base64
    	int ind = base64image.indexOf("/9j/");
    	if(ind!=-1 && ind < 100)
    		base64image = base64image.substring(ind);
    	else {
    		mcdj.logger.warn("Can't read album art of song "+curfile+" this image type doesn't support yet :("); 
    		return false;
    	}
    	
    	byte[] imageBytes = javax.xml.bind.DatatypeConverter.parseBase64Binary(base64image);
    	BufferedImage AlbumArt = null;
		try {
			AlbumArt = ImageIO.read(new ByteArrayInputStream(imageBytes));
		} catch (IOException e1) {
			mcdj.logger.error("Error getting album art!"+e1.getMessage());
			return false;
		}
		
    	BufferedImage Basetexture = null;
		try {
			Basetexture = ImageIO.read(getClass().getClassLoader().getResourceAsStream("record_base.png"));
		} catch (IOException e1) {
			mcdj.logger.error("Error getting base texture! Try to reinstall MCDJ"+e1.getMessage());
			return false;
		}
		
		//Getting base texture size
		double hb = Basetexture.getHeight();
		double wb = Basetexture.getWidth();
		double h = hb/2.28;
		double w = wb/3.37;
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
			mcdj.logger.error("Error writing album art texture, default texture will be used!"+e.getMessage());
			return false;
		}
    	return true;
    }
	
}
