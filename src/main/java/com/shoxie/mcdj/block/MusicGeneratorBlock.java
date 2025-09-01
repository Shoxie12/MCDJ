package com.shoxie.mcdj.block;

import javax.annotation.Nullable;

import com.shoxie.mcdj.Config;
import com.shoxie.mcdj.init.Init;
import com.shoxie.mcdj.mcdj;
import com.shoxie.mcdj.entity.MusicGeneratorEntity;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class MusicGeneratorBlock extends Block implements EntityBlock {
	public static final String name = "music_generator";

	public MusicGeneratorBlock(Properties properties) {
		super(properties);
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
		return new MusicGeneratorEntity(p_153215_, p_153216_);
	}

    @Override
    public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
		BlockEntity blockEntity = world.getBlockEntity(pos);

		if (!(blockEntity instanceof MusicGeneratorEntity))
			return InteractionResult.PASS;

    	if(!mcdj.musicloaded) {
    		player.sendSystemMessage(Component.translatable("message.mcdj.playlistempty"));
    	}

	    else if (Config.isMGenabled()) {
            if (player instanceof ServerPlayer sp) {
                NetworkHooks.openScreen(sp,(MenuProvider) blockEntity,pos);
            }
        }
        return InteractionResult.CONSUME;
    }
    
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
	  return type == Init.MUSIC_GENERATOR_ENTITY.get() ? MusicGeneratorEntity::tick : null;
	}

	@Override
	public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
		if (!level.isClientSide()) {
			BlockEntity _entity = level.getBlockEntity(pos);
			if (_entity instanceof MusicGeneratorEntity entity) {
				ItemStackHandler inv = entity.getInventory();
				level.addFreshEntity(
						new ItemEntity(level, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D,
								inv.getStackInSlot(0)
						)
				);
			}
		}
		super.onRemove(state, level, pos, newState, isMoving);
	}
}