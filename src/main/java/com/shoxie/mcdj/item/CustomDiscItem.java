package com.shoxie.mcdj.item;

import com.shoxie.mcdj.Config;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.RegistryObject;

public class CustomDiscItem extends RecordItem {
	public String name;
	public RegistryObject<SoundEvent> snd;
	private final int id;
	public CustomDiscItem(String _name, RegistryObject<SoundEvent> _snd, int duration, int id) {
		super(14, _snd,(new Item.Properties()).stacksTo(1),duration * 20);
		snd = _snd;
		name = _name;
		this.id = id;
	}
	
 	@Override
	
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
	     tooltip.add(Component.literal(
				 (Config.isTrackNumbersEnabled() ? id + ". " : "") + Component.translatable(this.getDescriptionId() + ".desc").getString()).withStyle(ChatFormatting.GRAY));
	}

	public int getTrackId() {
		return this.id;
	}
}