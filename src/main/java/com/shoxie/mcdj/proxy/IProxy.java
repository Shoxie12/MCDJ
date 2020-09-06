package com.shoxie.mcdj.proxy;

import java.io.File;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface IProxy {
 

    void rpinit(File rp);
	

	void rpreload();
	

	String getCWD();


	void ScreenInit();


	World getClientWorld();


	PlayerEntity getClientPlayer();

}
