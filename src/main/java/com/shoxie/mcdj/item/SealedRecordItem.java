package com.shoxie.mcdj.item;

import com.shoxie.mcdj.Lib;
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
	public InteractionResultHolder<ItemStack> use(Level p_41432_, Player p_41433_, InteractionHand p_41434_) {
		super.use(p_41432_,p_41433_,p_41434_);
		ItemStack itemstack = p_41433_.getItemInHand(p_41434_);
		return InteractionResultHolder.sidedSuccess(ItemUtils.createFilledResult(itemstack, p_41433_, Lib.getRandomMusicDisc()),p_41432_.isClientSide());
	 }

	@Override
	public InteractionResult useOn(UseOnContext p_43048_) {
		var player = p_43048_.getPlayer();
		if(player.getItemInHand(player.getUsedItemHand()).isEmpty()){
			player.setItemInHand(player.getUsedItemHand(), Lib.getRandomMusicDisc());
		}
		else player.getInventory().add(Lib.getRandomMusicDisc());
		return InteractionResult.SUCCESS;
	 }
}