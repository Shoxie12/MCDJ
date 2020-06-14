package com.shoxie.mcdj.proxy;

import java.io.File;
import java.util.Map;

import com.shoxie.mcdj.Config;
import com.shoxie.mcdj.mcdj;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.resources.FolderPack;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class ClientProxy implements IProxy {
	 
	@Override
	public void rpinit(File rp) {
        final String id = "mcdj";
        final ITextComponent name = new StringTextComponent("MCDJ Resource Pack");
        final ITextComponent description = new StringTextComponent("Makes possible to load your music automatically");
        final IResourcePack pack = new FolderPack(new File(mcdj.proxy.getCWD()+"/"+Config.GetMcdjPath()+"/"+Config.GetMcdjRootFolderName()));
        Minecraft.getInstance().getResourcePackList().addPackFinder(new IPackFinder()
        {
            @Override
            public <T extends ResourcePackInfo> void addPackInfosToMap(Map<String, T> nameToPackMap, ResourcePackInfo.IFactory<T> packInfoFactory)
            {
                nameToPackMap.put(
                        id,
                        (T) new ClientResourcePackInfo(
                                id, true,
                                () -> pack, name, description, PackCompatibility.COMPATIBLE, ResourcePackInfo.Priority.BOTTOM, true, null, true)
                );
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