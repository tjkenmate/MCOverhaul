package com.tjtofvesson.mcoverhaul.init;

import com.tjtofvesson.mcoverhaul.Reference;
import com.tjtofvesson.mcoverhaul.block.PlayerPlate;
import com.tjtofvesson.mcoverhaul.items.ItemTestItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MCOBlocks {
	
	public static Block playerPlate;

	public static void init()
	{
		playerPlate = new PlayerPlate();
		playerPlate.setCreativeTab(Reference.ttmcoTab);
	}
	
	public static void register()
	{
		GameRegistry.register(playerPlate);
	}
	
	public static void registerRenders()
	{
		registerRender(playerPlate);
	}
	
	private static void registerRender(Block block)
	{
		
		
		//More Efficient but non working render
		//ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
}
