package com.tjtofvesson.mcoverhaul;

import scala.reflect.internal.Trees.This;

public class Reference {

	public static final String MOD_ID = "ttmco";
	public static final String NAME = "Minecraft Overhaul";
	public static final String VERSION = "0.1 Alpha";
	public static final String MC_VERS = "[1.10,1.10.2,)";
	
	public static final String CLIENT_PROXY_CLASS = "com.tjtofvesson.mcoverhaul.proxy.ClientProxy";
	public static final String SERVER_PROXY_CLASS = "com.tjtofvesson.mcoverhaul.proxy.ServerProxy";
	
	public static enum MCOItemsData{
		TESTITEM("testItem", "ItemTestItem");
		
		private String unlocalizedName;
		private String registryName;
		
		MCOItemsData(String unlocalizedName, String registryName){
			this.unlocalizedName = unlocalizedName;
			this.registryName = registryName;
		}
		
		public String getRegistryName() {
			return registryName;
		}
		
		public String getUnlocalizedName() {
			return unlocalizedName;
		}
	}
	
	public static enum MCOBlocksData{
		PlayerPlate("playerPlate", "PlayerPlate");
		
		private String unlocalizedName;
		private String registryName;
		
		MCOBlocksData(String unlocalizedName, String registryName){
			this.unlocalizedName = unlocalizedName;
			this.registryName = registryName;
		}
		
		public String getRegistryName() {
			return registryName;
		}
		
		public String getUnlocalizedName() {
			return unlocalizedName;
		}
	}
}
