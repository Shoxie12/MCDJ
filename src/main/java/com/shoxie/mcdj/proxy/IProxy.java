package com.shoxie.mcdj.proxy;

import net.minecraft.client.Minecraft;

import java.util.Map;

public interface IProxy {

	Map<String, String> getPreInitModTranslationsMap();

	void ScreenInit();

	void checkFFmpeg();

	
}
