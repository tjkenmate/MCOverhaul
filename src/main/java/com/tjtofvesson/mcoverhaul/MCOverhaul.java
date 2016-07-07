/**
 * 
 */
package com.tjtofvesson.mcoverhaul;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.sun.jna.platform.win32.Winspool.PRINTER_INFO_1;
import com.tjtofvesson.mcoverhaul.api.ChatUtils;
import com.tjtofvesson.mcoverhaul.api.EventUtils;
import com.tjtofvesson.mcoverhaul.api.EventUtils.Listeners;
import com.tjtofvesson.mcoverhaul.api.IWorldCallback;
import com.tjtofvesson.mcoverhaul.init.MCOBlocks;
import com.tjtofvesson.mcoverhaul.init.MCOItems;
import com.tjtofvesson.mcoverhaul.proxy.CommonProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.Profiler;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		MinecraftForge.EVENT_BUS.register(this);
		
		System.out.println("[Anonymous Memester] Why, hello there! I hope you have a good day!");
		
		MCOItems.init();
		MCOBlocks.init();
		MCOItems.register();
		MCOBlocks.register();
		
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
	public void onServerChatEvent(ServerChatEvent e){
		if(e.getMessage().equals("On")){
			EventUtils.startListenerQueue();
			System.out.println("Activated!");
		}
		else if(e.getMessage().equals("Off")){
			EventUtils.stopListenerQueue();
			System.out.println("Deactivated!");
		}else if(e.getMessage().equals("Stat")){
			String s = "";
			for(Listeners l1 : EventUtils.getActiveListenerQueue()) s+=l1.name()+" ";
			ChatUtils.sendNoSpamMessages(12345, ChatUtils.wrapMessage(s));
		}
	}
	
	private class LelWorld extends WorldClient{

		public LelWorld(NetHandlerPlayClient netHandler, WorldSettings settings, int dimension,
				EnumDifficulty difficulty, Profiler profilerIn) {
			super(netHandler, settings, dimension, difficulty, profilerIn);
		}

	}
	
}
