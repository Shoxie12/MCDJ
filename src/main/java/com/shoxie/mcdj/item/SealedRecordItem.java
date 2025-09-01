package com.shoxie.mcdj.item;

import com.shoxie.mcdj.Lib;
import com.shoxie.mcdj.init.Init;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class SealedRecordItem extends Item {
	
	public static String name = "sealed_record";
	public SealedRecordItem() {
		super(new Item.Properties());
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level p_41432_, Player player, InteractionHand p_41434_) {
		super.use(p_41432_,player,p_41434_);
        ItemStack itemstack = player.getItemInHand(p_41434_);
        if(!Init.CUSTOM_RECORD_ITEMS.isEmpty()) {

            if (itemstack.isEmpty()) {
                player.setItemInHand(player.getUsedItemHand(), Lib.getRandomMusicDisc());
            } else {
                player.getInventory().add(Lib.getRandomMusicDisc());
                itemstack.setCount(itemstack.getCount()-1);
            }
            return InteractionResultHolder.pass(itemstack);
        }
	    return InteractionResultHolder.pass(itemstack);
    }

	@Override
	public InteractionResult useOn(UseOnContext p_43048_) {
		var player = p_43048_.getPlayer();
        if(!Init.CUSTOM_RECORD_ITEMS.isEmpty()) {
            ItemStack itemstack = player.getItemInHand(player.getUsedItemHand());
            itemstack.setCount(itemstack.getCount()-1);
            if (itemstack.isEmpty()) {
                player.setItemInHand(player.getUsedItemHand(), Lib.getRandomMusicDisc());
            } else {
                player.getInventory().add(Lib.getRandomMusicDisc());
            }
            return InteractionResult.CONSUME;
        }
        else return InteractionResult.PASS;
	 }
}