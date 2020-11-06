package com.shoxie.mcdj.gui;

import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.screen.MusicGeneratorScreen;
import com.shoxie.mcdj.tile.MusicGeneratorTile;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.TextFieldWidget;

public class MusicGeneratorTextField extends TextFieldWidget {

	private MusicGeneratorTile tile;
	private MusicGeneratorScreen scr;

	public MusicGeneratorTextField(FontRenderer p_i232260_1_, int p_i232260_2_, int p_i232260_3_, int p_i232260_4_,
			int p_i232260_5_, String p_i232260_6_, MusicGeneratorTile tile, MusicGeneratorScreen scr) {
		super(p_i232260_1_, p_i232260_2_, p_i232260_3_, p_i232260_4_, p_i232260_5_, p_i232260_6_);
		this.tile = tile;
		this.scr = scr;
	}

	public static boolean isAllowedCharacter(char character) {
		return ((character >= 48 && character <= 57) || (character >= 96 && character <= 105)) 
				&& character != 167 && character >= ' ' && character != 127;
	}
	
	public static boolean isAllowedKey(int k) {
		return (k == 14 || k == 211 || k == 203 || k == 205 || k == 45);
	}

	@Override
	public boolean charTyped(char typedChar, int keyCode) {
		if(isAllowedCharacter(typedChar) || isAllowedKey(keyCode))
			return super.charTyped(typedChar, keyCode);
		return false;
	}

	@Override
	public void deleteFromCursor(int num) {
		super.deleteFromCursor(num);
		syncid();
		scr.setDisc(0);
	}
	
	@Override
	public void writeText(String textToWrite) {
		if(tile.isProcessing()){ return; }
		try {
			Integer.parseInt(textToWrite);
		}
		catch(NumberFormatException e) { return; }
		super.writeText(textToWrite);
		syncid();
		scr.setDisc(0);
	}
	
	private void syncid() {
		int i = 0;
		if(this.getText() != null)
			if(!this.getText().isEmpty()) 
				i = Integer.parseInt(this.getText());
		if(i > ModItems.RECORDS.length) { i = ModItems.RECORDS.length-1; this.setText(Integer.toString(i));}
		tile.discid = i;
	}
}
