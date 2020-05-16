package com.shoxie.mcdj.proxy;

import java.io.File;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ServerProxy implements IProxy {
	
    @Override
	public void rpinit(File rp) {
	}

	@Override
	public void rpreload() {
	}
	
	@Override
	public String getCWD(){
		return System.getProperty("user.dir")+"/";
	}
}