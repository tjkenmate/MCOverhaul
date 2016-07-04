package com.tjtofvesson.mcoverhaul.items;

import com.tjtofvesson.mcoverhaul.Reference;
import net.minecraft.item.Item;

public class ItemTestItem extends Item {
	
	public ItemTestItem() {
		setUnlocalizedName(Reference.MCOItemsData.TESTITEM.getUnlocalizedName());
		setRegistryName(Reference.MCOItemsData.TESTITEM.getRegistryName());
	}
	
}
