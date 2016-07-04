package com.tjtofvesson.mcoverhaul.proxy;

import com.tjtofvesson.mcoverhaul.init.MCOItems;

public class ClientProxy implements CommonProxy{

	@Override
	public void init() {
		MCOItems.registerRenders();
		
	}
	
	

}
