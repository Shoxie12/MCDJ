package com.shoxie.mcdj.proxy;

import java.io.File;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class CommonProxy {
	
	public void renderItem(Item item, int meta, ResourceLocation rl) {
	}
	
    public void renderBlock(Block block) {
    }
	
	public void rpinit(File rp) {
	}

	public void rpreload() {
	}

	public String getCWD(){
		return System.getProperty("user.dir")+"/";
	}

}