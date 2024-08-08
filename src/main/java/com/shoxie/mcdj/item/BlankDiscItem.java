package com.shoxie.mcdj.item;

import com.shoxie.mcdj.init.Init;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.RecordItem;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

public class BlankDiscItem extends RecordItem {
	public static String name = "blank_record";
	public BlankDiscItem() {
		super(14, Init.BLANK_RECORD_SOUND_EVENT,(new Item.Properties()).stacksTo(1), 40);
	}
}