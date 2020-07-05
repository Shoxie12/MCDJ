package com.shoxie.mcdj;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Lib {
	
	public static String genrandomword(int len) {
        String basestr = "qwertyuiopasdfghjklzxcvbnm"; 
        StringBuilder sb = new StringBuilder(len); 
        for (int i = 0; i < len; i++) {
            int index = (int)(basestr.length()* Math.random()); 
            sb.append(basestr.charAt(index)); 
        } 
        return sb.toString(); 
	}
	
	public static String ToUpperWords(String src) {
		String res = src.substring(0, 1).toUpperCase();
		for (int i = 1; i < src.length(); i++) {
		    if (" ".equals(src.substring(i-1, i)))
		    	res = res + src.substring(i, i+1).toUpperCase();
		    else
		    	res = res + src.substring(i, i+1);
		}
		return res;
	}

	public static void writefile(String filepath ,String sContent)
    {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(filepath))) {
        	bufferedWriter.write(sContent);
        } catch (IOException e) {
        	mcdj.logger.error("Unable to write file: "+filepath+e.getMessage());
		}
    }

	public static String readFile(File file)
	{
		try(FileInputStream inputStream = new FileInputStream(file)) {     
		    return IOUtils.toString(inputStream);
		    
		} catch (FileNotFoundException e) {
			mcdj.logger.error("Unable to read file: "+file+e.getMessage());
			return null;
		} catch (IOException e) {
			mcdj.logger.error("Unable to read file: "+file+e.getMessage());
			return null;
		}
	}
    
	public static String HashMusicDir(File[] lomf) {
		String chk = "";
		for (int i = 0; i < lomf.length; i++) {
			chk = chk + lomf[i].getName()+lomf[i].length();
		}
		return Integer.toString(chk.hashCode());
	}
	  
    
	public static void cleanAll(String respath) {
		try {
			FileUtils.cleanDirectory(Paths.get(respath+"models/item").toFile());
			FileUtils.cleanDirectory(Paths.get(respath+"textures/items").toFile());
		} catch (IOException e1) {
			mcdj.logger.error("Error while cleaning up!"+e1.getMessage());
		} 
		Paths.get(respath+"sounds.json").toFile().delete();
		Paths.get(respath+"lang/en_us.lang").toFile().delete();
    }
    
	public static void createSymLink(String streamingpath) {
		String cwd=mcdj.proxy.getCWD();
	    Path source = Paths.get(cwd+streamingpath);
	    Path symlink = Paths.get(cwd+"Music");
	    
	    //Checking OS
		if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
		    
			//Creating shortcut to streaming folder using powershell (may not work on winxp)
		    String command = "powershell.exe -ExecutionPolicy Bypass -NoLogo -NonInteractive -NoProfile -Command \"$ws = New-Object -ComObject WScript.Shell; $s = $ws.CreateShortcut('"+symlink.toString()+".lnk"+"'); $S.TargetPath = '"+source.toString()+"'; $S.Save()\"";
		    try {
				Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				mcdj.logger.error("Error creating symlink to "+source.toString()+" Please add your music to this folder directly!"+e.getMessage());
			}
			
		}
		else {
			//Creating symlink to streaming folder
			try {
				Files.createSymbolicLink(symlink, source);
			} catch (IOException e) {
				mcdj.logger.error("Error creating symlink to "+source.toString()+" Please add your music to this folder directly!"+e.getMessage());
			}
		}
	}
	
	public static void generateItemJson(String fname, Boolean art, String respath) {
		String name = (art) ? fname : mcdj.DEFAULT_RECORD_TEXTURE;
		String json=
				"{" + 
					"\"parent\": \"item/generated\"," + 
					"\"textures\": {" + 
						"\"layer0\": \""+mcdj.MODID+":items/"+name+"\"" + 
					"}" + 
				"}";
		
		writefile(respath+"models/item/"+fname+".json",json);
	}
	
	public static String appendSounds(String cname, String firstchar) {
		return firstchar +
			"\"" + cname + "\": {" + 
				"\"category\" : \"record\","+ 
				"\"sounds\": [{" + 
					"\"name\": \""+mcdj.MODID+":streaming/" + cname + "\"," + 
					"\"stream\": true" + 
					"}]" + 
				"}";
	}
	
	public static String appendLang(String type, String cname, String name, String desc, String firstchar) {
		String langfile = firstchar+"\n\""+type+"."+mcdj.MODID+"."+cname+"\": \""+name+"\"";

		if (desc!="") 
			langfile = langfile + "," + "\n\""+type+"."+mcdj.MODID+"."+cname+".desc\": \""+desc+"\"";
		
		return langfile;
	}
	
	
	public static Item getVanillaRecord(int num) {
		switch(num)
		{
			case 1: return new ItemStack(Items.MUSIC_DISC_13).getItem();
			case 2: return new ItemStack(Items.MUSIC_DISC_BLOCKS).getItem();
			case 3: return new ItemStack(Items.MUSIC_DISC_CAT).getItem();
			case 4: return new ItemStack(Items.MUSIC_DISC_CHIRP).getItem();
			case 5: return new ItemStack(Items.MUSIC_DISC_FAR).getItem();
			case 6: return new ItemStack(Items.MUSIC_DISC_MALL).getItem();
			case 7: return new ItemStack(Items.MUSIC_DISC_MELLOHI).getItem();
			case 8: return new ItemStack(Items.MUSIC_DISC_STAL).getItem();
			case 9: return new ItemStack(Items.MUSIC_DISC_STRAD).getItem();
			case 10: return new ItemStack(Items.MUSIC_DISC_WAIT).getItem();
			case 11: return new ItemStack(Items.MUSIC_DISC_WARD).getItem();
			case 12: return new ItemStack(Items.field_234775_qK_).getItem();
			default: return new ItemStack(Items.MUSIC_DISC_11).getItem();
		}
	}

	public static boolean ismusic(String s) {
		if(s.contains("OggS")) return true;
		
		return false;
	}

	public static String getfiletype(Path s) {
		String line="";
		try(FileReader fr = new FileReader(s.toFile())) {
            BufferedReader reader = new BufferedReader(fr);
            line = reader.readLine();
            reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		}
		return line;
	}
}
