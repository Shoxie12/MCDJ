package com.shoxie.mcdj.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.shoxie.mcdj.Config;
import com.shoxie.mcdj.init.Init;
import com.shoxie.mcdj.item.CustomDiscItem;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.item.BlankDiscItem;
import com.shoxie.mcdj.menu.MusicGeneratorMenu;
import com.shoxie.mcdj.networking.MGDiscidUpdPacket;
import com.shoxie.mcdj.networking.MGGenPacket;
import com.shoxie.mcdj.networking.Networking;
import com.shoxie.mcdj.entity.MusicGeneratorEntity;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;


public class MusicGeneratorScreen extends AbstractContainerScreen<MusicGeneratorMenu> {
	private final ResourceLocation GUI = new ResourceLocation(mcdj.MODID, "textures/gui/mg.png");
	private MusicGeneratorTextField mgfw;
	private MusicGeneratorEntity entity;
	public static final int previewslot = 1;
	protected int xSize = 176;
	protected int ySize = 182;
	
	private boolean backward = false;
	private int strlen = 500;
	private int maxtick = 0;
	private int mintick = 0;
	private double tick = 0;
	private int msstart = 0;
	private int msend = 0;
	private int lasttrack = 0;
	private final int maxstrlen = 30;
	private MusicGeneratorMenu menu;

    public MusicGeneratorScreen(MusicGeneratorMenu menu, Inventory inv, Component name) {
        super(menu, inv, name);
        this.entity = menu.getEntity();
        this.menu = menu;
    }
	
    @Override
    public void init() {
        super.init();
        clearWidgets();
        mgfw = new MusicGeneratorTextField(this.font, getGuiLeft() + 110,getGuiTop() + 78,32,10, Component.literal(Integer.toString(entity.discid)), this.menu.getPos(),entity);
        addRenderableWidget(mgfw);

		//Next
		addRenderableWidget(
				Button.builder(Component.literal(" > "),
				(button) ->  {if(!this.menu.isProcessing()) this.setDisc(1);}
				).bounds(getGuiLeft() + 106,getGuiTop() + 46,20,20).build());

		//Prev
		addRenderableWidget(
				Button.builder(Component.literal(" < "),
				(button) -> {if(!this.menu.isProcessing()) this.setDisc(2);}
				).bounds(getGuiLeft() + 39, getGuiTop() + 46, 20, 20).build());

		//Generate
		addRenderableWidget(
				Button.builder(Component.literal(" Generate "),
				this::gendisc
				).bounds(getGuiLeft() + 12, getGuiTop() + 73, 49, 20).build());
	}

	@Override
	protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {
		RenderSystem.setShader(GameRenderer::getPositionTexShader);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
		RenderSystem.setShaderTexture(0, GUI);
		guiGraphics.blit(GUI, this.getGuiLeft(), this.getGuiTop(), 0, 0, this.xSize, this.ySize);
		int igt = this.menu.getGenTime(24);
		if(this.menu.isProcessing()) guiGraphics.blit(GUI, this.getGuiLeft() + 73, this.getGuiTop() + 47, 176, 0, 24 - igt, 17);
		else guiGraphics.blit(GUI, this.getGuiLeft() + 73, this.getGuiTop() + 47, 176, 0, 0, 17);
	}

	public void setDisc(int opt) {
	    switch(opt) {
	    	case 0 : {sendid(); break;}
	    	case 1 : if(entity.discid < Init.CUSTOM_RECORD_ITEMS.size()+1) {++entity.discid; sendid(); break;}
	    	case 2 : if(entity.discid > 1) {--entity.discid; sendid(); break;}
	    }
	    entity.discid = Math.min(entity.discid, Init.CUSTOM_RECORD_ITEMS.size());
		mgfw.setValue(Integer.toString(entity.discid));
    }
    
    @Override
    public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
    	if(this.isHovering(this.menu.getSlot(previewslot).x,this.menu.getSlot(previewslot).y, 16, 16, p_231044_1_, p_231044_3_)) return true;
    	return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
    }
    
    private void sendid() {
    	Networking.sendToServer(new MGDiscidUpdPacket(menu.getPos(),entity.discid-1));
    }
    
    private void gendisc(Button button) {
    	if(this.menu.getSlot(0).getItem().getItem() instanceof BlankDiscItem)
    		Networking.sendToServer(new MGGenPacket(this.menu.getPos()));
    }


    @Override
	public void render(GuiGraphics guigraphics, int X, int Y, float p_281886_) {
        super.render(guigraphics, X, Y, p_281886_);
        this.renderTooltip(guigraphics, X, Y);
        
    }
	
	@Override
    protected void renderLabels(GuiGraphics guiGraphics, int X, int Y) {
		if(this.menu.getSlot(previewslot).getItem().getItem() instanceof CustomDiscItem mdi) {
            String str = "";
            str = (Config.isTrackNumbersEnabled() ? mdi.getTrackId() + ". " + str : "") + Component.translatable(mdi.getDescriptionId() + ".desc").getString();
			if(str.length() > maxstrlen)
				drawMovingString(guiGraphics, str);
			else
				guiGraphics.drawString(this.font,str, 8, 5, 0xffffff);
		}
    }

	private void drawMovingString(GuiGraphics guiGraphics, String sname) {
		strlen = sname.length();
        if(strlen > maxstrlen) {
        	maxtick = strlen*10 + 2;
        	mintick = maxtick - (maxtick - maxstrlen * 10);
	        if(lasttrack != strlen) {
	        	tick = maxtick;
	    		lasttrack = strlen;
	        }
			backward = !(tick >= maxtick) && (tick <= mintick || backward);
	        msstart = (strlen - (int)tick / 10);
	        msend = msstart+maxstrlen;
	        if(msend > strlen || msend < 0 || msend < msstart) {msend = strlen;}
	        tick = tick < mintick ? mintick : tick > maxtick ? maxtick : backward ? tick+0.5 : tick-0.5;

            String s = (" "+sname+" ").substring(msstart, msend+1);
			guiGraphics.drawString(this.font, s, 5, 5, 0xffffff);
        }
	}
}