package com.shoxie.mcdj;

import com.shoxie.mcdj.tile.MusicGeneratorTile;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModTileEntities {

	@ObjectHolder("mcdj:musicgenerator")
	public static TileEntityType<MusicGeneratorTile> TILE_MG;
	
}
