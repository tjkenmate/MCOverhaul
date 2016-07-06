package com.tjtofvesson.mcoverhaul.block;

import java.util.List;

import com.tjtofvesson.mcoverhaul.Reference;

import net.minecraft.block.BlockPressurePlate;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.minecraft.block.BlockPressurePlate.Sensitivity;

public class PlayerPlate extends BlockPressurePlate{

	public PlayerPlate(){ this(null, null); }
	
	protected PlayerPlate(Material materialIn, Sensitivity sensitivityIn) {
		super(Material.field_151576_e, Sensitivity.MOBS);
		func_149663_c(Reference.MCOBlocksData.PlayerPlate.getUnlocalizedName());
		setRegistryName(Reference.MCOBlocksData.PlayerPlate.getRegistryName());
		func_149647_a(Reference.ttmcoTab);
		func_149711_c(0.5f);
		setHarvestLevel("pickaxe", 1);
	}
	
	@Override
	protected int func_180669_e(World worldIn, BlockPos pos)
    {
		List<EntityPlayer> trigger = worldIn.<EntityPlayer>func_72872_a(EntityPlayer.class, field_185511_c.func_186670_a(pos));
		if(!trigger.isEmpty()) for(EntityPlayer e : trigger) if(!e.func_145773_az()) return 15;
		return 0;
    }

}
