package com.shoxie.mcdj.block;

import java.util.Random;

import com.shoxie.mcdj.Lib;
import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.item.ItemBlankRecord;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class BlockMusicGenerator extends Block{
	static Random rn = new Random();
	String name = "musicgenerator";
    public BlockMusicGenerator () {
        super(Properties.create(Material.IRON)
        		.sound(SoundType.METAL)
        		.hardnessAndResistance(3.0f)
        );
        setRegistryName(name);
    }
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) { 
	    if(player.getHeldItemMainhand().getItem() instanceof ItemBlankRecord) {
	    	int randi=0;
	    	ItemStack rec;
	    	if (mcdj.musicloaded) {
	    		int maxrecords=ModItems.RECORDS.length;
	    		randi = rn.nextInt(maxrecords+12);
	    		if(randi < maxrecords)
	    			rec = new ItemStack(ModItems.RECORDS[randi]);
	    		else
	    			rec = new ItemStack(Lib.getVanillaRecord(randi - maxrecords));
	    	}
	    	else
	    	{
	    		randi = rn.nextInt(12);
	    		rec = new ItemStack(Lib.getVanillaRecord(randi));
	    	}
	    	
	    	player.setHeldItem(hand, rec);
	    	return ActionResultType.SUCCESS;
	    	
	    }
	    return ActionResultType.FAIL;
    }
}