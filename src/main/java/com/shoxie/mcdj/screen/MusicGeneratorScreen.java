package com.shoxie.mcdj.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.container.MusicGeneratorContainer;
import com.shoxie.mcdj.gui.MusicGeneratorTextField;
import com.shoxie.mcdj.item.BlankDiscItem;
import com.shoxie.mcdj.networking.MGDiscidUpdPacket;
import com.shoxie.mcdj.networking.MGGenPacket;
import com.shoxie.mcdj.networking.Networking;
import com.shoxie.mcdj.tile.MusicGeneratorTile;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MusicGeneratorScreen extends ContainerScreen<MusicGeneratorContainer> {
	private ResourceLocation GUI = new ResourceLocation(mcdj.MODID, "textures/gui/mg.png");
	private MusicGeneratorTextField mgfw;
	private MusicGeneratorTile tile;
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
	
    public MusicGeneratorScreen(MusicGeneratorContainer container, PlayerInventory inv, ITextComponent name) {
        super(container, inv, name);
        this.tile = container.getTile();
        
    }
	
    @Override
    public void init() {
        super.init();
        buttons.clear();
        mgfw = new MusicGeneratorTextField(this.font, guiLeft + 110,guiTop + 78,32,10,Integer.toString(tile.discid), tile, this);
        addButton(mgfw);

        addButton(new Button(guiLeft + 106, guiTop + 46, 20, 20, new TranslationTextComponent(" > ").getFormattedText(), (button) ->  {if(!this.container.isProcessing()) this.setDisc(1);}));
        addButton(new Button(guiLeft + 39, guiTop + 46, 20, 20, new TranslationTextComponent(" < ").getFormattedText(), (button) -> {if(!this.container.isProcessing()) this.setDisc(2);}));
        addButton(new Button(guiLeft + 12, guiTop + 73, 49, 20, new TranslationTextComponent("gui.mcdj.genbtn").getFormattedText(), (button) -> gendisc()));
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
    
    @Override
    public boolean mouseClicked(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
    	if(this.isPointInRegion(this.container.getSlot(previewslot).xPos,this.container.getSlot(previewslot).yPos, 16, 16, p_231044_1_, p_231044_3_)) return true;
    	return super.mouseClicked(p_231044_1_, p_231044_3_, p_231044_5_);
    }
    
    private void sendid() {
    	Networking.INSTANCE.sendToServer(new MGDiscidUpdPacket(container.getPos(), tile.discid));
    }
    
    private void gendisc() {
    	if(this.container.getSlot(0).getStack().getItem() instanceof BlankDiscItem)
    		Networking.INSTANCE.sendToServer(new MGGenPacket(container.getPos()));
    }


	@Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
	

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    	GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(GUI);
        this.blit(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        int i = ((MusicGeneratorContainer)this.container).getGenTime(24);
        if(this.container.isProcessing()) this.blit(this.guiLeft + 73, this.guiTop + 47, 176, 0, 24 - i, 17);
        else this.blit(this.guiLeft + 73, this.guiTop + 47, 176, 0, 0, 17);
    }
	
	@Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if(this.container.getSlot(previewslot).getStack().getItem() instanceof MusicDiscItem) {
			MusicDiscItem mdi = (MusicDiscItem) this.container.getSlot(previewslot).getStack().getItem();
			String str = mdi.getRecordDescription().getString();
			if(str.length() > maxstrlen)
				drawMovingString(str);
			else
				this.font.drawString(str, 8, 5, 0xffffff);
		}
		else if(!this.container.isProcessing()){
			setDisc(0);
		}
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
	        this.font.drawString(s, 10, 5, 0xffffff);
        }
		
	}
}