package com.tjtofvesson.mcoverhaul.init;

import com.tjtofvesson.mcoverhaul.Reference;
import com.tjtofvesson.mcoverhaul.items.ItemTestItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MCOItems {
	
	public static Item testItem;

	public static void init()
	{
		testItem = new ItemTestItem();
	}
	
	public static void register()
	{
		GameRegistry.register(testItem);
	}
	
	public static void registerRenders()
	{
		registerRender(testItem);
	}
	
	private static void registerRender(Item item)
	{
		Minecraft.func_71410_x().func_175599_af().func_175037_a().func_178086_a(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
		//More Efficient but non working render
		//ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}
