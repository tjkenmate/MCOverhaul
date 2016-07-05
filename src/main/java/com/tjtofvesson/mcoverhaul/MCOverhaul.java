/**
 * 
 */
package com.tjtofvesson.mcoverhaul;

import com.tjtofvesson.mcoverhaul.init.MCOBlocks;
import com.tjtofvesson.mcoverhaul.init.MCOItems;
import com.tjtofvesson.mcoverhaul.proxy.CommonProxy;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

/**
 * @author TjKenMate, Blynd3
 *
 */

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, acceptedMinecraftVersions = Reference.MC_VERS)

public class MCOverhaul {

	@Instance
	public static MCOverhaul instance;
	
	@SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.SERVER_PROXY_CLASS)
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		System.out.println("HAHAH PRE");
		
		MCOItems.init();
		MCOBlocks.init();
		MCOItems.register();
		MCOBlocks.register();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		System.out.println("HAHAH PRE");
		proxy.init();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event)
	{
		System.out.println("HAHAH PRE");
	}
}
