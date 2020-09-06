package com.shoxie.mcdj.proxy;

import java.io.File;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

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

	@Override
	public void ScreenInit() {
	}

	@Override
	public World getClientWorld() {
		return null;
	}
	
	@Override
	public PlayerEntity getClientPlayer() {
		return null;
	}

}