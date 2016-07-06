package com.tjtofvesson.mcoverhaul.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicReference;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventUtils {

	// Variable Initializations
   //Security	Static Volatility Finality	Type							Name					Value
	protected 	static volatile 			boolean 						queueRun = 				false;
	protected 	static volatile 			boolean 						previousWorldState = 	false; //true = World exists, false = World doesn't exist
	protected	static volatile				boolean							using = 				false;
				static						boolean							isRegistered =			false;
	
	// Object Initializations
   //Security	Static Volatility Finality	Type							Name					Value
	protected 	static volatile 			List<Listeners> 				active = 				new ArrayList<Listeners>();
	protected 	static volatile 			Map<Integer, IGenericCallback> 	gListeners = 			new HashMap<Integer, IGenericCallback>();
	protected	static volatile				List<EntityPlayer>				swungPlayers = 			new ArrayList<EntityPlayer>();
	public		static						EventUtils						context =				new EventUtils();
	private 	static 				final 	MinecraftRunnable 				swingListener = w->
	{
		if(w==null) return;
		World world = (World) w;
		List l = null;
		swungPlayers.removeIf(e->!e.isSwingInProgress);
		if(!gListeners.isEmpty() && !(l=(world.getEntities(EntityPlayer.class, p->{
				boolean b = p.isSwingInProgress && !swungPlayers.contains(p); if(b) swungPlayers.add(p); return b; //Tfw lambda executing instructions in an if-statement
			}))).isEmpty()){
			for(IGenericCallback i : gListeners.values())
				if(i instanceof ISwingCallback) ((ISwingCallback)i).onSwing(l);
		}
	};
   //Security	Static Volatility Finality	Type							Name					Value
	private 	static 				final 	MinecraftRunnable 				worldListener = 		w->
	{
		if(previousWorldState!=(previousWorldState = w!=null))
			for(IGenericCallback i : gListeners.values())
				if(i instanceof IWorldCallback) ((IWorldCallback)i).onWorldEvent((WorldClient)w);
	};
	
	
	
	private EventUtils(){
		if(!isRegistered) MinecraftForge.EVENT_BUS.register(this);
	}
	
	// \/\/ Public Methods \/\/
	
	// Swing Listener handling
	public static boolean registerSwingListener(ISwingCallback callback, Integer UID){
		if(gListeners.keySet().contains(UID)) return false;
		gListeners.put(UID, callback);
		return true;
	}
	
	// World Listener handling
	public static boolean registerWorldListener(IWorldCallback callback, Integer UID){
		if(gListeners.keySet().contains(UID)) return false;
		gListeners.put(UID, callback);
		return true;
	}

	// Generic Listener actions
	public static void removeListener(Integer UID){ if(gListeners.containsKey(UID)) gListeners.remove(UID); }
	public static boolean isListenerRegistered(Integer UID){ return gListeners.keySet().contains(UID); }
	
	// Listener queue actions
	public static void activateListenerInQueue(Listeners l){ if(!active.contains(l)) active.add(l); }
	public static void pauseListenerInQueue(Listeners l){ if(active.contains(l)) active.remove(l); }
	public static List<Listeners> getActiveListenerQueue(){ return active; }
	public static Integer getUnusedUID(){
		int i;
		while(gListeners.containsKey(i = ThreadLocalRandom.current().nextInt())) ;
		return i;
	}
	
	// Main listener queue methods
	public static final void startListenerQueue(){
		queueRun = true;
	}
	@SubscribeEvent
	public void onWorldTick(TickEvent.WorldTickEvent event) {
		if(queueRun && !active.isEmpty())
			for(int i=0; i<active.size(); ++i)
				switch(active.get(i)){
				case WORLD:
					worldListener.run(Minecraft.getMinecraft().theWorld);
					break;
				case PLAYER_SWING:
					swingListener.run(Minecraft.getMinecraft().theWorld);
					break;
				}
	}
	public static final void stopListenerQueue(){ queueRun=false; }
	
	// /\/\ Public Methods /\/\
	
	// Other stuff
	
	// Runnable variant
	private interface MinecraftRunnable{ public default void run(){ run(null); } public void run(@Nullable Object o); }
	
	// Listener Options
	public enum Listeners{
		WORLD(0), PLAYER_SWING(1);

		int id;
		Listeners(int id){ this.id = id; }
		int getId(){ return id; }
	}
}
