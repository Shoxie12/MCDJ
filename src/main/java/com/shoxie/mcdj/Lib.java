package com.shoxie.mcdj;

import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.shoxie.mcdj.init.Init;
import com.shoxie.mcdj.misc.discRegData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PathPackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.world.flag.FeatureFlagSet;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.gagravarr.ogg.audio.OggAudioStatistics;
import org.gagravarr.vorbis.VorbisFile;

import com.shoxie.mcdj.mcdj.SupportedFormats;
import com.shoxie.mcdj.misc.MusicFile;

import net.minecraft.locale.Language;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

import static net.minecraft.locale.Language.loadFromJson;


public final class Lib {
	private static final Gson GSON = new Gson();

	public static String genrandomword(int len) {
        String basestr = "qwertyuiopasdfghjklzxcvbnm"; 
        StringBuilder sb = new StringBuilder(len); 
        for (int i = 0; i < len; i++) {
            int index = (int)(basestr.length()* Math.random()); 
            sb.append(basestr.charAt(index)); 
        } 
        return sb.toString();
	}

	public static String cutFileExt(String Filename){
		return Filename.substring(0,Filename.length() - (FilenameUtils.getExtension(Filename).length()+1));
	}
	
	public static String ToUpperWords(String src) {
		StringBuilder res = new StringBuilder(src.substring(0, 1).toUpperCase());
		for (int i = 1; i < src.length(); i++) {
		    if (" ".equals(src.substring(i-1, i)))
		    	res.append(src.substring(i, i + 1).toUpperCase());
		    else
		    	res.append(src.charAt(i));
		}
		return res.toString();
	}

	public static void writefile(String filepath ,String sContent)
    {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(filepath))) {
        	bufferedWriter.write(sContent);
        } catch (IOException e) {
        	mcdj.logger.error(getTranslated("message.mcdj.error.io.write")+": "+e.getMessage());
		}
    }

	public static String readFile(File file)
	{
		try(FileInputStream inputStream = new FileInputStream(file)) {
			return IOUtils.toString(inputStream);

		} catch (Exception e) {
			mcdj.logger.error(getTranslated("message.mcdj.error.io.read")+": "+e.getMessage());
			return null;
		}
	}

	public static String HashMusicDir(File[] lomf) {
		StringBuilder chk = new StringBuilder();
        for (File file : lomf)
			chk.append(file.getName())
					.append(file.length());
		return Integer.toString(chk.toString().hashCode());
	}
	  
    
	public static void cleanAll(String respath) {
		try {
			FileUtils.cleanDirectory(Paths.get(respath+"models/item").toFile());
			FileUtils.cleanDirectory(Paths.get(respath+"textures/item").toFile());
		} catch (IOException e) { mcdj.logger.error(getTranslated("message.mcdj.error.cleanall") + e.getMessage()); }
		Paths.get(respath+"sounds.json").toFile().delete();
		Paths.get(respath+"lang/en_us.lang").toFile().delete();
    }
	
	public static void generateItemJson(String fname, Boolean art, String respath) {
		String name = (art) ? fname : mcdj.DEFTEXTURE;
		JsonObject itemTextures = new JsonObject();
		JsonObject item = new JsonObject();

		itemTextures.addProperty("layer0", mcdj.MODID+":item/"+name);
		item.addProperty("parent", "item/generated");
		item.add("textures", itemTextures);
		writefile(respath+"models/item/"+fname+".json",GSON.toJson(item));
	}

	public static void generateSoundsJson(String path, ArrayList<discRegData> records_data) {
		JsonObject root = new JsonObject();

		for (discRegData iMusic : records_data) {
			JsonObject sound = new JsonObject();
			JsonArray soundarr = new JsonArray();
			JsonObject soundinfo = new JsonObject();

			soundinfo.addProperty("name", mcdj.MODID+":streaming/" + iMusic.getFileName());
			soundinfo.addProperty("stream", true);
			soundarr.add(soundinfo);

			sound.addProperty("category", "record");
			sound.add("sounds", soundarr);

			root.add(iMusic.getName(), sound);
		}
		writefile(path+"sounds.json",GSON.toJson(root));
	}

	public static void generateTagsJson(String dataPath, ArrayList<discRegData> records_data) {
		JsonObject root = new JsonObject();
		JsonArray Tags = new JsonArray();
		for (discRegData iMusic : records_data)
			Tags.add(mcdj.MODID + ":" + iMusic.getName());

		root.add("values", Tags);
		String s = GSON.toJson(root);
		writefile(dataPath+"music_discs.json",s);
		if(Config.isCreeperDropEnabled()) writefile(dataPath+"creeper_drop_music_discs.json",s);
		else if(Paths.get(dataPath+"creeper_drop_music_discs.json").toFile().exists()) Paths.get(dataPath+"creeper_drop_music_discs.json").toFile().delete();
	}

	public static void generateLangJson(String path, ArrayList<discRegData> records_data) {
		JsonObject root = new JsonObject();

		for (var iMusic : records_data) {
			root.addProperty("item.mcdj."+iMusic.getName(), getTranslated("item.minecraft.music_disc_13"));
			root.addProperty("item.mcdj."+iMusic.getName()+".desc", iMusic.getDisplayName());
		}
		writefile(path+"lang/en_us.json",GSON.toJson(root));
	}

	public static void generateRespackJson(String path) {
		JsonObject root = new JsonObject();
		JsonObject pack = new JsonObject();
		pack.addProperty("pack_format", 8);
		pack.addProperty("description", "mcdj");
		root.add("pack", pack);

		writefile(path,GSON.toJson(root));
	}

	public static SupportedFormats getFileType(File s) {
		String sign = getsign(s);
		if(sign != null){
			if(sign.contains("_4f676753")) return SupportedFormats.ogg;
			if(sign.contains("_494433") || sign.contains("_fffb")) return SupportedFormats.mp3;
		}
		return null;
	}

	public static String getsign(File file){
		try {
			InputStream inputStream = new FileInputStream(file);
			String s = "_";
			for (int i = 0; i<10;i++){
				s = s + Integer.toHexString(inputStream.read());
			}
			inputStream.close();
			return s.toLowerCase();
		} catch (Exception e) {
			mcdj.logger.error(getTranslated("message.mcdj.error.io.read") + e.getMessage());
		}
		return null;
	}

	public static boolean CheckPlaylist(String r, String res) {
		return (
				Files.isDirectory(Paths.get(res+"models/item")) && 
				Files.isDirectory(Paths.get(res+"textures/item")) &&
				Files.isDirectory(Paths.get(res+"sounds/streaming")) && 
				Files.isDirectory(Paths.get(res+"lang")) && 
				Paths.get(r+"pack.mcmeta").toFile().exists()
				);
	}

	public static void InitPlaylist(String r, String res) {
		Paths.get(getPlaylistDataPath()).toFile().mkdirs();
		Paths.get(res+"models/item").toFile().mkdirs();
		Paths.get(res+"textures/item").toFile().mkdirs();
		Paths.get(res+"sounds/streaming").toFile().mkdirs();
		Paths.get(res+"lang").toFile().mkdirs();
		generateRespackJson(r+"pack.mcmeta");
		
		if(Paths.get(r+"mc.hash").toFile().exists())
			Paths.get(r+"mc.hash").toFile().delete();
	}

	public static boolean addNewToPlaylist(Path path){
		SupportedFormats oformat = getFileType(path.toFile());
		if(oformat != null) {
			File newfile = getDestinationFile(path.toFile());
			if(newfile != null) {
				if(newfile.exists() && getFileType(newfile) == SupportedFormats.ogg) {
					Path newpath = newfile.toPath();
					String name = getSongNameFromTags(newpath);
					mcdj.needrestart = true;
					mcdj.currentPlaylist.add(new MusicFile(name, newpath.getFileName().toString(), newpath,false,mcdj.currentPlaylist.size()));
					return true;
				}
			}
		}
		return false;
	}

	public static File getDestinationFile(File source) {
        return new File(
			getPlaylistStreamingPath()+
			fnamestringfix(cutFileExt(source.getName()))+".ogg");
	}

	public static String getFileHash(Path path) {
		try {
			byte[] data = Files.readAllBytes(path);
			byte[] hash = MessageDigest.getInstance("MD5").digest(data);
			return new BigInteger(1, hash).toString(16);
		} catch (Exception e) {
			mcdj.logger.error(getTranslated("message.mcdj.error.io.hash")+": "+e.getMessage());
		}
		return "-";
	}

	public static String getSongNameFromTags(Path path) {
		try {
			VorbisFile vrb = new VorbisFile((path).toFile());
			String Artist = vrb.getTags().getArtist();
			String title = vrb.getTags().getTitle();
			vrb.close();
			if(title != null) 
				return Artist != null ? Artist + " - " + title : title;
			
		} catch (Exception e) {
			mcdj.logger.error(getTranslated("message.mcdj.error.io.tags")+": "+e.getMessage());
		}
		return ToUpperWords(cutFileExt(path.getFileName().toString()).replaceAll("_", " "));
	}

	public static ArrayList<discRegData> MusicScan() {
		boolean playlistchanged = false;

    	if(!CheckPlaylist(getPlaylistRootPath(), getPlaylistAssetsPath())) InitPlaylist(getPlaylistRootPath(), getPlaylistAssetsPath());
    	else mcdj.logger.info(getTranslated("message.mcdj.musicscanning"));

		DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> Lib::rpinit);

		int iNumber = 0;
    	File[] MusicFiles = Paths.get(getPlaylistStreamingPath()).toFile().listFiles();
		ArrayList<discRegData> records_data = new ArrayList<>();

		//Scaning music folder
		if(MusicFiles == null) { return new ArrayList<>(); }
		Arrays.sort(MusicFiles, Comparator.comparing(File::getName));
		if(MusicFiles.length == 0) {
			if(Paths.get(getPlaylistRootPath()).toFile().exists()) mcdj.logger.info(getTranslated("message.mcdj.playlistempty"));
			else mcdj.logger.error(getTranslated("message.mcdj.error.rpinit"));
			
			//Cleaning up textures and json files, if we found old hash file
			File hf = Paths.get(getPlaylistRootPath()+"mc.hash").toFile();
			if(hf.exists()) {
				cleanAll(getPlaylistAssetsPath());
				hf.delete();
			}
			if(playlistchanged) mcdj.logger.info(getTranslated("message.mcdj.playlistempty"));
			return null;
		}
			
		String lasthash = readFile(Paths.get(getPlaylistRootPath()+"mc.hash").toFile());
			
		//Checking for changes in music directory
		if(lasthash!=null) {
			if(lasthash.compareTo(HashMusicDir(MusicFiles))!=0) {
				playlistchanged=true;
				cleanAll(getPlaylistAssetsPath());
			}
		}
		else playlistchanged=true;

		//Parsing streaming directory
		for (int i = 0; i < MusicFiles.length; i++) {
			if (MusicFiles[i].isFile()) {
				String curfile = MusicFiles[i].getName();
				Path filepath = Paths.get(getPlaylistStreamingPath()+curfile);
				mcdj.logger.debug(getTranslated("message.mcdj.debug.procfile")+": "+curfile);
				if(getFileType(filepath.toFile()) == SupportedFormats.ogg) {
					curfile = cutFileExt(curfile);

					//Checking file name for illegal symbols
					if(!checkFilename(curfile)) {
						playlistchanged = true;
						curfile = validateFilename(curfile,".ogg",getPlaylistStreamingPath());
						if(curfile == null) {
							continue;
						}
						filepath = Paths.get(getPlaylistStreamingPath()+curfile+".ogg");
					}
					var dname = getSongNameFromTags(filepath);
					if(playlistchanged) {
						validateCustomTexture(curfile, filepath, getPlaylistAssetsPath());
						mcdj.logger.debug(curfile+" "+getTranslated("message.mcdj.fileregistered")+" "+dname);
					}
					records_data.add(new discRegData(++iNumber,curfile,dname,filepath));
				}
				else movetojunk(curfile,getPlaylistStreamingPath());
			}
		}
		if(playlistchanged) {
			generateSoundsJson(getPlaylistAssetsPath(),records_data);
			generateTagsJson(getPlaylistDataPath(),records_data);
			generateLangJson(getPlaylistAssetsPath(), records_data);
			//generatePlayList(rootpath, records_data);
			writefile(getPlaylistRootPath()+"mc.hash",HashMusicDir(MusicFiles));
		}
		mcdj.musicloaded = !records_data.isEmpty();
		return records_data;
    }

	private static void validateCustomTexture(String curfile, Path filepath, String respath) {
		boolean customtexture = GenerateDiscTexture(curfile,filepath,respath);
		generateItemJson("record_"+curfile,customtexture, respath);
	}

	public static boolean GenerateDiscTexture(String trackName, Path path, String respath)
    {
    	BufferedImage Basetexture;
		try {
			Basetexture = ImageIO.read(mcdj.class.getClassLoader().getResourceAsStream("discbase.png"));
		} catch (IOException e1) {
			mcdj.logger.error(getTranslated("message.mcdj.error.texturemissing")+" "+e1.getMessage());
			return false;
		}
		
		//Getting base texture size
		double hb = Basetexture.getHeight();
		double wb = Basetexture.getWidth();
		double h = hb/2.28;
		double w = wb/3.37;
		
    	BufferedImage AlbumArt;
    	AlbumArt = new BufferedImage((int)hb/2, (int)wb/2, BufferedImage.TYPE_INT_ARGB);
    	Graphics2D g2d = AlbumArt.createGraphics();
    		
    	Color p = new Color(getrand(trackName, 50),getrand(trackName, 100),getrand(trackName, 150));
    	g2d.setPaint (p);
    	g2d.fillRect (0, 0, AlbumArt.getWidth(), AlbumArt.getHeight());
		
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
			ImageIO.write(albumtexture, "PNG", new File(respath+"textures/item", "record_"+ trackName +".png"));
		} catch (IOException e) {
			mcdj.logger.error(getTranslated("message.mcdj.error.io.writetexture")+": "+e.getMessage());
			return false;
		}
    	return true;
    }

	public static File fnamestringfix(File f){
		String path = genrandomword(8);
		try {
			path = f.getCanonicalPath();
		} catch (IOException e) {
			mcdj.logger.error(getTranslated("message.mcdj.error.io.read")+": "+e.getMessage());
		}
		String ptf = path.replace(f.getName(),"");
		String fname = f.getName();
		String ext = "."+FilenameUtils.getExtension(fname);
        fname = fname.replace(ext, "");
        String fixedname = fnamestringfix(fname);
		return new File(ptf+fixedname+ext);
	}

	public static String fnamestringfix(String str){
		String ret = str.toLowerCase();
		String regextocheck = "[^a-z0-9- _]";		
		if(ret.replaceAll("[^a-z0-9]", "").length() > 5) {
			ret = ret.replaceAll(" ", "_");
			ret = ret.replaceAll(regextocheck, "");
		}
		else {
			ret = genrandomword(8);
		}
		return ret;
	}

	public static boolean checkFilename(String oldname){
		if(oldname.length() > 5){
			String regextocheck = "[^a-z0-9- _]";
			Matcher matcher = Pattern.compile(regextocheck).matcher(oldname);
			if(!matcher.find()) return true;
		}
		return false;
	}

	public static String validateFilename(String oldname, String ext, String musicpath){
		//Checking file name for illegal symbols.
			String ret = oldname;
			String regextocheck = "[^a-z0-9- _]";
			Matcher matcher = Pattern.compile(regextocheck).matcher(oldname);
			if(matcher.find() || oldname.length() <= 5) {
				String newcurfile = fnamestringfix(oldname);
				File file = new File(musicpath+oldname+ext);
				int iname = 0;
				if(new File(musicpath+newcurfile+ext).exists())
					while(new File(musicpath+newcurfile+(iname>0? "_"+ iname : "")+ext).exists()) iname++;
				File file2 = new File(musicpath+newcurfile+(iname>0? "_"+ iname : "")+ext);
				if (!file.renameTo(file2)) {
					newcurfile = genrandomword(8);
					file2 = new File(musicpath+newcurfile+ext);
					if(file.renameTo(file2)) {
						mcdj.logger.info(oldname + " "+getTranslated("message.mcdj.newfilename")+" "+ newcurfile);
						ret = newcurfile;
					}
					else {
						mcdj.logger.error(oldname + " " + getTranslated("message.mcdj.error.renamefile"));
						return null;
					}
				}
				else {
					mcdj.logger.info(oldname + " "+getTranslated("message.mcdj.newfilename")+" "+ newcurfile);
					ret = newcurfile;
				}
			}
			return ret;
		}

	public static void movetojunk(String name, String mpath) {
		Path nonmusic = Paths.get(getCWD()+"nonmusic/");
		if(!nonmusic.toFile().exists()) nonmusic.toFile().mkdirs();
		File file = new File(getCWD()+mpath+name);
		File file2 = new File(nonmusic +name);
		if(file.renameTo(file2)) mcdj.logger.warn(name + " "+getTranslated("message.mcdj.error.notogg")+": " + nonmusic);
		else mcdj.logger.warn(getTranslated("message.mcdj.error.io.write") + " \"" +name+"\". " + getTranslated("message.mcdj.error.fileignored"));
	}

	public static int getrand(String name, int c) {
		int r = Math.abs((name.hashCode() % 256) - c);
		while (r > 255) r = r/2;
		return r;
	}

	public static boolean isSupportedMusicFile(Path path) {
        return getFileType(path.toFile()) != null;
    }

	public static String getTranslated(String val) {
		String ret = Language.getInstance().getOrDefault(val);
		if(ret.equals(val)) ret = mcdj.PreInitLangMap.getOrDefault(val, val);
		return ret;
	}

	public static ItemStack getRandomMusicDisc() {
		Random rand = new Random();
		int discid = rand.nextInt(Init.CUSTOM_RECORD_ITEMS.size());
		return new ItemStack(Init.CUSTOM_RECORD_ITEMS.get(discid).get());
    }

	public static String getCWD(){
		return System.getProperty("user.dir")+"/";
	}

	public static String getRoot(){
		return getCWD()+"/"+Config.getMcdjPath()+".mcdj/";
	}

	public static String getPlaylistRootPath(){
		return getRoot();
	}

	public static String getPlaylistAssetsPath(){
		return getPlaylistRootPath()+"assets/"+mcdj.MODID+"/";
	}

	public static String getPlaylistStreamingPath(){
		return getPlaylistAssetsPath() + "sounds/streaming/";
	}

	public static String getPlaylistDataPath(){
		return getPlaylistRootPath() + "data/minecraft/tags/items/";
	}

	public static int getAudioDuration(File audio) {
		try {
			VorbisFile audioInputStream = new VorbisFile(audio);
			var f = new OggAudioStatistics(audioInputStream,audioInputStream);
			f.calculate();
			return (int)(f.getDurationSeconds());


		} catch (Exception e) {
			mcdj.logger.warn(getTranslated("message.mcdj.error.io.read") + audio.getName() + " " + getTranslated("message.mcdj.error.io.defduration") + " " + e.getMessage());
			return mcdj.DEFAULT_SONG_DURATION;
		}
	}

	
	public static void rpinit() {
		final String id = "mcdj";
		final Component name = Component.literal("MCDJ Resource Pack");
		final Component description = Component.literal("Makes possible to create custom music discs");
		Minecraft.getInstance().getResourcePackRepository().addPackFinder(nameToPackMap -> nameToPackMap.accept(
                Pack.create(id, description, true, new PathPackResources.PathResourcesSupplier(
                                Paths.get(getPlaylistRootPath()),true
                        ),
                        new Pack.Info(
                                description, PackCompatibility.COMPATIBLE, FeatureFlagSet.of(), new ArrayList<>()
                        ), Pack.Position.TOP,false,

                        new PackSource(){

                            @Override
                            public Component decorate(Component p_decorate_1_) {
                                return name;
                            }

                            @Override
                            public boolean shouldAddAutomatically() {
                                return true;
                            }
                        })));
	}

		public static void Checkffmpeg () {
			new Thread(() -> {
                Process p;
                try {
                    p = new ProcessBuilder("ffmpeg", "-h").start();
                    String s = new String(p.getInputStream().readAllBytes());
                    if (s.contains("Universal media converter")) {
                        mcdj.ffmpegfound = true;
                    }
                } catch (IOException e) {
                    //Can't find ffmpeg in the path
                    mcdj.ffmpegfound = false;
                }
            }).start();
		}

	public static Map<String, String> getPreInitModTranslationsMap() {
		return getPreInitModTranslationsMap(Minecraft.getInstance().options.languageCode);
	}

	public static Map<String, String> getPreInitModTranslationsMap(String lang) {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		BiConsumer<String, String> biconsumer = builder::put;
		parseTranslations(biconsumer, "assets/mcdj/lang/"+lang+".json");
		return new java.util.HashMap<>(builder.build());
	}

	private static void parseTranslations(BiConsumer<String, String> p_282031_, String lang) {
		try {
			InputStream inputstream = mcdj.class.getClassLoader().getResourceAsStream(lang);
			loadFromJson(inputstream, p_282031_);
		} catch (Exception e1) {
			mcdj.logger.error("Couldn't read strings from MCDJ for that language, fallback to English");
			try {
				InputStream inputstream = mcdj.class.getClassLoader().getResourceAsStream("assets/mcdj/lang/en_us.json");
				loadFromJson(inputstream, p_282031_);
			} catch (Exception e) {
				mcdj.logger.error("Couldn't read strings from MCDJ for default language, MCDJ mod file may be damaged {}", e);
			}
		}

	}

	public static Path ExistPathAppend(Path dest) {
		var ret = dest;
		int i = 1;
		while(ret.toFile().exists() || i > 10000)
			ret = Paths.get(dest.getParent().toString() + "/" + dest.getFileName().toString().substring(0,dest.getFileName().toString().length()-4)+(i++)+".ogg");
		return ret;
	}
}