package com.shoxie.mcdj.gui;

import java.io.IOException;

import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.container.ContainerMusicGenerator;
import com.shoxie.mcdj.item.ItemBlankRecord;
import com.shoxie.mcdj.networking.MGDiscidUpdPacket;
import com.shoxie.mcdj.networking.MGGenPacket;
import com.shoxie.mcdj.networking.Networking;
import com.shoxie.mcdj.tile.TileMusicGenerator;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;

public class GuiMusicGenerator extends GuiContainer {

	private ResourceLocation GUI = new ResourceLocation(mcdj.MODID, "textures/gui/mg.png");
	private TextFieldMusicGenerator mgfw;
	private TileMusicGenerator tile;
	private ContainerMusicGenerator container;
	public static final int previewslot = 1;
	protected int xSize = 176;
	protected int ySize = 182;
	private int strlen = 500;
	private int maxtick = strlen*50;
	private int tick = 0;
	private int msstart = 0;
	private int msend = 0;
	private int lasttrack = 0;
	private final int maxstrlen = 30;
	
    public GuiMusicGenerator(TileMusicGenerator tile, ContainerMusicGenerator container) {
        super(container);
        this.tile = tile;
        this.container = container;
    }

    @Override
    public void initGui() {
        super.initGui();
        
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(11, guiLeft + 106, guiTop + 46, 20, 20, " > "));
        this.buttonList.add(new GuiButton(12, guiLeft + 39, guiTop + 46, 20, 20, " < "));
        this.buttonList.add(new GuiButton(13, guiLeft + 12, guiTop + 73, 49, 20, new TextComponentTranslation("gui.mcdj.genbtn").getFormattedText()));
        mgfw = new TextFieldMusicGenerator(1, this.fontRenderer, guiLeft + 110, guiTop + 78, 32, 10, tile, this);
        mgfw.setCanLoseFocus(true);
        mgfw.setEnabled(true);
    }
    
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
     this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        mgfw.drawTextBox();
        renderHoveredToolTip(mouseX, mouseY);
    }
	
    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException
    {
        if (!mgfw.textboxKeyTyped(typedChar, keyCode))
            super.keyTyped(typedChar, keyCode);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException
    {
    	if(this.isPointInRegion(this.container.getSlot(previewslot).xPos,this.container.getSlot(previewslot).yPos, 16, 16, mouseX, mouseY)) return;
    	super.mouseClicked(mouseX, mouseY, mouseButton);
    	mgfw.mouseClicked(mouseX - this.guiLeft, mouseY - this.guiTop, mouseButton);
    	mgfw.setFocused(true);
    }
    
    @Override
    protected void actionPerformed(GuiButton button) {
    	switch (button.id) {
    	
	    	case 11: {
	    		if(!this.container.isProcessing()) this.setDisc(1); break;
	    		}
	    	
	    	case 12: {
	    		if(!this.container.isProcessing()) this.setDisc(2); break;
	    		}
	    	
	    	case 13: {
	    		gendisc(); break;
	        }
    	}
    }
    
    public void setDisc(int opt) {
	    switch(opt) {
    		case 0 : {sendid(); break;}
	    	case 1 : if(tile.discid < ModItems.RECORDS.length) {++tile.discid; sendid(); break;}
	    	case 2 : if(tile.discid > 0) {--tile.discid; sendid(); break;}
	    }
	    tile.discid = tile.discid >= ModItems.RECORDS.length ? tile.discid = ModItems.RECORDS.length-1 : tile.discid;
    	Slot s = this.container.getSlot(previewslot);
    	if(s.getHasStack()) s.onSlotChange(s.getStack(), ItemStack.EMPTY);
    	s.putStack(new ItemStack(ModItems.RECORDS[tile.discid]));
    	mgfw.setText(Integer.toString(tile.discid));
    }
    
    private void sendid() {
    	Networking.INSTANCE.sendToServer(new MGDiscidUpdPacket(container.getPos(), tile.discid));
    }
    
    private void gendisc() {
    	if(this.container.getSlot(0).getStack().getItem() instanceof ItemBlankRecord)
    		Networking.INSTANCE.sendToServer(new MGGenPacket(container.getPos()));
    }
     
    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {		
		if(this.container.getSlot(previewslot).getStack().getItem() instanceof ItemRecord) {
			ItemRecord mdi = (ItemRecord) this.container.getSlot(previewslot).getStack().getItem();
			String str = mdi.getRecordNameLocal();
			if(str.length() > maxstrlen)
				drawMovingString(str);
			else
				this.drawString(this.fontRenderer,str, 8, 5, 0xffffff);
		}
		else if(!this.container.isProcessing()) setDisc(0);
	}

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    	GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(GUI);
        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        int i = ((ContainerMusicGenerator)this.container).getGenTime(24);
        if(this.container.isProcessing()) this.drawTexturedModalRect(this.guiLeft + 73, this.guiTop + 47, 176, 0, 24 - i, 17);
        else this.drawTexturedModalRect(this.guiLeft + 73, this.guiTop + 47, 176, 0, 0, 17);
    }
    
	private void drawMovingString(String sname) {
		strlen = sname.length();
        if(strlen > maxstrlen) {
        	maxtick = (strlen)*50;
	        if(lasttrack != strlen) {
	        	tick = maxtick;
	    		lasttrack = strlen;
	        }
			
	        msstart = (strlen - tick / 50);
	        if(msstart > strlen-1 || msstart < 0) {msstart = 0; maxtick = (strlen)*50; tick = maxtick;}
	        msend = msstart+maxstrlen;
	        if(msend > strlen || msend < 0 || msend < msstart) {msend = strlen;}
	        tick = (tick < 1 ? maxtick : tick-1);
	        if(msstart > msend-4) tick = maxtick;
	        
	        if(msstart < 0) msstart = 0;
	        if(msend < 0) msend = 0;
	
	        String s = (" "+sname+" ").substring(msstart < 0 ? 0 : msstart, msend < 0 ? 0 : msstart > msend ? msstart : msend+1);
	        this.drawString(this.fontRenderer, s, 10, 5, 0xffffff);
        }
		
	}
}
