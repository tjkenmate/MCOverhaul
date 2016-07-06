package com.tjtofvesson.mcoverhaul.init;

import com.tjtofvesson.mcoverhaul.Reference;
import com.tjtofvesson.mcoverhaul.block.PlayerPlate;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MCOBlocks {
	
	public static PlayerPlate playerPlate;

	public static void init()
	{
		playerPlate = new PlayerPlate();
	}
	
	public static void register()
	{
		GameRegistry.register(playerPlate);
		ItemBlock i = new ItemBlock(playerPlate);
		i.setRegistryName(playerPlate.getRegistryName());
		GameRegistry.register(i);
	}
	
	public static void registerRenders()
	{
		registerRender(playerPlate);
	}
	
	private static void registerRender(Block block)
	{
		Minecraft.func_71410_x().func_175599_af().func_175037_a().func_178086_a(Item.func_150898_a(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
		
		//More Efficient but non working render
		//ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), 0, new ModelResourceLocation(block.getRegistryName(), "inventory"));
	}
}
