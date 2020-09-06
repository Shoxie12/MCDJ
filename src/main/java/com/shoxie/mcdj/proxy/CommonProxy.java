package com.shoxie.mcdj.proxy;

import java.io.File;

import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.networking.Networking;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

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

    public void preInit(FMLPreInitializationEvent e) {
        Networking.registerMessages("mcdj");
    }

    public void init(FMLInitializationEvent e) {
        NetworkRegistry.INSTANCE.registerGuiHandler(mcdj.instance, new GuiProxy());
    }
    
	public World getClientWorld() {
		return null;
	}
	
	public EntityPlayer getClientPlayer() {
		return null;
	}

}