package com.shoxie.mcdj.proxy;

import java.io.File;
import net.minecraft.block.Block;

public interface IProxy {
 

    void rpinit(File rp);
	

	void rpreload();
	

	String getCWD();

}
