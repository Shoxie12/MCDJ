package com.shoxie.mcdj.proxy;

import java.io.File;
import java.util.List;

import com.shoxie.mcdj.mcdj;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
	
	@Override
	public World getClientWorld() {
		return Minecraft.getMinecraft().world;
	}
	
	@Override
	public EntityPlayer getClientPlayer() {
		return Minecraft.getMinecraft().player;
	}
	
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        OBJLoader.INSTANCE.addDomain(mcdj.MODID);
    }

    @Override
    public void init(FMLInitializationEvent e) {
        super.init(e);
    }
	
	@Override
	public void renderItem(Item item, int meta, ResourceLocation rl) {
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(rl, "inventory"));
	}
	
	@Override
    public void renderBlock(Block block) {
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(new ItemBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
 
	@Override
	public void rpinit(File rp) {
		List<IResourcePack> defaultResourcePacks = ObfuscationReflectionHelper.getPrivateValue(Minecraft.class, Minecraft.getMinecraft(), "defaultResourcePacks", "field_110449_ao");
		defaultResourcePacks.add(new FolderResourcePack(rp));
		this.rpreload();
	}
	
	@Override
	public void rpreload() {
		Minecraft.getMinecraft().refreshResources();
	}
	
	@Override
	public String getCWD(){
		return Minecraft.getMinecraft().mcDataDir.toString()+"/";
	}
}