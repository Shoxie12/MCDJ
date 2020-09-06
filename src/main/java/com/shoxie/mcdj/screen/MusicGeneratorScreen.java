package com.shoxie.mcdj.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class MusicGeneratorScreen extends ContainerScreen<MusicGeneratorContainer> {
	private ResourceLocation GUI = new ResourceLocation(mcdj.MODID, "textures/gui/mg.png");
	private MusicGeneratorTextField mgfw;
	private MusicGeneratorTile tile;
	public static final int previewslot = 1;
	/** The X size of the inventory window in pixels. */
	protected int xSize = 176;
	/** The Y size of the inventory window in pixels. */
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
    public void func_231160_c_() {
        super.func_231160_c_();
        field_230710_m_.clear();
        mgfw = new MusicGeneratorTextField(this.field_230712_o_, guiLeft + 110,guiTop + 78,32,10,new StringTextComponent(Integer.toString(tile.discid)), tile, this);
        func_230480_a_(mgfw);
        
        func_230480_a_(new Button(guiLeft + 106, guiTop + 46, 20, 20, new TranslationTextComponent(" > "), (button) ->  {if(!this.container.isProcessing()) this.setDisc(1);}));
        func_230480_a_(new Button(guiLeft + 39, guiTop + 46, 20, 20, new TranslationTextComponent(" < "), (button) -> {if(!this.container.isProcessing()) this.setDisc(2);}));
        func_230480_a_(new Button(guiLeft + 12, guiTop + 73, 49, 20, new TranslationTextComponent(" Generate "), (button) -> gendisc()));
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
    public boolean func_231044_a_(double p_231044_1_, double p_231044_3_, int p_231044_5_) {
    	if(this.isPointInRegion(this.container.getSlot(previewslot).xPos,this.container.getSlot(previewslot).yPos, 16, 16, p_231044_1_, p_231044_3_)) return true;
    	return super.func_231044_a_(p_231044_1_, p_231044_3_, p_231044_5_);
    }
    
    private void sendid() {
    	Networking.INSTANCE.sendToServer(new MGDiscidUpdPacket(container.getPos(), tile.discid));
    }
    
    private void gendisc() {
    	if(this.container.getSlot(0).getStack().getItem() instanceof BlankDiscItem)
    		Networking.INSTANCE.sendToServer(new MGGenPacket(container.getPos()));
    }


    @Override
    public void func_230430_a_(MatrixStack p_230430_1_, int p_230430_2_, int p_230430_3_, float p_230430_4_) {
        this.func_230446_a_(p_230430_1_);
        super.func_230430_a_(p_230430_1_, p_230430_2_, p_230430_3_, p_230430_4_);
        this.func_230459_a_(p_230430_1_,p_230430_2_, p_230430_3_);
        
    }
	

    @Override
    protected void func_230450_a_(MatrixStack p_230450_1_, float p_230450_2_, int p_230450_3_, int p_230450_4_) {
    	GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.field_230706_i_.getTextureManager().bindTexture(GUI);
        this.func_238474_b_(p_230450_1_, this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
        int i = ((MusicGeneratorContainer)this.container).getGenTime(24);
        if(this.container.isProcessing()) this.func_238474_b_(p_230450_1_, this.guiLeft + 73, this.guiTop + 47, 176, 0, 24 - i, 17);
        else this.func_238474_b_(p_230450_1_, this.guiLeft + 73, this.guiTop + 47, 176, 0, 0, 17);
    }
	
	@Override
    protected void func_230451_b_(MatrixStack p_230450_1_, int mouseX, int mouseY) {
		if(this.container.getSlot(previewslot).getStack().getItem() instanceof MusicDiscItem) {
			MusicDiscItem mdi = (MusicDiscItem) this.container.getSlot(previewslot).getStack().getItem();
			String str = mdi.func_234801_g_().getString();
			if(str.length() > maxstrlen)
				drawMovingString(p_230450_1_, str);
			else
				this.field_230712_o_.func_238405_a_(p_230450_1_,str, 8, 5, 0xffffff);
		}
		else if(!this.container.isProcessing()){
			setDisc(0);
		}
    }

	private void drawMovingString(MatrixStack p_230450_1_, String sname) {
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
	        this.field_230712_o_.func_238405_a_(p_230450_1_, s, 10, 5, 0xffffff);
        }
		
	}
}