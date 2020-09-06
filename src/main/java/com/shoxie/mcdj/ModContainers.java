package com.shoxie.mcdj;

import com.shoxie.mcdj.container.MusicGeneratorContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.registries.ObjectHolder;

public class ModContainers {

	@ObjectHolder("mcdj:musicgenerator")
    public static ContainerType<MusicGeneratorContainer> CONTAINER_MUSIC_GENERATOR;
	
}
