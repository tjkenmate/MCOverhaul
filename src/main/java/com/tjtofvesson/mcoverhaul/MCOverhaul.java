/**
 * 
 */
package com.tjtofvesson.mcoverhaul;

import com.tjtofvesson.mcoverhaul.api.ChatUtils;
import com.tjtofvesson.mcoverhaul.api.EventUtils;
import com.tjtofvesson.mcoverhaul.api.EventUtils.Listeners;
import com.tjtofvesson.mcoverhaul.api.IWorldCallback;
import com.tjtofvesson.mcoverhaul.init.MCOBlocks;
import com.tjtofvesson.mcoverhaul.init.MCOItems;
import com.tjtofvesson.mcoverhaul.proxy.CommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * @author TjKenMate, Blynd3
 *
 */

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.MC_VERS)
public class MCOverhaul {

	@Instance(value = Reference.MOD_ID)
	public static MCOverhaul instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		
		System.out.println("[Anonymous Memester] Why, hello there! I hope you have a good day!");
		
		MCOItems.init();
		MCOBlocks.init();
		MCOItems.register();
		MCOBlocks.register();
		
		EventUtils.activateListenerInQueue(Listeners.WORLD);
		EventUtils.registerWorldListener(world->
		{
			if(world!=null){ if(!EventUtils.getActiveListenerQueue().contains(Listeners.PLAYER_SWING)) EventUtils.activateListenerInQueue(Listeners.PLAYER_SWING); }
			else if(EventUtils.getActiveListenerQueue().contains(Listeners.PLAYER_SWING)) EventUtils.pauseListenerInQueue(Listeners.PLAYER_SWING);
			
			
		}, EventUtils.getUnusedUID());
		EventUtils.registerSwingListener(p->ChatUtils.sendNoSpamMessages(0, ChatUtils.wrapMessage(p.size()+" players are currently swinging.")), EventUtils.getUnusedUID());
		EventUtils.startListenerQueue();
		
		// Crafting
		
		GameRegistry.addSmelting(new ItemStack(Blocks.STONE_PRESSURE_PLATE), new ItemStack(MCOBlocks.playerPlate), 0.2f);
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		
	}
	
	@SubscribeEvent
	public void ServerChatEvent(String s, String s1, EntityPlayer p, String s2){
		if(s1.equals("On")){
			EventUtils.startListenerQueue();
			System.out.println("Activated!");
		}
		else if(s1.equals("Off")){
			EventUtils.stopListenerQueue();
			System.out.println("Deactivated!");
		}
	}
}
