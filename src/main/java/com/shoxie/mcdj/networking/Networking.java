package com.shoxie.mcdj.networking;

import com.shoxie.mcdj.mcdj;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.*;

public class Networking {

    private static final SimpleChannel INSTANCE = ChannelBuilder.named(
            new ResourceLocation(mcdj.MODID, "main"))
            .serverAcceptedVersions((status, version) -> true)
            .clientAcceptedVersions((status, version) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void registerMessages() {
        //MGGenPacket
        INSTANCE.messageBuilder(MGGenPacket.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(MGGenPacket::encode)
                .decoder(MGGenPacket::new)
                .consumerMainThread(MGGenPacket::handle)
                .add();

        //MGDiscidUpdPacket
        INSTANCE.messageBuilder(MGDiscidUpdPacket.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(MGDiscidUpdPacket::encode)
                .decoder(MGDiscidUpdPacket::new)
                .consumerMainThread(MGDiscidUpdPacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }
}