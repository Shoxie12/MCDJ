package com.shoxie.mcdj.proxy;

import java.io.File;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.FolderResourcePack;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class ClientProxy extends CommonProxy {
	
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