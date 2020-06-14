package com.shoxie.mcdj.proxy;

import java.io.File;

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