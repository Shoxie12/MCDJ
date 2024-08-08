package com.shoxie.mcdj.misc;

import java.nio.file.Path;

import com.shoxie.mcdj.init.Init;
import com.shoxie.mcdj.mcdj;

import net.minecraft.sounds.SoundEvent;

public class MusicFile {
    private int number;
    private String displayname;
    private String name;
    private String filename;
    private Path path;
    private boolean deleted;
    private int duration;
    private boolean available_in_mg = true;
    private boolean available_in_sr = true;
    private int chance_in_sr = 100;

    private boolean regisetered;

    public MusicFile(String displayname, String filename, Path path, boolean registered, int number) {
        this(displayname,filename,path,0,registered,number);
    }

    public MusicFile(String displayname, String filename, Path path, int duration, boolean registered, int number) {
        this.displayname = displayname;
        this.name = "record_"+displayname;
        this.filename = filename;
        this.path = path;
        this.regisetered = registered;
        this.number = number;
        this.duration = duration;
    }

    public String getDisplayName(){
        return this.displayname;
    }

    public String getName(){
        return this.name;
    }

    public String getFileName(){
        return this.filename;
    }

    public int getDuration() { return duration; }
    public void setDuration(int d) { this.duration = d; }

    public SoundEvent getSoundEvent(){
        return Init.CUSTOM_RECORD_SOUND_EVENTS.get(number-1).get();
    }

    public boolean isRegistered(){
        return this.regisetered;
    }

    public boolean remove(){
        this.deleted = this.path.toAbsolutePath().toFile().delete();
        mcdj.needrestart = true;
        return this.deleted;
    }

    public boolean isDeleted(){
        return this.deleted;
    }

    public boolean isAvailableForMg(){
        return this.available_in_mg;
    }

    public void setAvailableForMg(boolean val){
        this.available_in_mg = val;
    }

    public void setAvailableForSr(boolean val){
        this.available_in_sr = val;
    }

    public boolean isAvailableForSr(){
        return this.available_in_sr;
    }

    public int getTrackNumber(){
        return this.number;
    }

    public int getChanceToDrop(){
        return this.chance_in_sr;
    }

    public void setChanceToDrop(int val) { this.chance_in_sr = val; }

}