package com.shoxie.mcdj.networking;

import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.tile.TileMusicGenerator;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MGDiscidUpdPacket  implements IMessage {
	
    private BlockPos pos;
    private int discid;

    public MGDiscidUpdPacket() { }

    public MGDiscidUpdPacket(BlockPos pos, int discid) {
        this.pos = pos;
        this.discid = discid;
    }
    
    @Override
    public void fromBytes(ByteBuf buf) {
    	pos = BlockPos.fromLong(buf.readLong());
    	discid = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(pos.toLong());
        buf.writeInt(discid);
    }

    public static class Handler implements IMessageHandler<MGDiscidUpdPacket, IMessage> {
        @Override
        public IMessage onMessage(MGDiscidUpdPacket message, MessageContext ctx) {
            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> handle(message, ctx));
            return null;
        }

        private void handle(MGDiscidUpdPacket message, MessageContext ctx) {
            EntityPlayerMP playerEntity = ctx.getServerHandler().player;
            World world = playerEntity.getEntityWorld();
            if (world.isBlockLoaded(message.pos)) {
            	if(!(message.discid >= 0 && message.discid < ModItems.RECORDS.length)) return;
                TileMusicGenerator tile = (TileMusicGenerator)world.getTileEntity(message.pos);
                if(message.discid >= 0 && message.discid < ModItems.RECORDS.length)
                	tile.discid = message.discid;
            }
        }
    }
}