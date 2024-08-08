package com.shoxie.mcdj.gui;

import com.shoxie.mcdj.entity.MusicGeneratorEntity;
import com.shoxie.mcdj.init.Init;
import com.shoxie.mcdj.networking.MGDiscidUpdPacket;
import com.shoxie.mcdj.networking.Networking;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;

public class MusicGeneratorTextField extends EditBox{

	private final BlockPos pos;
	private final MusicGeneratorEntity en;

	public MusicGeneratorTextField(Font p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_,
								   int p_i232260_5_, Component p_i232260_6_, BlockPos pos, MusicGeneratorEntity en) {
		super(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
		this.pos = pos;
		this.en = en;
	}

	public static boolean isAllowedCharacter(char character) {
		return character >= 48 && character <= 57;
	}
	
	public static boolean isAllowedKey(int k) {
		return (k == 14 || k == 211 || k == 203 || k == 205 || k == 45);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		boolean first = getValue().equals("1");
		if(isAllowedCharacter(typedChar) || isAllowedKey(keyCode)){
			if(super.charTyped(typedChar, keyCode)){
				if(first) this.setValueAndSync("1"+typedChar);
				syncid();
				return true;
			}
		}
		return false;
	}

	@Override
	public void deleteChars(int num) {
		super.deleteChars(num);
		try {
			Integer.parseInt(this.getValue());
		}
		catch(NumberFormatException e) { this.setValueAndSync("1"); return; }
		syncid();
	}

	public void setValueAndSync(String textToWrite) {
		syncid();
		setValue(textToWrite);
	}
	
	@Override
	public void setValue(String textToWrite) {
		try {
			Integer.parseInt(textToWrite);
		}
		catch(NumberFormatException e) { return; }
		super.setValue(textToWrite);
	}

	public void checkValue(){
		if(getValue().isEmpty()) this.setValue("1");
		else if(Integer.parseInt(this.getValue()) > Init.CUSTOM_RECORD_ITEMS.size()) this.setValue(Integer.toString(Init.CUSTOM_RECORD_ITEMS.size()));
		else if(Integer.parseInt(this.getValue()) < 1) this.setValue("1");
	}
		
	private void syncid() {
		if(this.getMessage() != null) {
			checkValue();
			try {
				sendid(Integer.parseInt(this.getValue()));
			}
			catch(NumberFormatException e) { this.setValue("1");
            }
		}
	}

	public void sendid(int val) {
		this.en.discid=val;
    	Networking.sendToServer(new MGDiscidUpdPacket(pos,val-1));
    }
}
