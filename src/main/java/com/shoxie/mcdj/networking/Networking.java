package com.shoxie.mcdj.networking;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class Networking {

    public static SimpleNetworkWrapper INSTANCE;
    private static int ID = 0;

    public Networking() {
    }

    public static void registerMessages(String channelName) {
        INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(channelName);
        registerMessages();
    }
    
    public static void registerMessages() {
        INSTANCE.registerMessage(
        		MGDiscidUpdPacket.Handler.class,
        		MGDiscidUpdPacket.class,
        		ID++,
        		Side.SERVER);
        
        INSTANCE.registerMessage(
        		MGGenPacket.Handler.class,
        		MGGenPacket.class,
        		ID++,
        		Side.SERVER);
    }
}