package com.shoxie.mcdj.networking;

import com.shoxie.mcdj.init.Init;
import com.shoxie.mcdj.entity.MusicGeneratorEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;
//import net.minecraftforge.event.server.CustomPayloadEvent;

public class MGDiscidUpdPacket {
    private int discid;
    private BlockPos pos;

    public MGDiscidUpdPacket(BlockPos pos, int discid) {
        this.discid = discid;
        this.pos = pos;
    }
    public MGDiscidUpdPacket(FriendlyByteBuf buf) {
        discid = buf.readInt();
        pos = buf.readBlockPos();
    }
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(discid);
        buf.writeBlockPos(pos);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if(player == null)
            return;

        ServerLevel sw = player.serverLevel();
        MusicGeneratorEntity tile = (MusicGeneratorEntity) sw.getBlockEntity(pos);
        if(tile == null || !(discid >= 0 && discid < Init.CUSTOM_RECORD_ITEMS.size())) return;

        tile.discid = this.discid;
        tile.updatePreviewSlot();
        tile.sendUpdates();
    }
}