package com.shoxie.mcdj;

import com.shoxie.mcdj.block.BlockMusicGenerator;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public class ModBlocks {
	
	@ObjectHolder("mcdj:musicgenerator")
	public static Block MUSIC_GENERATOR = new BlockMusicGenerator();
}
