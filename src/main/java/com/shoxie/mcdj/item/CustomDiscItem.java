package com.shoxie.mcdj.item;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CustomDiscItem extends MusicDiscItem {
	public String name;
	private int soundid;
	public CustomDiscItem(String _name, SoundEvent snd, int soundid) {
		super(0, snd,new Item.Properties().group(ItemGroup.MISC).maxStackSize(1));
		name = "record_"+_name;
		setRegistryName(name);
		this.soundid = soundid;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
	    tooltip.add(this.getRecordDescriptionWithId().applyTextStyle(TextFormatting.GRAY));
	}
	
	@OnlyIn(Dist.CLIENT)
	public ITextComponent getRecordDescriptionWithId() {
	    return new StringTextComponent(soundid+". " + new TranslationTextComponent(this.getTranslationKey() + ".desc").getString());
	}
}