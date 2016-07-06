package com.tjtofvesson.mcoverhaul.proxy;

import com.tjtofvesson.mcoverhaul.api.ChatUtils;
import com.tjtofvesson.mcoverhaul.api.EventUtils;
import com.tjtofvesson.mcoverhaul.api.EventUtils.Listeners;
import com.tjtofvesson.mcoverhaul.init.MCOBlocks;
import com.tjtofvesson.mcoverhaul.init.MCOItems;

import net.minecraft.entity.player.EntityPlayer;

public class ClientProxy implements CommonProxy{

	@Override
	public void init() {
		MCOItems.registerRenders();
		MCOBlocks.registerRenders();
	}
	

}
