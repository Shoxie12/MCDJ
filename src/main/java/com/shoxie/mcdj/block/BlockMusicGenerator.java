package com.shoxie.mcdj.block;

import java.util.Random;

import com.shoxie.mcdj.Items;
import com.shoxie.mcdj.Lib;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.item.ItemBlankRecord;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockMusicGenerator extends Block{
	private static String name="musicgenerator";
	static Random rn = new Random();
    public BlockMusicGenerator () {
        super(Material.IRON);
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(3);
    }
    
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {   
	    if(playerIn.getHeldItemMainhand().getItem() instanceof ItemBlankRecord) {
	    	int randi=0;
	    	ItemStack rec;
	    	if (mcdj.musicloaded) {
	    		randi = rn.nextInt(Items.RECORDS.length+12);
	    		if(randi < Items.RECORDS.length)
	    			rec = new ItemStack(Items.RECORDS[randi]);
	    		else
	    			rec = new ItemStack(Lib.getVanillaRecord(randi - Items.RECORDS.length));
	    	}
	    	else
	    	{
	    		randi = rn.nextInt(12);
	    		rec = new ItemStack(Lib.getVanillaRecord(randi));
	    	}
	    	
	    	playerIn.setHeldItem(hand, rec);
	    	return enableStats;
	    }
		return false;
    }
}