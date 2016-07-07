package com.tjtofvesson.mcoverhaul.api;

import javax.annotation.Nullable;
import net.minecraft.world.World;

public interface IWorldCallback extends IGenericCallback { public void onWorldEvent(@Nullable World world); }
