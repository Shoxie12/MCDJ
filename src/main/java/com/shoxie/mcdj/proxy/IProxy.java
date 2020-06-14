package com.shoxie.mcdj.proxy;

import java.io.File;

public interface IProxy {
 

    void rpinit(File rp);
	

	void rpreload();
	

	String getCWD();

}
