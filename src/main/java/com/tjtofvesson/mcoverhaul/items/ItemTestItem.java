package com.tjtofvesson.mcoverhaul.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tjtofvesson.mcoverhaul.Reference;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTestItem extends Item {
	
	public ItemTestItem() {
		setUnlocalizedName(Reference.MCOItemsData.TESTITEM.getUnlocalizedName());
		setRegistryName(Reference.MCOItemsData.TESTITEM.getRegistryName());	
		this.setCreativeTab(CreativeTabs.COMBAT);
		this.maxStackSize = 1;
	}
	
	/**
     * gets the CreativeTab this item is displayed on
     */
    @SideOnly(Side.CLIENT)
    public CreativeTabs getCreativeTab()
    {
        return CreativeTabs.COMBAT;
    }
    
    @SideOnly(Side.CLIENT)
    public boolean onLeftClickEntity(ItemStack stack, final EntityPlayer player, final Entity entity){
    	super.onLeftClickEntity(stack, player, entity);
    	player.addChatMessage(new ITextComponent(){

			@Override
			public Iterator<ITextComponent> iterator() {
				List<ITextComponent> l = new ArrayList<ITextComponent>();
				l.add(this);
				return l.iterator();
			}

			@Override
			public ITextComponent setStyle(Style style) {
				return this;
			}

			@Override
			public Style getStyle() {
				return new Style();
			}

			@Override
			public ITextComponent appendText(String text) {
				return this;
			}

			@Override
			public ITextComponent appendSibling(ITextComponent component) {
				return this;
			}

			@Override
			public String getUnformattedComponentText() {
				return "<"+player.getName()+"> Fugg yu, "+entity.getName();
			}

			@Override
			public String getUnformattedText() {
				return "<"+player.getName()+"> Fugg yu, "+entity.getName();
			}

			@Override
			public String getFormattedText() {
				return "<"+player.getName()+"> Fugg yu, "+entity.getName();
			}

			@Override
			public List<ITextComponent> getSiblings() {
				return new ArrayList<ITextComponent>();
			}

			@Override
			public ITextComponent createCopy() {
				return this;
			}
		});
    	return true;
    }
}
