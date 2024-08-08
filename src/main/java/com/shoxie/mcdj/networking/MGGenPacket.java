package com.shoxie.mcdj.networking;

import com.shoxie.mcdj.init.Init;
import com.shoxie.mcdj.item.BlankDiscItem;
import com.shoxie.mcdj.entity.MusicGeneratorEntity;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class MGGenPacket {
    private BlockPos pos;

    public MGGenPacket(BlockPos pos) {
        this.pos = pos;
    }

    public MGGenPacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public void handle(CustomPayloadEvent.Context context) {
        ServerPlayer player = context.getSender();
        if (player == null)
            return;

        ServerLevel sw = player.serverLevel();
        MusicGeneratorEntity tile = (MusicGeneratorEntity) sw.getBlockEntity(pos);
        if(tile == null) return;
        if (tile.getItemInSlot(0).getItem() instanceof BlankDiscItem && (tile.discid >= 0 && tile.discid < Init.CUSTOM_RECORD_ITEMS.size())) {
            tile.StartGen();
            tile.updatePreviewSlot();
        }
    }
}