package com.tjtofvesson.mcoverhaul.items;

import com.tjtofvesson.mcoverhaul.Reference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTestItem extends Item {
	
	public ItemTestItem() {
		setUnlocalizedName(Reference.MCOItemsData.TESTITEM.getUnlocalizedName());
		setRegistryName(Reference.MCOItemsData.TESTITEM.getRegistryName());	
		this.setCreativeTab(CreativeTabs.COMBAT);
	}
	
	/**
     * gets the CreativeTab this item is displayed on
     */
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab()
    {
        return CreativeTabs.COMBAT;
    }
}
