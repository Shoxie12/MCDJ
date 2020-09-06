package com.shoxie.mcdj.networking;

import java.util.function.Supplier;

import com.shoxie.mcdj.ModItems;
import com.shoxie.mcdj.item.BlankDiscItem;
import com.shoxie.mcdj.tile.MusicGeneratorTile;

import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;

public class MGGenPacket{
	
	private BlockPos pos;

	public MGGenPacket(PacketBuffer buf) {
        pos = buf.readBlockPos();
    }
	
	public MGGenPacket(BlockPos pos) {
        this.pos = pos;
    }
	
    public void toBytes(PacketBuffer buf) {
        buf.writeBlockPos(pos);
    }
	
    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
        	ServerWorld sw = ctx.get().getSender().getServerWorld();
        	MusicGeneratorTile tile = (MusicGeneratorTile)sw.getTileEntity(pos);
        	if(tile.getItemInSlot(0).getItem() instanceof BlankDiscItem && (tile.discid >= 0 && tile.discid < ModItems.RECORDS.length)) {
        		tile.StartGen();
                tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            	    h.extractItem(1, 1, false);
            	    h.insertItem(1, new ItemStack(ModItems.RECORDS[tile.discid]), false);
                });
        	}
        });
        ctx.get().setPacketHandled(true);
    }
}