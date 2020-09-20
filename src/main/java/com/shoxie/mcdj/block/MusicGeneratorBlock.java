package com.shoxie.mcdj.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.shoxie.mcdj.Config;
import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.item.BlankDiscItem;
import com.shoxie.mcdj.tile.MusicGeneratorTile;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class MusicGeneratorBlock extends Block{
	static Random rn = new Random();
	String name = "musicgenerator";
    public MusicGeneratorBlock () {
        super(Properties.create(Material.IRON)
        		.sound(SoundType.METAL)
        		.hardnessAndResistance(3.0f)
        );
        setRegistryName(name);
    }
    
    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    	return new MusicGeneratorTile();
    }
    
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult result) {
    	if(!mcdj.musicloaded) {
    		player.sendMessage(new TranslationTextComponent("message.mcdj.playlistempty"), player.getUniqueID());
    		return ActionResultType.PASS;
    	}
    	
    	if(Config.isLegacyMode()) {
        	if(player.getHeldItemMainhand().getItem() instanceof BlankDiscItem) genRecord(player, hand);
        	return ActionResultType.SUCCESS;
        }
        else
	    	if (!world.isRemote) {
	            TileEntity tileEntity = world.getTileEntity(pos);
	            if (tileEntity instanceof INamedContainerProvider) {
	                NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
	                return ActionResultType.SUCCESS;
	            }
	        }
        return ActionResultType.PASS;
    }
    
    private void genRecord(PlayerEntity player, Hand hand) { 
	    if(Config.isHeadlessMode()) return;
	    else if(player.getHeldItemMainhand().getItem() instanceof BlankDiscItem) {
	    	int randi=0;
	    	ItemStack rec = null;
	    	if (mcdj.musicloaded && !(Config.isHeadlessMode())) {
	    		int maxrecords=ModItems.RECORDS.length;
	    		randi = rn.nextInt(maxrecords);
	    		rec = new ItemStack(ModItems.RECORDS[randi]);
	    		player.setHeldItem(hand, rec);
	    	}    	
	    }
    }
}