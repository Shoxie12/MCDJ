package com.shoxie.mcdj.proxy;

import com.shoxie.mcdj.Lib;
import java.util.Map;

public class ServerProxy implements IProxy {

	public Map<String, String> getPreInitModTranslationsMap() {
		return Lib.getPreInitModTranslationsMap("en_us");
	}

	@Override
	public void ScreenInit() {
	}

	@Override
	public void checkFFmpeg() {
	}
}