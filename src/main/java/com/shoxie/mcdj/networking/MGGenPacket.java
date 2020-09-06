package com.shoxie.mcdj.networking;

import com.shoxie.mcdj.tile.TileMusicGenerator;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MGGenPacket implements IMessage {
	
    private BlockPos pos;

    public MGGenPacket() { }

    public MGGenPacket(BlockPos pos) {
        this.pos = pos;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
    	pos = BlockPos.fromLong(buf.readLong());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
    }

    public static class Handler implements IMessageHandler<MGGenPacket, IMessage> {
        @Override
        public IMessage onMessage(MGGenPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(MGGenPacket message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.getEntityWorld();
            if (world.isBlockLoaded(message.pos)) {
                TileMusicGenerator tile = (TileMusicGenerator)world.getTileEntity(message.pos);
                tile.StartGen();
            }
        }
    }
}