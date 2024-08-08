package com.shoxie.mcdj.proxy;

import com.shoxie.mcdj.Lib;
import com.shoxie.mcdj.gui.PlaylistScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

import java.util.Map;

public class ClientProxy implements IProxy {
	
	public Map<String, String> getPreInitModTranslationsMap() {
		return Lib.getPreInitModTranslationsMap();
	}

	@Override
	public void ScreenInit() {
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
				() -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new PlaylistScreen()));
	}

	@Override
	public void checkFFmpeg() {
		Lib.Checkffmpeg();
	}
}