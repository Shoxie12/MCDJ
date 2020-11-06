package com.shoxie.mcdj.gui;

import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.tile.TileMusicGenerator;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiTextField;

public class TextFieldMusicGenerator extends GuiTextField{

	private TileMusicGenerator tile;
	private GuiMusicGenerator scr;
	public TextFieldMusicGenerator(int componentId, FontRenderer fontrendererObj, int x, int y, int par5Width,
			int par6Height, TileMusicGenerator tile, GuiMusicGenerator scr) {
		super(componentId, fontrendererObj, x, y, par5Width, par6Height);
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
	public boolean textboxKeyTyped(char typedChar, int keyCode) {
		if(isAllowedCharacter(typedChar) || isAllowedKey(keyCode))
			return super.textboxKeyTyped(typedChar, keyCode);
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
