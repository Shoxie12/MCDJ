package com.shoxie.mcdj;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class ModSoundEvents {

	public static SoundEvent BLANK_RECORD = new SoundEvent(new ResourceLocation(mcdj.MODID,mcdj.DEFAULT_BLANK_RECORD_SOUND)).setRegistryName(mcdj.MODID,mcdj.DEFAULT_BLANK_RECORD_SOUND);
	public static SoundEvent[] RECORDS;
	
}
