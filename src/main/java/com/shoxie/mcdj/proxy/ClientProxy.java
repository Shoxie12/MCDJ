package com.shoxie.mcdj.proxy;

import java.io.File;
import java.util.function.Consumer;

import com.shoxie.mcdj.Config;
import com.shoxie.mcdj.mcdj;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;
import net.minecraft.resources.data.PackMetadataSection;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ClientProxy implements IProxy {
	 
	@Override
	public void rpinit(File rp) {
        final String id = "mcdj";
        final ITextComponent name = new StringTextComponent("MCDJ Resource Pack");
        final ITextComponent description = new StringTextComponent("Makes possible to load your music automatically");
        final IResourcePack pack = new FolderPack(new File(mcdj.proxy.getCWD()+"/"+Config.GetMcdjPath()+"/"+Config.GetMcdjRootFolderName()));
        final PackMetadataSection PackMetadataSection = new PackMetadataSection(description, 4);
        
        Minecraft.getInstance().getResourcePackList().addPackFinder(new IPackFinder()
        {
			@Override
			public void func_230230_a_(Consumer<ResourcePackInfo> nameToPackMap, IFactory packInfoFactory) {
	            {
	            	nameToPackMap.accept(packInfoFactory.create(id, true, () -> pack, pack, PackMetadataSection, ResourcePackInfo.Priority.BOTTOM, 
	            		
	            		new IPackNameDecorator(){

		        			@Override
		        			public ITextComponent decorate(ITextComponent p_decorate_1_) {
		        				return name;
		        			}
	            	}));
	            }
			}
        });
          this.rpreload();
	}
	
	@Override
	public void rpreload() {
		Minecraft.getInstance().reloadResources();
	}
	
	@Override
	public String getCWD(){
		return Minecraft.getInstance().gameDir.toString()+"/";
	}
	
}