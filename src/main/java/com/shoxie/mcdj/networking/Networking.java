package com.shoxie.mcdj.networking;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class Networking {

    public static SimpleChannel INSTANCE;
    private static int ID = 0;

    public static int nextID() {
        return ID++;
    }

    public static void registerMessages() {
        INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(com.shoxie.mcdj.mcdj.MODID, "mcdj"), () -> "1.0", s -> true, s -> true);

        INSTANCE.registerMessage(nextID(),
        		MGGenPacket.class,
        		MGGenPacket::toBytes,
        		MGGenPacket::new,
        		MGGenPacket::handle);
        
        INSTANCE.registerMessage(nextID(),
        		MGDiscidUpdPacket.class,
        		MGDiscidUpdPacket::toBytes,
        		MGDiscidUpdPacket::new,
        		MGDiscidUpdPacket::handle);
        
    }
}