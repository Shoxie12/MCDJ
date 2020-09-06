package com.shoxie.mcdj.networking;

import java.util.function.Supplier;

import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.tile.MusicGeneratorTile;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class MGDiscidUpdPacket{
	
    private int discid;
	private BlockPos pos;

	public MGDiscidUpdPacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
		discid = buf.readInt();
    }
	
	public MGDiscidUpdPacket(BlockPos pos, int discid) {
        this.pos = pos;
		this.discid = discid;
    }
	
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    	buf.writeInt(discid);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ServerWorld sw = ctx.get().getSender().getServerWorld();
        	MusicGeneratorTile tile = (MusicGeneratorTile)sw.getTileEntity(pos);
        	if(discid >= 0 && discid < ModItems.RECORDS.length)
        		tile.discid = this.discid;
        });
        ctx.get().setPacketHandled(true);
    }
}