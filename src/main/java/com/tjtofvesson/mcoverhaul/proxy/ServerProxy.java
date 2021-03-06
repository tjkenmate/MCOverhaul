package com.tjtofvesson.mcoverhaul.proxy;

import com.tjtofvesson.mcoverhaul.api.ChatUtils;
import com.tjtofvesson.mcoverhaul.api.EventUtils;
import com.tjtofvesson.mcoverhaul.api.EventUtils.Listeners;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class ServerProxy implements CommonProxy{

	@Override
	public void init() {
		initiateEventHandler();
	}

	public void initiateEventHandler(){
		// Start custom event handler
		System.out.println("Loaded");
		EventUtils.activateListenerInQueue(Listeners.WORLD);
		EventUtils.registerWorldListener(world->
		{
			System.out.println("State changed!");
			if(world!=null){ if(!EventUtils.getActiveListenerQueue().contains(Listeners.PLAYER_SWING)) EventUtils.activateListenerInQueue(Listeners.PLAYER_SWING); }
			else if(EventUtils.getActiveListenerQueue().contains(Listeners.PLAYER_SWING)) EventUtils.pauseListenerInQueue(Listeners.PLAYER_SWING);
			
			
		}, EventUtils.getUnusedUID());
		EventUtils.registerSwingListener(p->
		{
			ChatUtils.sendNoSpamMessages(0, ChatUtils.wrapMessage(p.size()+" players are currently swinging."));
			for(EntityPlayer p1 : p) if(p1.getPosition().getY()>=100) p1.addVelocity(0, -20f, 0);
		}, EventUtils.getUnusedUID());
		EventUtils.startListenerQueue();
	}

}
