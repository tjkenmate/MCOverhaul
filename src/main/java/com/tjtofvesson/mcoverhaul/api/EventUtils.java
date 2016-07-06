package com.tjtofvesson.mcoverhaul.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class EventUtils {

	// Variable Initializations
	protected static volatile boolean queueRun = false;
	protected static volatile boolean previousWorldState = false; //true = World exists, false = World doesn't exist
	
	// Object Initializations
	protected static volatile List<Listeners> active = new ArrayList<Listeners>();
	protected static volatile Map<Integer, IGenericCallback> gListeners = new HashMap<Integer, IGenericCallback>();
	private static final MinecraftRunnable swingListener = w->
	{
		if(w==null) return;
		World world = (World) w;
		List l;
		if(world!=null && !gListeners.isEmpty() && !(l=(world.getEntities(EntityPlayer.class, p->p.isSwingInProgress))).isEmpty())
			for(IGenericCallback i : gListeners.values())
				if(i instanceof ISwingCallback) ((ISwingCallback)i).onSwing(l);
	};
	private static final MinecraftRunnable worldListener = w->
	{
		if(previousWorldState!=(Minecraft.getMinecraft().theWorld!=null))
			for(IGenericCallback i : gListeners.values())
				if(i instanceof IWorldCallback) ((IWorldCallback)i).onWorldEvent(Minecraft.getMinecraft().theWorld);
	};
	
	
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
		if(!queueRun)
			new Thread(()->
			{
				while(queueRun)
					if(!active.isEmpty())
						for(Listeners l : active)
							switch(l){
							case WORLD:
								worldListener.run(null);
								break;
							case PLAYER_SWING:
								swingListener.run(Minecraft.getMinecraft().theWorld);
								break;
							}
			}).start();
	}
	public static final void stopListenerQueue(){ queueRun=false; }
	
	// /\/\ Public Methods /\/\
	
	// Other stuff
	
	// Runnable variant
	private interface MinecraftRunnable{ public void run(@Nullable Object o); }
	
	// Listener Options
	public enum Listeners{
		WORLD(0), PLAYER_SWING(1);

		int id;
		Listeners(int id){ this.id = id; }
		int getId(){ return id; }
	}
}
