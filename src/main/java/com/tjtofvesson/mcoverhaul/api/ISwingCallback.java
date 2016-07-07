package com.tjtofvesson.mcoverhaul.api;

import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public interface ISwingCallback extends IGenericCallback { public void onSwing(List<EntityPlayer> players); }
