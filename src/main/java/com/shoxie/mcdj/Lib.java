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

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class Lib {
	
	protected static String genrandomword(int len) {
        String basestr = "qwertyuiopasdfghjklzxcvbnm"; 
        StringBuilder sb = new StringBuilder(len); 
        for (int i = 0; i < len; i++) {
            int index = (int)(basestr.length()* Math.random()); 
            sb.append(basestr.charAt(index)); 
        } 
        return sb.toString(); 
	}
	
	protected static String ToUpperWords(String src) {
		String res = src.substring(0, 1).toUpperCase();
		for (int i = 1; i < src.length(); i++) {
		    if (" ".equals(src.substring(i-1, i)))
		    	res = res + src.substring(i, i+1).toUpperCase();
		    else
		    	res = res + src.substring(i, i+1);
		}
		return res;
	}

    protected static void writefile(String filepath ,String sContent)
    {
        try (BufferedWriter bufferedWriter = Files.newBufferedWriter(Paths.get(filepath))) {
        	bufferedWriter.write(sContent);
        } catch (IOException e) {
        	mcdj.logger.error("Unable to write file: "+filepath+e.getMessage());
		}
    }

    protected static String readFile(File file)
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
		for (int i = 0; i < lomf.length; i++) 
			chk = chk + lomf[i].getName()+lomf[i].length();
		
		
		chk = chk + (Config.hires_texture ? 1 : 0) + (Config.albumarts ? 1 : 0);
		return Integer.toString(chk.hashCode());
	}
    
    protected static void cleanAll(String respath) {
		try {
			FileUtils.cleanDirectory(Paths.get(respath+"models/item").toFile());
			FileUtils.cleanDirectory(Paths.get(respath+"textures/items").toFile());
		} catch (IOException e1) {
			mcdj.logger.error("Error while cleaning up!"+e1.getMessage());
		} 
		Paths.get(respath+"sounds.json").toFile().delete();
		Paths.get(respath+"lang/en_us.lang").toFile().delete();
    }
    
	protected static void createSymLink(String streamingpath) {
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
	
    protected static void generateItemJson(String fname, Boolean art, String respath) {
    	String name = (art) ? fname : Config.hires_texture ? mcdj.DEFTEXTURE : mcdj.DEFTEXTURE16;
		String json=
				"{" + 
					"\"parent\": \"item/generated\"," + 
					"\"textures\": {" + 
						"\"layer0\": \""+mcdj.MODID+":items/"+name+"\"" + 
					"}" + 
				"}";
		
		writefile(respath+"models/item/"+fname+".json",json);
	}
	
	protected static String appendSounds(String cname, String firstchar) {
		return firstchar +
			"\"" + cname + "\": {" + 
				"\"category\" : \"record\","+ 
				"\"sounds\": [{" + 
					"\"name\": \""+mcdj.MODID+":streaming/" + cname + "\"," + 
					"\"stream\": true" + 
					"}]" + 
				"}";
	}
	
	protected static String appendLang(String type, String cname, String name, String desc, Boolean isrecord) {
		String langfile = type+"."+cname+".name="+name+"\n";
		
		if (isrecord) 
			langfile = langfile + type+".record."+cname+".desc="+desc+"\n";
		else if (desc!="") 
			langfile = langfile + type+"."+cname+".desc="+desc+"\n";
		
		return langfile;
	}
	
	
	public static Item getVanillaRecord(int num) {
		switch(num)
		{
			case 1: return new ItemStack(Items.RECORD_13).getItem();
			case 2: return new ItemStack(Items.RECORD_BLOCKS).getItem();
			case 3: return new ItemStack(Items.RECORD_CAT).getItem();
			case 4: return new ItemStack(Items.RECORD_CHIRP).getItem();
			case 5: return new ItemStack(Items.RECORD_FAR).getItem();
			case 6: return new ItemStack(Items.RECORD_MALL).getItem();
			case 7: return new ItemStack(Items.RECORD_MELLOHI).getItem();
			case 8: return new ItemStack(Items.RECORD_STAL).getItem();
			case 9: return new ItemStack(Items.RECORD_STRAD).getItem();
			case 10: return new ItemStack(Items.RECORD_WAIT).getItem();
			case 11: return new ItemStack(Items.RECORD_WARD).getItem();
			default: return new ItemStack(Items.RECORD_11).getItem();
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
