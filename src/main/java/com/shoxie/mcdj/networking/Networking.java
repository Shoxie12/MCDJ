package com.shoxie.mcdj.networking;

import com.shoxie.mcdj.mcdj;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;

public class Networking {

    private static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
            new ResourceLocation(mcdj.MODID, "main"))
            .serverAcceptedVersions(( version) -> true)
            .clientAcceptedVersions(( version) -> true)
            .networkProtocolVersion(() -> "1")
            .simpleChannel();
    private static int ID = 1;

    public static void registerMessages() {
        //MGGenPacket
        INSTANCE.messageBuilder(MGGenPacket.class, ID++)
                .encoder(MGGenPacket::encode)
                .decoder(MGGenPacket::new)
                .consumerMainThread(MGGenPacket::handle)
                .add();

        //MGDiscidUpdPacket
        INSTANCE.messageBuilder(MGDiscidUpdPacket.class, ID++)
                .encoder(MGDiscidUpdPacket::encode)
                .decoder(MGDiscidUpdPacket::new)
                .consumerMainThread(MGDiscidUpdPacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.sendToServer(msg);
        //INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }
}