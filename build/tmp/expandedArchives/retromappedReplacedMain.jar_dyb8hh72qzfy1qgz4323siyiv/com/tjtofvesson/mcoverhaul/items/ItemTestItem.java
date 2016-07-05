package com.tjtofvesson.mcoverhaul.items;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tjtofvesson.mcoverhaul.Reference;
import com.tjtofvesson.mcoverhaul.utils.ChatUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
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
		func_77655_b(Reference.MCOItemsData.TESTITEM.getUnlocalizedName());
		setRegistryName(Reference.MCOItemsData.TESTITEM.getRegistryName());
		func_77637_a(Reference.ttmcoTab);
		this.field_77777_bU = 1;
	}
    
    @SideOnly(Side.CLIENT)
    public boolean onLeftClickEntity(ItemStack stack, final EntityPlayer player, final Entity entity){
    	super.onLeftClickEntity(stack, player, entity);
    	ChatUtils.sendNoSpamMessages(414, new ITextComponent(){

			@Override
			public Iterator<ITextComponent> iterator() {
				List<ITextComponent> l = new ArrayList<ITextComponent>();
				l.add(this);
				return l.iterator();
			}

			@Override
			public ITextComponent func_150255_a(Style style) {
				return this;
			}

			@Override
			public Style func_150256_b() {
				return new Style();
			}

			@Override
			public ITextComponent func_150258_a(String text) {
				return this;
			}

			@Override
			public ITextComponent func_150257_a(ITextComponent component) {
				return this;
			}

			@Override
			public String func_150261_e() {
				return "This is a "+entity.func_70005_c_();
			}

			@Override
			public String func_150260_c() {
				return "This is a "+entity.func_70005_c_();
			}

			@Override
			public String func_150254_d() {
				return "This is a "+entity.func_70005_c_();
			}

			@Override
			public List<ITextComponent> func_150253_a() {
				return new ArrayList<ITextComponent>();
			}

			@Override
			public ITextComponent func_150259_f() {
				return this;
			}
		});
    	return true;
    }
}
