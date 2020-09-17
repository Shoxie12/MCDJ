package com.shoxie.mcdj.block;

import java.util.Random;

import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.Config;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.item.ItemBlankRecord;
import com.shoxie.mcdj.tile.TileMusicGenerator;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

public class BlockMusicGenerator extends Block implements ITileEntityProvider {
	private static String name="musicgenerator";
	public static final int ID = 1;
	static Random rn = new Random();
    public BlockMusicGenerator () {
        super(Material.IRON);
        this.setRegistryName(name);
        this.setUnlocalizedName(name);
        this.setCreativeTab(CreativeTabs.DECORATIONS);
        this.setHardness(3);
    }
    
    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileMusicGenerator();
    }
    
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
    	if(!mcdj.musicloaded) {
    		player.sendMessage(new TextComponentTranslation("message.mcdj.playlistempty"));
    		return false;
    	}
    	
    	if(Config.legacymg) {
        	if(player.getHeldItemMainhand().getItem() instanceof ItemBlankRecord) genRecord(player, hand);
        	return true;
        }
        else
        {
	    	if (world.isRemote) 
	            return true;
	        
	        TileEntity tile = world.getTileEntity(pos);
	        if (!(tile instanceof TileMusicGenerator)) 
	            return false;
	        
	        player.openGui(mcdj.instance, ID, world, pos.getX(), pos.getY(), pos.getZ());
	        
        }
        return true;
    }
    
    private void genRecord(EntityPlayer player, EnumHand hand) { 
	    if(Config.headlessmode) return;
	    else if(player.getHeldItemMainhand().getItem() instanceof ItemBlankRecord) {
	    	int randi=0;
	    	ItemStack rec = null;
	    	if (mcdj.musicloaded && !(Config.headlessmode)) {
	    		int maxrecords=ModItems.RECORDS.length;
	    		randi = rn.nextInt(maxrecords);
	    		rec = new ItemStack(ModItems.RECORDS[randi]);
	    		player.setHeldItem(hand, rec);
	    	}    	
	    }
    }
}